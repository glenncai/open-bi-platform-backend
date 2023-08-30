package com.glenncai.openbiplatform.bimq;

import com.glenncai.openbiplatform.constant.BiMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * BI RabbitMQ producer
 *
 * @author Glenn Cai
 * @version 1.0 29/08/2023
 */
@Component
public class BiMessageProducer {

  @Resource
  private RabbitTemplate rabbitTemplate;

  /**
   * Send message
   *
   * @param message    message
   * @param routingKey routing key
   */
  public void sendMessage(String message, String routingKey) {
    rabbitTemplate.convertAndSend(BiMqConstant.BI_MQ_EXCHANGE_NAME, routingKey, message);
  }
}
