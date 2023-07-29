package com.glenncai.openbiplatform.model.dto.chart.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for adding chart request body
 *
 * @author Glenn Cai
 * @version 1.0 07/24/2023
 */
@Data
public class ChartAddRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 164856469614660001L;

  /**
   * Chart name
   */
  private String name;

  /**
   * Goal
   */
  private String goal;

  /**
   * Chart data
   */
  private String chartData;

  /**
   * Chart type
   */
  private String chartType;
}
