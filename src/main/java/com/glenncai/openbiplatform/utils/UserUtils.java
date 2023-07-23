package com.glenncai.openbiplatform.utils;

import static com.glenncai.openbiplatform.constant.UserConstant.SALT;
import org.springframework.util.DigestUtils;

/**
 * This class is for user common utils
 *
 * @author Glenn Cai
 * @version 1.0 7/20/2023
 */
public class UserUtils {

  private UserUtils() {
  }

  public static String encryptPassword(String password) {
    return DigestUtils.md5DigestAsHex((password + SALT).getBytes());
  }
}
