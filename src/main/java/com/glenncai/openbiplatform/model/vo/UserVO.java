package com.glenncai.openbiplatform.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is for user view object (already filtered)
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Data
public class UserVO implements Serializable {

  @Serial
  private static final long serialVersionUID = -4218283791853524068L;

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
   * Created time
   */
  private Date createdAt;
}
