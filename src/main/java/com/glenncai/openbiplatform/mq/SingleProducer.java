package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * RabbitMQ "Hello World!" demo
 *
 * @author Glenn Cai
 * @version 1.0 24/08/2023
 */
public class SingleProducer {

  private static final String QUEUE_NAME = "hello";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    // Create a connection to the server
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      // Declare a queue
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      // Send a message
      String message = "Hello World!";
      channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '" + message + "'");
    }
  }
}