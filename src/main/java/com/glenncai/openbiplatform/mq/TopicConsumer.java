package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class TopicConsumer {

  private static final String EXCHANGE_NAME = "topic-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, "topic");

    // Create frontend queue
    String queueName1 = "frontend_queue";
    channel.queueDeclare(queueName1, true, false, false, null);
    channel.queueBind(queueName1, EXCHANGE_NAME, "#.frontend.#");

    // Create backend queue
    String queueName2 = "backend_queue";
    channel.queueDeclare(queueName2, true, false, false, null);
    channel.queueBind(queueName2, EXCHANGE_NAME, "#.backend.#");

    // Create product queue
    String queueName3 = "product_queue";
    channel.queueDeclare(queueName3, true, false, false, null);
    channel.queueBind(queueName3, EXCHANGE_NAME, "#.product.#");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback groupaDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [Group A] Received '" +
                             delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    DeliverCallback groupbDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [Group B] Received '" +
                             delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    DeliverCallback groupcDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [Group C] Received '" +
                             delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    channel.basicConsume(queueName1, true, groupaDeliverCallback, consumerTag -> {
    });
    channel.basicConsume(queueName2, true, groupbDeliverCallback, consumerTag -> {
    });
    channel.basicConsume(queueName3, true, groupcDeliverCallback, consumerTag -> {
    });
  }
}