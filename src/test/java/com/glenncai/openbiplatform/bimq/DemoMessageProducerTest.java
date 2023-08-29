package com.glenncai.openbiplatform.bimq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * RabbitMQ demo test
 *
 * @author Glenn Cai
 * @version 1.0 29/08/2023
 */
@SpringBootTest
class DemoMessageProducerTest {

  @Resource
  private DemoMessageProducer demoMessageProducer;

  @Test
  void sendMessage() {
    demoMessageProducer.sendMessage("demo_exchange", "demo_routing_key", "Hello RabbitMQ!");
  }
}