package com.glenncai.openbiplatform.model.dto.chart.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for querying chart request body
 *
 * @author Glenn Cai
 * @version 1.0 07/24/2023
 */
@Data
public class ChartQueryRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = -7178353979068280540L;

  /**
   * Chart id
   */
  private Long id;

  /**
   * Chart name
   */
  private String name;

  /**
   * Goal
   */
  private String goal;

  /**
   * Chart type
   */
  private String chartType;

  /**
   * User id
   */
  private Long userId;
}
