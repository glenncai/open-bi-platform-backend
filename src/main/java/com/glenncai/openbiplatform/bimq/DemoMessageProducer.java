package com.glenncai.openbiplatform.bimq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ message producer
 *
 * @author Glenn Cai
 * @version 1.0 29/08/2023
 */
@Component
public class DemoMessageProducer {

  @Resource
  private RabbitTemplate rabbitTemplate;

  /**
   * Send message
   *
   * @param exchange   exchange
   * @param routingKey routing key
   * @param message    message
   */
  public void sendMessage(String exchange, String routingKey, String message) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }
}
