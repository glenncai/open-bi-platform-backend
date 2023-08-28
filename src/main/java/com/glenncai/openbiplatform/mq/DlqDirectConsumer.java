package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Dead letter queue direct consumer
 *
 * @author Glenn Cai
 * @version 1.0 28/08/2023
 */
public class DlqDirectConsumer {

  private static final String DEAD_EXCHANGE_NAME = "dlx-direct-exchange";
  private static final String WORK_EXCHANGE_NAME = "direct-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(WORK_EXCHANGE_NAME, "direct");

    // Define the dead letter queue parameter
    Map<String, Object> args1 = new HashMap<>();

    // Link the dead letter exchange
    args1.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
    // Link the dead letter routing key
    args1.put("x-dead-letter-routing-key", "out-source1");

    // Create the queue
    String queueName1 = "user_queue";
    channel.queueDeclare(queueName1, true, false, false, args1);
    channel.queueBind(queueName1, WORK_EXCHANGE_NAME, "user");

    // Define the dead letter queue parameter
    Map<String, Object> args2 = new HashMap<>();

    // Link the dead letter exchange
    args2.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
    // Link the dead letter routing key
    args2.put("x-dead-letter-routing-key", "out-source2");

    // Create the queue
    String queueName2 = "admin_queue";
    channel.queueDeclare(queueName2, true, false, false, args2);
    channel.queueBind(queueName2, WORK_EXCHANGE_NAME, "admin");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback userDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      // Simulate reject message manually
      channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
      System.out.println(
          " [user] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    DeliverCallback adminDeliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      // Simulate reject message manually
      channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
      System.out.println(
          " [admin] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };

    // Consume message from user_queue
    channel.basicConsume(queueName1, false, userDeliverCallback, consumerTag -> {
    });
    // Consume message from admin_queue
    channel.basicConsume(queueName2, false, adminDeliverCallback, consumerTag -> {
    });
  }
}
