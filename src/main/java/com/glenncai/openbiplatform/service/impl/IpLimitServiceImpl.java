package com.glenncai.openbiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glenncai.openbiplatform.common.ErrorCode;
import com.glenncai.openbiplatform.constant.IpLimitConstant;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.exception.enums.IpLimitExceptionEnum;
import com.glenncai.openbiplatform.mapper.IpLimitMapper;
import com.glenncai.openbiplatform.mapper.UserMapper;
import com.glenncai.openbiplatform.model.entity.IpLimit;
import com.glenncai.openbiplatform.model.entity.User;
import com.glenncai.openbiplatform.service.IpLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import javax.annotation.Resource;

/**
 * This class is for ip limit service implementation
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Service
@Slf4j
public class IpLimitServiceImpl extends ServiceImpl<IpLimitMapper, IpLimit>
    implements IpLimitService {

  @Resource
  private UserMapper userMapper;

  @Resource
  private IpLimitMapper ipLimitMapper;

  /**
   * Initialize ip limit
   *
   * @param clientIp client ip
   */
  @Override
  public void initIpLimit(String clientIp) {
    // Check if the ip exists in the table
    IpLimit ipLimit = this.lambdaQuery().eq(IpLimit::getIp, clientIp).one();

    if (ipLimit == null) {
      ipLimit = new IpLimit();
      ipLimit.setIp(clientIp);
      boolean createIpResult = this.save(ipLimit);
      if (!createIpResult) {
        log.error("Client ip {}, Error message: Create ip limit failed", clientIp);
        throw new BusinessException(IpLimitExceptionEnum.IP_LIMIT_INIT_ERROR.getCode(),
                                    IpLimitExceptionEnum.IP_LIMIT_INIT_ERROR.getMessage());
      }
      return;
    }

    Date lastCallDate = ipLimit.getLastCallDate();
    boolean isSameDay = DateUtils.isSameDay(lastCallDate, new Date());
    if (!isSameDay) {
      ipLimit.setCallCountToday(IpLimitConstant.MIN_CALL_COUNT_TODAY);
    }
  }

  /**
   * Check if the user has remaining call count
   *
   * @param userId user id
   * @return true if the user has remaining call count
   */
  @Override
  public boolean hasRemainingCallCount(Long userId) {
    if (userId < 0) {
      throw new BusinessException(ErrorCode.PARAM_ERROR);
    }

    User user = userMapper.selectById(userId);
    if (user == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ERROR);
    }
    Date currentDate = new Date();
    String username = user.getUsername();

    IpLimit ipLimit = getIpLimitInfoByUserId(userId);

    int callCountToday = ipLimit.getCallCountToday();
    if (callCountToday >= IpLimitConstant.MAX_CALL_COUNT_TODAY) {
      log.error("The user '{}' has no remaining call count quota on {}", username, currentDate);
      throw new BusinessException(IpLimitExceptionEnum.IP_LIMIT_CALL_COUNT_ERROR.getCode(),
                                  IpLimitExceptionEnum.IP_LIMIT_CALL_COUNT_ERROR.getMessage());
    }

    return true;
  }

  /**
   * Get ip limit info by user id
   *
   * @param userId user id
   * @return ip limit info
   */
  @Override
  public IpLimit getIpLimitInfoByUserId(Long userId) {
    User user = userMapper.selectById(userId);
    if (user == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ERROR);
    }

    String loginIp = user.getLoginIp();

    IpLimit ipLimit = this.lambdaQuery().eq(IpLimit::getIp, loginIp).one();
    if (ipLimit == null) {
      throw new BusinessException(IpLimitExceptionEnum.IP_LIMIT_NOT_EXIST_ERROR.getCode(),
                                  IpLimitExceptionEnum.IP_LIMIT_NOT_EXIST_ERROR.getMessage());
    }

    return ipLimit;
  }

  /**
   * Increase call count 1
   *
   * @param userId user id
   */
  @Override
  public void increaseCallCount(Long userId) {
    IpLimit ipLimit = getIpLimitInfoByUserId(userId);
    ipLimit.setCallCountToday(ipLimit.getCallCountToday() + 1);
    boolean updateResult = this.updateById(ipLimit);
    if (!updateResult) {
      log.error("Update t_ip_limit table failed, try to update the data: {}", ipLimit);
      throw new BusinessException(IpLimitExceptionEnum.IP_LIMIT_UPDATE_ERROR.getCode(),
                                  IpLimitExceptionEnum.IP_LIMIT_UPDATE_ERROR.getMessage());
    }
  }
}




