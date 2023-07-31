package com.glenncai.openbiplatform.model.dto.thread_mock.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Thread mock add request body
 *
 * @author Glenn Cai
 * @version 1.0 07/31/2023
 */
@Data
public class ThreadMockAddRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 4370003817636013226L;

  /**
   * Task name
   */
  private String name;
}
