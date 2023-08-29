package com.glenncai.openbiplatform.bimq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * RabbitMQ message consumer
 *
 * @author Glenn Cai
 * @version 1.0 29/08/2023
 */
@Component
@Slf4j
public class DemoMessageConsumer {

  @RabbitListener(queues = {"demo_queue"}, ackMode = "MANUAL")
  private void receiveMessage(String message, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
    log.info("Received message: {}", message);
    try {
      channel.basicAck(deliveryTag, false);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
