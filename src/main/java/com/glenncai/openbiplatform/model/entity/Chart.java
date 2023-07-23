package com.glenncai.openbiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is for table t_chart entity
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@TableName(value = "t_chart")
@Data
public class Chart implements Serializable {

  @Serial
  @TableField(exist = false)
  private static final long serialVersionUID = 1L;

  /**
   * Primary key
   */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

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
   * waiting / running / succeed / failed
   */
  private String status;

  /**
   * Execution message
   */
  private String execMessage;

  /**
   * User ID
   */
  private Long userId;

  /**
   * 0: invalid, 1: valid
   */
  @TableLogic
  private Integer valid;

  /**
   * Created time
   */
  private Date createdAt;

  /**
   * Updated time
   */
  private Date updatedAt;
}