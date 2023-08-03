package com.glenncai.openbiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glenncai.openbiplatform.config.registry.AiThreadPoolExecutorRegistry;
import com.glenncai.openbiplatform.constant.AiConstant;
import com.glenncai.openbiplatform.constant.ChartConstant;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.exception.enums.AiExceptionEnum;
import com.glenncai.openbiplatform.exception.enums.ChartExceptionEnum;
import com.glenncai.openbiplatform.manager.AiManager;
import com.glenncai.openbiplatform.manager.RedisLimiterManager;
import com.glenncai.openbiplatform.mapper.ChartMapper;
import com.glenncai.openbiplatform.model.dto.ai.request.ChatRequest;
import com.glenncai.openbiplatform.model.dto.chart.request.ChartGenByAiRequest;
import com.glenncai.openbiplatform.model.dto.chart.request.ChartUpdateStatusRequest;
import com.glenncai.openbiplatform.model.entity.Chart;
import com.glenncai.openbiplatform.model.entity.User;
import com.glenncai.openbiplatform.model.enums.ChartStatusEnum;
import com.glenncai.openbiplatform.model.vo.ChartVO;
import com.glenncai.openbiplatform.service.ChartService;
import com.glenncai.openbiplatform.service.IpLimitService;
import com.glenncai.openbiplatform.service.UserService;
import com.glenncai.openbiplatform.utils.ChartUtils;
import com.glenncai.openbiplatform.utils.ExcelUtils;
import com.glenncai.openbiplatform.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is for chart service implementation
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

  @Resource
  private UserService userService;

  @Resource
  private IpLimitService ipLimitService;

  @Resource
  private AiManager aiManager;

  @Resource
  private RedisLimiterManager redisLimiterManager;

  /**
   * Generate chart by AI synchronously
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param request             http request
   * @return filtered chart info
   */
  @Override
  public ChartVO genChartByAiSync(MultipartFile multipartFile,
                                  ChartGenByAiRequest chartGenByAiRequest,
                                  HttpServletRequest request) {
    String originalFilename = multipartFile.getOriginalFilename();
    String chartName = chartGenByAiRequest.getName();
    String chartType = chartGenByAiRequest.getChartType();
    String goal = chartGenByAiRequest.getGoal();
    ChatRequest chatRequest = new ChatRequest();

    User currentLoginUser = userService.getCurrentLoginUser(request);

    // Rate limit for current user in this method, use 'genChartByAi_' and user id as identifier
    redisLimiterManager.doRateLimit("genChartByAi_" + currentLoginUser.getId());

    boolean hasCallQuota = ipLimitService.hasRemainingCallCount(currentLoginUser.getId());
    if (!hasCallQuota) {
      throw new BusinessException(ChartExceptionEnum.CHART_NO_QUOTA_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_NO_QUOTA_ERROR.getMessage());
    }

    if (StringUtils.isBlank(goal)) {
      throw new BusinessException(ChartExceptionEnum.CHART_EMPTY_GOAL_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_EMPTY_GOAL_ERROR.getMessage());
    }
    if (FileUtils.isInvalidFileSize(multipartFile, ChartConstant.FILE_MAX_SIZE, "M")) {
      throw new BusinessException(ChartExceptionEnum.CHART_FILE_SIZE_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_FILE_SIZE_ERROR.getMessage());
    }
    if (FileUtils.isInvalidFileExtension(multipartFile.getOriginalFilename(),
                                         ChartConstant.VALID_FILE_EXTENSIONS)) {
      throw new BusinessException(ChartExceptionEnum.CHART_FILE_EXTENSION_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_FILE_EXTENSION_ERROR.getMessage());
    }

    StringJoiner userInput = new StringJoiner("\n");
    userInput.add("Analysis needs:");
    String userGoal = goal;
    if (StringUtils.isNotBlank(chartType)) {
      userGoal += " , please generate a " + chartType + " chart.";
    }
    userInput.add(userGoal);
    userInput.add("Raw data:");

    // Compressed data
    String csvData = ExcelUtils.excelToCsv(multipartFile);
    userInput.add(csvData);

    chatRequest.setKey(AiConstant.AI_API_KEY_VALUE);
    chatRequest.setMessage(userInput.toString());

    // Call AI service to generate chart
    String chartResult = aiManager.doAiChat(chatRequest);

    // Parse result
    String[] splitChartResult = chartResult.split(ChartConstant.CHART_CONCLUSION_DELIMITER);

    if (splitChartResult.length < ChartConstant.CHART_CONCLUSION_SPLIT_LENGTH) {
      throw new BusinessException(AiExceptionEnum.AI_RESPONSE_FORMAT_ERROR.getCode(),
                                  AiExceptionEnum.AI_RESPONSE_FORMAT_ERROR.getMessage());
    }

    String chartCode = splitChartResult[1].trim();
    String chartConclusion = splitChartResult[2].trim();

    if (StringUtils.isBlank(chartType)) {
      chartType = "auto";
    }

    // Insert chart info into database
    Chart chart = new Chart();
    chartName = ChartUtils.genChartNameAuto(chartName, originalFilename);
    chart.setGoal(userGoal);
    chart.setName(chartName);
    chart.setChartData(csvData);
    chart.setChartType(chartType);
    chart.setGenChartData(chartCode);
    chart.setGenChartConclusion(chartConclusion);
    chart.setStatus(ChartStatusEnum.SUCCEED.getValue());
    chart.setExecMessage(userInput.toString());
    chart.setUserId(currentLoginUser.getId());
    boolean saveChartResult = this.save(chart);
    if (!saveChartResult) {
      throw new BusinessException(ChartExceptionEnum.CHART_SAVE_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_SAVE_ERROR.getMessage());
    }

    ChartVO chartVO = new ChartVO();
    BeanUtils.copyProperties(chart, chartVO);
    chartVO.setChartId(chart.getId());

    // Update call count quota for current user
    ipLimitService.increaseCallCount(currentLoginUser.getId());

    return chartVO;
  }

  /**
   * Generate chart by AI asynchronously (Thread pool executor)
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param request             http request
   */
  @Override
  public void genChartByAiAsync(MultipartFile multipartFile,
                                ChartGenByAiRequest chartGenByAiRequest,
                                HttpServletRequest request) {
    String originalFilename = multipartFile.getOriginalFilename();
    String chartName = chartGenByAiRequest.getName();
    String chartType = chartGenByAiRequest.getChartType();
    String goal = chartGenByAiRequest.getGoal();
    ChatRequest chatRequest = new ChatRequest();
    ChartUpdateStatusRequest chartUpdateStatusRequest = new ChartUpdateStatusRequest();

    User currentLoginUser = userService.getCurrentLoginUser(request);
    String currentUserRole = currentLoginUser.getRole();

    // Rate limit for current user in this method, use 'genChartByAi_' and user id as identifier
    redisLimiterManager.doRateLimit("genChartByAi_" + currentLoginUser.getId());

    boolean hasCallQuota = ipLimitService.hasRemainingCallCount(currentLoginUser.getId());
    if (!hasCallQuota) {
      throw new BusinessException(ChartExceptionEnum.CHART_NO_QUOTA_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_NO_QUOTA_ERROR.getMessage());
    }

    if (StringUtils.isBlank(goal)) {
      throw new BusinessException(ChartExceptionEnum.CHART_EMPTY_GOAL_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_EMPTY_GOAL_ERROR.getMessage());
    }
    if (FileUtils.isInvalidFileSize(multipartFile, ChartConstant.FILE_MAX_SIZE, "M")) {
      throw new BusinessException(ChartExceptionEnum.CHART_FILE_SIZE_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_FILE_SIZE_ERROR.getMessage());
    }
    if (FileUtils.isInvalidFileExtension(multipartFile.getOriginalFilename(),
                                         ChartConstant.VALID_FILE_EXTENSIONS)) {
      throw new BusinessException(ChartExceptionEnum.CHART_FILE_EXTENSION_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_FILE_EXTENSION_ERROR.getMessage());
    }

    StringJoiner userInput = new StringJoiner("\n");
    userInput.add("Analysis needs:");
    String userGoal = goal;
    if (StringUtils.isNotBlank(chartType)) {
      userGoal += " , please generate a " + chartType + " chart.";
    }
    userInput.add(userGoal);
    userInput.add("Raw data:");

    // Compressed data
    String csvData = ExcelUtils.excelToCsv(multipartFile);
    userInput.add(csvData);

    chatRequest.setKey(AiConstant.AI_API_KEY_VALUE);
    chatRequest.setMessage(userInput.toString());

    // Insert waiting chart info into database
    Chart chart = new Chart();
    chartName = ChartUtils.genChartNameAuto(chartName, originalFilename);
    chart.setGoal(userGoal);
    chart.setName(chartName);
    chart.setChartData(csvData);
    chart.setChartType(StringUtils.isBlank(chartType) ? "auto" : chartType);
    chart.setStatus(ChartStatusEnum.WAITING.getValue());
    chart.setExecMessage(userInput.toString());
    chart.setUserId(currentLoginUser.getId());
    boolean saveChartWaitingResult = this.save(chart);
    if (!saveChartWaitingResult) {
      throw new BusinessException(ChartExceptionEnum.CHART_SAVE_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_SAVE_ERROR.getMessage());
    }

    // Check thread pool executor queue size based on current user role
    ThreadPoolExecutor aiThreadPoolExecutor =
        AiThreadPoolExecutorRegistry.getAiThreadPoolExecutor(currentUserRole);
    int queueSize = aiThreadPoolExecutor.getQueue().size();
    int maxQueueSize = aiThreadPoolExecutor.getQueue().remainingCapacity();
    if (queueSize >= maxQueueSize) {
      throw new BusinessException(ChartExceptionEnum.CHART_AI_THREAD_POOL_FULL_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_AI_THREAD_POOL_FULL_ERROR.getMessage());
    }

    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
      // Update running chart info into database
      chartUpdateStatusRequest.setId(chart.getId());
      chartUpdateStatusRequest.setStatus(ChartStatusEnum.RUNNING);
      updateChartStatus(chartUpdateStatusRequest);

      // Call AI service to generate chart
      String chartResult = aiManager.doAiChat(chatRequest);

      // Parse result
      String[] splitChartResult = chartResult.split(ChartConstant.CHART_CONCLUSION_DELIMITER);

      if (splitChartResult.length < ChartConstant.CHART_CONCLUSION_SPLIT_LENGTH) {
        throw new BusinessException(AiExceptionEnum.AI_RESPONSE_FORMAT_ERROR.getCode(),
                                    AiExceptionEnum.AI_RESPONSE_FORMAT_ERROR.getMessage());
      }

      String chartCode = splitChartResult[1].trim();
      String chartConclusion = splitChartResult[2].trim();

      // Update success chart info into database
      Chart updateSucceedChart = new Chart();
      updateSucceedChart.setId(chart.getId());
      updateSucceedChart.setGenChartData(chartCode);
      updateSucceedChart.setGenChartConclusion(chartConclusion);
      updateSucceedChart.setStatus(ChartStatusEnum.SUCCEED.getValue());
      boolean updateSucceedChartResult = this.updateById(updateSucceedChart);
      if (!updateSucceedChartResult) {
        chartUpdateStatusRequest.setId(chart.getId());
        chartUpdateStatusRequest.setStatus(ChartStatusEnum.FAILED);
        this.updateChartStatus(chartUpdateStatusRequest);
      }
    }, aiThreadPoolExecutor);

    try {
      completableFuture.get(10, TimeUnit.MINUTES);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      chartUpdateStatusRequest.setId(chart.getId());
      chartUpdateStatusRequest.setStatus(ChartStatusEnum.FAILED);
      this.updateChartStatus(chartUpdateStatusRequest);
      Thread.currentThread().interrupt();
      throw new BusinessException(ChartExceptionEnum.CHART_AI_THREAD_POOL_FULL_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_AI_THREAD_POOL_FULL_ERROR.getMessage());
    }

    // Update call count quota for current user
    ipLimitService.increaseCallCount(currentLoginUser.getId());
  }

  /**
   * Updates chart status
   *
   * @param chartUpdateStatusRequest chart update status request body
   */
  @Override
  public void updateChartStatus(ChartUpdateStatusRequest chartUpdateStatusRequest) {
    Chart chart = new Chart();
    chart.setId(chartUpdateStatusRequest.getId());
    chart.setStatus(chartUpdateStatusRequest.getStatus().getValue());
    boolean updateChartStatusResult = this.updateById(chart);
    if (!updateChartStatusResult) {
      throw new BusinessException(ChartExceptionEnum.CHART_SAVE_ERROR.getCode(),
                                  ChartExceptionEnum.CHART_SAVE_ERROR.getMessage());
    }
  }
}




