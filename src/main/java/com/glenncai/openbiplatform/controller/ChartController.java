package com.glenncai.openbiplatform.controller;

import com.glenncai.openbiplatform.common.BaseResponse;
import com.glenncai.openbiplatform.common.BaseResult;
import com.glenncai.openbiplatform.model.dto.chart.request.ChartGenByAiRequest;
import com.glenncai.openbiplatform.model.vo.ChartVO;
import com.glenncai.openbiplatform.service.ChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is for chart controller (API)
 *
 * @author Glenn Cai
 * @version 1.0 07/25/2023
 */
@Api(tags = "Chart Controller")
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

  @Resource
  private ChartService chartService;

  /**
   * Chart generate by AI synchronously api
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param httpServletRequest  http request
   * @return filtered chart info
   */
  @ApiOperation(value = "Generate chart by AI sync")
  @PostMapping("/gen/sync")
  public BaseResponse<ChartVO> genChartByAiSync(@RequestPart("file") MultipartFile multipartFile,
                                                ChartGenByAiRequest chartGenByAiRequest,
                                                HttpServletRequest httpServletRequest) {
    ChartVO chartVO = chartService.genChartByAiSync(multipartFile, chartGenByAiRequest,
                                                    httpServletRequest);
    return BaseResult.success(chartVO);
  }

  /**
   * Chart generate by AI asynchronously (Thread pool executor) api
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param httpServletRequest  http request
   */
  @ApiOperation(value = "Generate chart by AI async")
  @PostMapping("/gen/async")
  public void genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                ChartGenByAiRequest chartGenByAiRequest,
                                HttpServletRequest httpServletRequest) {
    chartService.genChartByAiAsync(multipartFile, chartGenByAiRequest,
                                   httpServletRequest);
  }

  /**
   * Chart generate by AI asynchronously (RabbitMQ) api
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param httpServletRequest  http request
   */
  @ApiOperation(value = "Generate chart by AI async with RabbitMQ")
  @PostMapping("/gen/async/mq")
  public void genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                  ChartGenByAiRequest chartGenByAiRequest,
                                  HttpServletRequest httpServletRequest) {
    chartService.genChartByAiAsyncMq(multipartFile, chartGenByAiRequest,
                                     httpServletRequest);
  }
}
