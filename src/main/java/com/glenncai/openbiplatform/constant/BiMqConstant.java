package com.glenncai.openbiplatform.constant;

/**
 * BI RabbitMQ constant
 *
 * @author Glenn Cai
 * @version 1.0 29/08/2023
 */
public final class BiMqConstant {

  /**
   * BI RabbitMQ exchange name
   */
  public static final String BI_MQ_EXCHANGE_NAME = "bi_exchange";

  /**
   * BI RabbitMQ queue name
   */
  public static final String BI_MQ_QUEUE_NAME = "bi_queue";

  /**
   * BI RabbitMQ routing key
   */
  public static final String BI_MQ_ROUTING_KEY = "bi_routing_key";

  /**
   * BI RabbitMQ direct exchange
   */
  public static final String BI_MQ_DIRECT_EXCHANGE = "direct";

  /**
   * BI RabbitMQ dead letter queue exchange name
   */
  public static final String BI_DLX_EXCHANGE_NAME = "bi_dlx_exchange";

  /**
   * BI RabbitMQ dead letter queue name
   */
  public static final String BI_DLX_QUEUE_NAME = "bi_dlx_queue";

  /**
   * BI RabbitMQ dead letter queue routing key
   */
  public static final String BI_DLX_ROUTING_KEY = "bi_dlx_routing_key";

  private BiMqConstant() {
  }
}
