package com.glenncai.openbiplatform.model.dto.user.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for user add request body
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Data
public class UserAddRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 8464525927488463852L;

  private String username;

  private String password;

  private String role;
}
