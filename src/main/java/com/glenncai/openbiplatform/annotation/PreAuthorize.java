package com.glenncai.openbiplatform.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for auth check
 *
 * @author Glenn Cai
 * @version 1.0 07/23/2023
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthorize {

  /**
   * Role allowed
   *
   * @return role allowed
   */
  String roleAllowed() default "";
}
