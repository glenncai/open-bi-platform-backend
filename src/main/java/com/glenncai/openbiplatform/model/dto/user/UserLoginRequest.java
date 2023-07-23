package com.glenncai.openbiplatform.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for user login request body
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Data
public class UserLoginRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 372708386515440671L;

  private String username;

  private String password;
}
