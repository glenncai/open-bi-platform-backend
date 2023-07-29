package com.glenncai.openbiplatform.model.dto.user.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for user register request body
 *
 * @author Glenn Cai
 * @version 1.0 7/19/2023
 */
@Data
public class UserRegisterRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 5828747689399838674L;

  private String username;

  private String password;

  private String confirmPassword;
}
