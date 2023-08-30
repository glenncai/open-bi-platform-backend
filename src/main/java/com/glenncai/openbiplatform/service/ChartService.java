package com.glenncai.openbiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glenncai.openbiplatform.model.dto.chart.request.ChartGenByAiRequest;
import com.glenncai.openbiplatform.model.dto.chart.request.ChartUpdateStatusRequest;
import com.glenncai.openbiplatform.model.entity.Chart;
import com.glenncai.openbiplatform.model.vo.ChartVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface is for chart service
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
public interface ChartService extends IService<Chart> {

  /**
   * Generate chart by AI synchronously
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param request             http request
   * @return filtered chart info
   */
  ChartVO genChartByAiSync(MultipartFile multipartFile, ChartGenByAiRequest chartGenByAiRequest,
                           HttpServletRequest request);

  /**
   * Generate chart by AI asynchronously (Thread pool executor)
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param request             http request
   */
  void genChartByAiAsync(MultipartFile multipartFile, ChartGenByAiRequest chartGenByAiRequest,
                         HttpServletRequest request);

  /**
   * Generate chart by AI asynchronously (RabbitMQ)
   *
   * @param multipartFile       file
   * @param chartGenByAiRequest chart gen by ai request body
   * @param request             http request
   */
  void genChartByAiAsyncMq(MultipartFile multipartFile, ChartGenByAiRequest chartGenByAiRequest,
                           HttpServletRequest request);

  /**
   * Updates chart status
   *
   * @param chartUpdateStatusRequest chart update status request body
   */
  void updateChartStatus(ChartUpdateStatusRequest chartUpdateStatusRequest);
}
