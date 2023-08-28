package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * TTL single producer demo
 *
 * @author Glenn Cai
 * @version 1.0 24/08/2023
 */
public class TtlProducer {

  private static final String QUEUE_NAME = "ttl_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    // Create a connection to the server
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      String message = "Hello World!";

      // Set TTL for the message
      AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
          .expiration("1000")
          .build();
      channel.basicPublish("my-exchange", "routing-key", properties,
                           message.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '" + message + "'");

    }
  }
}