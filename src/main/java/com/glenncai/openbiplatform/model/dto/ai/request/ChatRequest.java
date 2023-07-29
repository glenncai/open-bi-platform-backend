package com.glenncai.openbiplatform.model.dto.ai.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is for AI chat request body
 *
 * @author Glenn Cai
 * @version 1.0 07/28/2023
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = -8992915834693874492L;

  /**
   * Execute message for chat
   */
  private String message;

  /**
   * Key value
   */
  private String key;
}
