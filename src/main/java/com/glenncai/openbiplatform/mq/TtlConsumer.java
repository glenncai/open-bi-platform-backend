package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * TTL single consumer demo
 *
 * @author Glenn Cai
 * @version 1.0 28/8/2023
 */
public class TtlConsumer {

  private static final String QUEUE_NAME = "ttl_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    // Create a queue with TTL
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("x-message-ttl", 5000);
    channel.queueDeclare(QUEUE_NAME, false, false, false, args);

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    // Define how to handle the message
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [x] Received '" + message + "'");
    };

    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
    });
  }
}
