package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class DirectConsumer {

  private static final String EXCHANGE_NAME = "direct-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, "direct");

    // Create user queue
    String queueName1 = "user_queue";
    channel.queueDeclare(queueName1, true, false, false, null);
    channel.queueBind(queueName1, EXCHANGE_NAME, "user");

    // Create admin queue
    String queueName2 = "admin_queue";
    channel.queueDeclare(queueName2, true, false, false, null);
    channel.queueBind(queueName2, EXCHANGE_NAME, "admin");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback userDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(
          " [user] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    DeliverCallback adminDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(
          " [admin] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    channel.basicConsume(queueName1, true, userDeliverCallback, consumerTag -> {
    });
    channel.basicConsume(queueName2, true, adminDeliverCallback, consumerTag -> {
    });
  }
}