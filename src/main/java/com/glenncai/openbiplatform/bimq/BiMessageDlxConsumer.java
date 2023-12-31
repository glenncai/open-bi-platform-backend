package com.glenncai.openbiplatform.bimq;

import com.glenncai.openbiplatform.constant.AiConstant;
import com.glenncai.openbiplatform.constant.BiMqConstant;
import com.glenncai.openbiplatform.constant.ChartConstant;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.exception.enums.AiExceptionEnum;
import com.glenncai.openbiplatform.exception.enums.MqExceptionEnum;
import com.glenncai.openbiplatform.manager.AiManager;
import com.glenncai.openbiplatform.model.dto.ai.request.ChatRequest;
import com.glenncai.openbiplatform.model.dto.chart.request.ChartUpdateStatusRequest;
import com.glenncai.openbiplatform.model.entity.Chart;
import com.glenncai.openbiplatform.model.enums.ChartStatusEnum;
import com.glenncai.openbiplatform.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import javax.annotation.Resource;

/**
 * BI RabbitMQ dead letter queue consumer
 *
 * @author Glenn Cai
 * @version 1.0 30/08/2023
 */
@Component
@Slf4j
public class BiMessageDlxConsumer {

  @Resource
  private ChartService chartService;

  @Resource
  private AiManager aiManager;

  @RabbitListener(queues = {BiMqConstant.BI_DLX_USER_QUEUE_NAME,
      BiMqConstant.BI_DLX_ADMIN_QUEUE_NAME},
      ackMode = "MANUAL")
  public void receiveMessage(String message, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
      throws IOException {
    // Receive message from dead letter queue
    log.warn("RabbitMQ received message from dead letter queue: {}", message);

    if (StringUtils.isBlank(message)) {
      // Reject message when message is empty
      channel.basicNack(deliveryTag, false, false);
      throw new BusinessException(MqExceptionEnum.MQ_MESSAGE_EMPTY_ERROR.getCode(),
                                  MqExceptionEnum.MQ_MESSAGE_EMPTY_ERROR.getMessage());
    }

    // Get the corresponding chart id from the message
    long chartId = Long.parseLong(message);
    // Get the chart data
    Chart chart = chartService.getById(chartId);
    ChatRequest chatRequest = new ChatRequest();
    ChartUpdateStatusRequest chartUpdateStatusRequest = new ChartUpdateStatusRequest();

    // If the chart data is empty, reject the message
    if (chart == null) {
      channel.basicNack(deliveryTag, false, false);
      throw new BusinessException(MqExceptionEnum.MQ_CHART_DATA_EMPTY_ERROR.getCode(),
                                  MqExceptionEnum.MQ_CHART_DATA_EMPTY_ERROR.getMessage());
    }

    // Update chart status to running
    Chart updateChartRunning = new Chart();
    updateChartRunning.setId(chartId);
    updateChartRunning.setStatus(ChartStatusEnum.RUNNING.getValue());
    boolean updateChartRunningResult = chartService.updateById(updateChartRunning);
    if (!updateChartRunningResult) {
      // If update failed, reject the message
      channel.basicNack(deliveryTag, false, false);
      chartUpdateStatusRequest.setId(chartId);
      chartUpdateStatusRequest.setStatus(ChartStatusEnum.FAILED);
      chartService.updateChartStatus(chartUpdateStatusRequest);
    }

    // Call AI service to generate chart
    chatRequest.setMessage(chart.getExecMessage());
    chatRequest.setKey(AiConstant.AI_API_KEY_VALUE);
    String chartResult = aiManager.doAiChat(chatRequest);

    // Parse the result
    String[] splitChartResult = chartResult.split(ChartConstant.CHART_CONCLUSION_DELIMITER);

    if (splitChartResult.length < ChartConstant.CHART_CONCLUSION_SPLIT_LENGTH) {
      // If the result split length is not valid, reject the message
      channel.basicNack(deliveryTag, false, false);
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
    boolean updateSucceedChartResult = chartService.updateById(updateSucceedChart);
    if (!updateSucceedChartResult) {
      // If update failed, reject the message
      channel.basicNack(deliveryTag, false, false);
      chartUpdateStatusRequest.setId(chart.getId());
      chartUpdateStatusRequest.setStatus(ChartStatusEnum.FAILED);
      chartService.updateChartStatus(chartUpdateStatusRequest);
    }

    // Everything is ok, ack the message
    channel.basicAck(deliveryTag, false);
  }
}
