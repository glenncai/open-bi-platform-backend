package com.glenncai.openbiplatform.exception.enums;

import com.glenncai.openbiplatform.common.ErrorCode;

/**
 * This enum is for Excel exception constant in code and message pair
 *
 * @author Glenn Cai
 * @version 1.0 07/25/2023
 */
public enum ExcelExceptionEnum {

  EXCEL_EMPTY_ERROR(ErrorCode.PARAM_ERROR.getCode(), "Excel file cannot be empty."),
  EXCEL_TYPE_ERROR(ErrorCode.PARAM_ERROR.getCode(), "Excel file type is incorrect.");

  /**
   * Error code
   */
  private final int code;

  /**
   * Error message
   */
  private final String message;

  ExcelExceptionEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
