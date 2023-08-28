package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class MultiConsumer {

  private static final String TASK_QUEUE_NAME = "multi_queue";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    final Connection connection = factory.newConnection();

    for (int i = 0; i < 2; i++) {
      final Channel channel = connection.createChannel();
      channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

      // Set the prefetch count to 1, so that RabbitMQ will wait for the previous message to be
      // acknowledged before giving the consumer a new message
      channel.basicQos(1);

      int finalI = i;

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        // Convert the received message to String
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        try {
          System.out.println(" [x] Received ' Number" + finalI + ":" + message + "'");
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          // Mock the time-consuming task
          Thread.sleep(20000);
        } catch (InterruptedException e) {
          e.printStackTrace();
          channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        } finally {
          System.out.println(" [x] Done");
          // Send a proper acknowledgment from the worker, once we're done with a task
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
      };
      // Start consuming messages
      channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
      });
    }
  }
}