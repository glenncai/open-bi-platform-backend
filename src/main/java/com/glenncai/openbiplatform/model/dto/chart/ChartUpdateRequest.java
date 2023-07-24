package com.glenncai.openbiplatform.model.dto.chart;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is for updating chart request body
 *
 * @author Glenn Cai
 * @version 1.0 07/24/2023
 */
@Data
public class ChartUpdateRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = -5370052547887204853L;

  /**
   * Chart id
   */
  @TableId(type = IdType.ASSIGN_ID)
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

  /**
   * Generate chart data
   */
  private String genChartData;

  /**
   * Generate chart conclusion
   */
  private String genChartConclusion;

  /**
   * User id
   */
  private Long userId;

  /**
   * Created time
   */
  private Date createdAt;

  /**
   * Updated time
   */
  private Date updatedAt;

  /**
   * Logic delete
   */
  @TableLogic
  private Integer valid;
}
