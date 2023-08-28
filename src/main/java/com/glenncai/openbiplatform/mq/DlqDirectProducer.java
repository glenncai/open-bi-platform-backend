package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Dead letter queue direct producer
 *
 * @author Glenn Cai
 * @version 1.0 28/08/2023
 */
public class DlqDirectProducer {

  private static final String DEAD_EXCHANGE_NAME = "dlx-direct-exchange";
  private static final String WORK_EXCHANGE_NAME = "direct-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()
    ) {
      // Declare dead letter exchange
      channel.exchangeDeclare(DEAD_EXCHANGE_NAME, "direct");

      // Create dead letter queue
      String queueName1 = "out-source1-queue";
      channel.queueDeclare(queueName1, true, false, false, null);
      channel.queueBind(queueName1, DEAD_EXCHANGE_NAME, "out-source1");

      String queueName2 = "out-source2-queue";
      channel.queueDeclare(queueName2, true, false, false, null);
      channel.queueBind(queueName2, DEAD_EXCHANGE_NAME, "out-source2");

      DeliverCallback outsource1DeliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        // Simulate reject message manually
        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        System.out.println(
            " [out-source1] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message +
                "'");
      };

      DeliverCallback outsource2DeliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        // Simulate reject message manually
        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
        System.out.println(
            " [out-source2] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message +
                "'");
      };

      // Consume message from out-source1-queue
      channel.basicConsume(queueName1, false, outsource1DeliverCallback, consumerTag -> {
      });
      // Consume message from out-source2-queue
      channel.basicConsume(queueName2, false, outsource2DeliverCallback, consumerTag -> {
      });

      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNext()) {
        String userInput = scanner.nextLine();
        String[] strings = userInput.split(" ");
        if (strings.length < 1) {
          continue;
        }
        String message = strings[0];
        String routingKey = strings[1];

        channel.basicPublish(WORK_EXCHANGE_NAME, routingKey, null,
                             message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + " with routing:" + routingKey + "'");
      }
    }
  }
}
