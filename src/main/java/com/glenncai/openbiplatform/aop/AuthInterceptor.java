package com.glenncai.openbiplatform.aop;

import com.glenncai.openbiplatform.annotation.PreAuthorize;
import com.glenncai.openbiplatform.common.ErrorCode;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.model.entity.User;
import com.glenncai.openbiplatform.model.enums.UserRoleEnum;
import com.glenncai.openbiplatform.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is for auth interceptor to check user's auth before request is handled
 *
 * @author Glenn Cai
 * @version 1.0 07/23/2023
 */
@Aspect
@Component
public class AuthInterceptor {

  @Resource
  private UserService userService;

  @Around("@annotation(preAuthorize)")
  public Object doInterceptor(ProceedingJoinPoint joinPoint, PreAuthorize preAuthorize)
      throws Throwable {
    String roleAllowed = preAuthorize.roleAllowed();
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    // Current login user info
    User currentLoginUser = userService.getCurrentLoginUser(request);

    // Must include roleAllowed
    if (StringUtils.isNotBlank(roleAllowed)) {
      UserRoleEnum roleAllowedEnum = UserRoleEnum.getEnumByValue(roleAllowed);

      if (roleAllowedEnum == null) {
        throw new BusinessException(ErrorCode.UNAUTHORIZED_ERROR);
      }
      String currentLoginUserRole = currentLoginUser.getRole();

      // If the user is banned, throw exception
      if (UserRoleEnum.BAN.equals(roleAllowedEnum)) {
        throw new BusinessException(ErrorCode.UNAUTHORIZED_ERROR);
      }

      // Only admin can access
      if (UserRoleEnum.ADMIN.equals(roleAllowedEnum) && !roleAllowed.equals(currentLoginUserRole)) {
        throw new BusinessException(ErrorCode.UNAUTHORIZED_ERROR);
      }
    }

    // Pass auth check, continue to execute
    return joinPoint.proceed();
  }
}
