package com.glenncai.openbiplatform.manager;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.glenncai.openbiplatform.common.BaseResponse;
import com.glenncai.openbiplatform.constant.AiConstant;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.exception.enums.AiExceptionEnum;
import com.glenncai.openbiplatform.model.dto.ai.request.ChatRequest;
import com.glenncai.openbiplatform.model.dto.ai.response.ChatResponse;
import com.glenncai.openbiplatform.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Communicate with AI service (e.g. OpenAI, Google Bard, POE, etc.)
 *
 * @author Glenn Cai
 * @version 1.0 07/26/2023
 */
@Service
@Slf4j
public class AiManager {

  /**
   * Process AI chat
   *
   * @param chatRequest chat request body, including 'message' and 'key'
   * @return AI response
   */
  public String doAiChat(ChatRequest chatRequest) {
    String jsonStr = JSONUtil.toJsonStr(chatRequest);
    String result = HttpUtils.post(AiConstant.AI_API_URL, jsonStr);
    TypeReference<BaseResponse<ChatResponse>> typeRef = new TypeReference<>() {
    };

    BaseResponse<ChatResponse> response = JSONUtil.toBean(result, typeRef, false);
    if (response == null) {
      throw new BusinessException(AiExceptionEnum.AI_RESPONSE_ERROR.getCode(),
                                  AiExceptionEnum.AI_RESPONSE_ERROR.getMessage());
    }
    if (response.getCode() != 0) {
      throw new BusinessException(AiExceptionEnum.AI_RESPONSE_ERROR.getCode(),
                                  AiExceptionEnum.AI_RESPONSE_ERROR.getMessage());
    }

    return response.getData().getContent();
  }
}
