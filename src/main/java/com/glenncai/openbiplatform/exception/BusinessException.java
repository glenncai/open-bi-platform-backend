package com.glenncai.openbiplatform.exception;

import com.glenncai.openbiplatform.common.ErrorCode;

/**
 * This class is for custom business exception
 *
 * @author Glenn Cai
 * @version 1.0 07/20/2023
 */
public class BusinessException extends RuntimeException {

  /**
   * Error code
   */
  private final int code;

  public BusinessException(int code) {
    this.code = code;
  }

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.code = errorCode.getCode();
  }

  public int getCode() {
    return code;
  }
}
