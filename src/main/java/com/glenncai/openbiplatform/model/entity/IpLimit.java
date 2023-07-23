package com.glenncai.openbiplatform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is for table t_ip_limit entity
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@TableName(value = "t_ip_limit")
@Data
public class IpLimit implements Serializable {

  @Serial
  @TableField(exist = false)
  private static final long serialVersionUID = 1L;

  /**
   * IP
   */
  @TableId(type = IdType.INPUT)
  private String ip;

  /**
   * Call API service count today
   */
  private Integer callCountToday;

  /**
   * Last call API service date
   */
  private Date lastCallDate;
}