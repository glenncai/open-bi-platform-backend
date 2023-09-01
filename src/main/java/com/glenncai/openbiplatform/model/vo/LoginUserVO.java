package com.glenncai.openbiplatform.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is for login user view object (already filtered)
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Data
public class LoginUserVO implements Serializable {

  @Serial
  private static final long serialVersionUID = 1073586232203120456L;

  /**
   * User id
   */
  private Long id;

  /**
   * Username
   */
  private String username;

  /**
   * User role
   */
  private String role;

  /**
   * Login IP
   */
  private String loginIp;

  /**
   * Is valid
   */
  private Integer valid;

  /**
   * Created time
   */
  private Date createdAt;

  /**
   * Updated time
   */
  private Date updatedAt;
}
