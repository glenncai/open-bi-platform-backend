package com.glenncai.openbiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glenncai.openbiplatform.model.entity.IpLimit;

/**
 * This interface is for ip limit service
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
public interface IpLimitService extends IService<IpLimit> {
  /**
   * Initialize ip limit
   *
   * @param clientIp client ip
   */
  void initIpLimit(String clientIp);

  /**
   * Check if the user has remaining call count
   *
   * @param userId user id
   * @return true if the user has remaining call count
   */
  boolean hasRemainingCallCount(Long userId);

  /**
   * Get ip limit info by user id
   *
   * @param userId user id
   * @return ip limit info
   */
  IpLimit getIpLimitInfoByUserId(Long userId);

  /**
   * Increase call count 1
   *
   * @param userId user id
   */
  void increaseCallCount(Long userId);
}
