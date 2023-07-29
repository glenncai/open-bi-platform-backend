package com.glenncai.openbiplatform.model.dto.chart.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for editing chart request body
 *
 * @author Glenn Cai
 * @version 1.0 07/24/2023
 */
@Data
public class ChartEditRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = -9092786050706527465L;

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
   * Chart data
   */
  private String chartData;

  /**
   * Chart type
   */
  private String chartType;
}
