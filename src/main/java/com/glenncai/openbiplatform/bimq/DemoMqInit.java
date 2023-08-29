package com.glenncai.openbiplatform.bimq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Demo for creating a RabbitMQ message queue and exchange (Only run once)
 *
 * @author Glenn Cai
 * @version 1.0 29/08/2023
 */
public class DemoMqInit {

  public static void main(String[] args) {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();
      String EXCHANGE_NAME = "demo_exchange";
      channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

      // Create a queue
      String QUEUE_NAME = "demo_queue";
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "demo_routing_key");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
