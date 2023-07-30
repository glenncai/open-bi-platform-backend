package com.glenncai.openbiplatform.manager;

import com.glenncai.openbiplatform.common.ErrorCode;
import com.glenncai.openbiplatform.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Redis rate limiter service test
 *
 * @author Glenn Cai
 * @version 1.0 07/30/2023
 */
@SpringBootTest
class RedisLimiterManagerTest {

  @Resource
  private RedisLimiterManager redisLimiterManager;

  @Test
  void doRateLimit() {
    String userId = "1";
    int count = 0;
    for (int i = 0; i < 2; i++) {
      try {
        redisLimiterManager.doRateLimit(userId);
        count++;
      } catch (BusinessException e) {
        Assertions.assertEquals(ErrorCode.TOO_MANY_REQUESTS_ERROR.getCode(), e.getCode());
        Assertions.assertEquals(ErrorCode.TOO_MANY_REQUESTS_ERROR.getMessage(), e.getMessage());
        Assertions.assertEquals(1, count);
      }
    }
  }
}