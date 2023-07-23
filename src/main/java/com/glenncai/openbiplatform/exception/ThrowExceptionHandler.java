package com.glenncai.openbiplatform.exception;

import com.glenncai.openbiplatform.common.ErrorCode;

/**
 * This class is for handling throw exception
 *
 * @author Glenn Cai
 * @version 1.0 07/23/2023
 */
public class ThrowExceptionHandler {

  /**
   * Throw runtime exception if condition is true
   *
   * @param condition        condition
   * @param runtimeException runtime exception
   */
  public static void throwExceptionIf(boolean condition, RuntimeException runtimeException) {
    if (condition) {
      throw runtimeException;
    }
  }

  /**
   * Throw runtime exception if condition is true
   *
   * @param condition condition
   * @param errorCode custom error code
   */
  public static void throwExceptionIf(boolean condition, ErrorCode errorCode) {
    throwExceptionIf(condition, new BusinessException(errorCode));
  }

  /**
   * Throw runtime exception if condition is true
   *
   * @param condition condition
   * @param errorCode custom error code
   * @param message   error message
   */
  public static void throwExceptionIf(boolean condition, ErrorCode errorCode, String message) {
    throwExceptionIf(condition, new BusinessException(errorCode, message));
  }
}
