package com.glenncai.openbiplatform.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FanoutProducer {

  // Define the name of the exchange
  private static final String EXCHANGE_NAME = "fanout-exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      // Declare the fanout exchange
      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

      Scanner scanner = new Scanner(System.in);
      while (scanner.hasNext()) {
        String message = scanner.nextLine();
        // Publish the message to the fanout exchange
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");
      }
    }
  }
}