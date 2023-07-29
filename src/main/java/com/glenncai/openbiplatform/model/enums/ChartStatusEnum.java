package com.glenncai.openbiplatform.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Chart status enum
 *
 * @author Glenn Cai
 * @version 1.0 07/28/2023
 */
public enum ChartStatusEnum {

  WAITING("Waiting", "waiting"),
  RUNNING("Running", "running"),
  SUCCESS("Success", "success"),
  FAILED("Failed", "failed");

  private final String text;
  private final String value;

  ChartStatusEnum(String text, String value) {
    this.text = text;
    this.value = value;
  }

  /**
   * Get all values
   *
   * @return all chart status values
   */
  public static List<String> getValues() {
    return Arrays.stream(ChartStatusEnum.values()).map(ChartStatusEnum::getValue).toList();
  }

  /**
   * Get enum by value
   *
   * @param value status value
   * @return chart status enum
   */
  public static ChartStatusEnum getEnumByValue(String value) {
    return Arrays.stream(ChartStatusEnum.values())
                 .filter(statusEnum -> statusEnum.getValue().equals(value))
                 .findFirst()
                 .orElse(null);
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }
}
