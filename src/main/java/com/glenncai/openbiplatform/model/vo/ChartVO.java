package com.glenncai.openbiplatform.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for chart view object
 *
 * @author Glenn Cai
 * @version 1.0 07/25/2023
 */
@Data
public class ChartVO implements Serializable {

  @Serial
  private static final long serialVersionUID = 2656454462744095473L;

  /**
   * Analysis target
   */
  private String goal;

  /**
   * Chart name
   */
  private String name;

  /**
   * Chart data
   */
  private String chartData;

  /**
   * Chart type
   */
  private String chartType;

  /**
   * Generated chart data
   */
  private String genChartData;

  /**
   * Generated chart conclusion
   */
  private String genChartConclusion;

  /**
   * 0: Waiting, 1: Running, 2: Succeed, 3: Failed
   */
  private int status;

  /**
   * New chart id
   */
  private Long chartId;
}
