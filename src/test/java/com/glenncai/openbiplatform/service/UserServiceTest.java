package com.glenncai.openbiplatform.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.exception.enums.AuthExceptionEnum;
import com.glenncai.openbiplatform.mapper.UserMapper;
import com.glenncai.openbiplatform.model.dto.user.request.UserLoginRequest;
import com.glenncai.openbiplatform.model.dto.user.request.UserRegisterRequest;
import com.glenncai.openbiplatform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

/**
 * This class is for user service test
 *
 * @author Glenn Cai
 * @version 1.0 07/21/2023
 */
@SpringBootTest
class UserServiceTest {

  @Resource
  private UserService userService;

  @MockBean
  private UserMapper userMapper;

  @Mock
  private UserServiceImpl userServiceImpl;

  @Test
  void register() {
    BusinessException businessException;
    UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteAddr("127.0.0.1");

    // Test empty error
    userRegisterRequest.setUsername("");
    userRegisterRequest.setPassword("test123456!");
    userRegisterRequest.setConfirmPassword("test123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_EMPTY_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_EMPTY_ERROR.getMessage(),
                            businessException.getMessage());

    // Test username length error
    userRegisterRequest.setUsername("tes");
    userRegisterRequest.setPassword("test123456!");
    userRegisterRequest.setConfirmPassword("test123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_USERNAME_LENGTH_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_USERNAME_LENGTH_ERROR.getMessage(),
                            businessException.getMessage());

    // Test username format error
    userRegisterRequest.setUsername("test@");
    userRegisterRequest.setPassword("test123456!");
    userRegisterRequest.setConfirmPassword("test123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_USERNAME_FORMAT_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_USERNAME_FORMAT_ERROR.getMessage(),
                            businessException.getMessage());

    // Test password format error
    userRegisterRequest.setUsername("test");
    userRegisterRequest.setPassword("test 123456!");
    userRegisterRequest.setConfirmPassword("test 123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_PASSWORD_SPACE_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_PASSWORD_SPACE_ERROR.getMessage(),
                            businessException.getMessage());

    // Test password length error
    userRegisterRequest.setUsername("test");
    userRegisterRequest.setPassword("test1!");
    userRegisterRequest.setConfirmPassword("test1!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_PASSWORD_LENGTH_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_PASSWORD_LENGTH_ERROR.getMessage(),
                            businessException.getMessage());

    // Test password not match error
    userRegisterRequest.setUsername("test");
    userRegisterRequest.setPassword("test123456!");
    userRegisterRequest.setConfirmPassword("test123456");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_PASSWORD_NOT_MATCH_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_PASSWORD_NOT_MATCH_ERROR.getMessage(),
                            businessException.getMessage());

    // Test username exist error
    userRegisterRequest.setUsername("test");
    userRegisterRequest.setPassword("test123456!");
    userRegisterRequest.setConfirmPassword("test123456!");
    when(userMapper.selectCount(any())).thenReturn(1L);
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_USERNAME_EXIST_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_USERNAME_EXIST_ERROR.getMessage(),
                            businessException.getMessage());

    // Clear the specified mock
    reset(userMapper);

    // Test create user error
    userRegisterRequest.setUsername("john");
    userRegisterRequest.setPassword("test123456!");
    when(userMapper.insert(any())).thenReturn(0);
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.register(userRegisterRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_CREATE_USER_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_CREATE_USER_ERROR.getMessage(),
                            businessException.getMessage());
  }

  @Test
  void login() {
    BusinessException businessException;
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteAddr("127.0.0.1");

    // Test empty error
    userLoginRequest.setUsername("test");
    userLoginRequest.setPassword("");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.login(userLoginRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage(),
                            businessException.getMessage());

    // Test username length error
    userLoginRequest.setUsername("hi");
    userLoginRequest.setPassword("test123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.login(userLoginRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage(),
                            businessException.getMessage());

    // Test username format error
    userLoginRequest.setUsername("test@");
    userLoginRequest.setPassword("test123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.login(userLoginRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage(),
                            businessException.getMessage());

    // Test password format error
    userLoginRequest.setUsername("test");
    userLoginRequest.setPassword("test 123456!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.login(userLoginRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage(),
                            businessException.getMessage());

    // Test password length error
    userLoginRequest.setUsername("test");
    userLoginRequest.setPassword("test1!");
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.login(userLoginRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage(),
                            businessException.getMessage());

    // Check if username and password are correct
    userLoginRequest.setUsername("test");
    userLoginRequest.setPassword("test123456@@@");
    when(userMapper.selectOne(any())).thenReturn(null);
    businessException = Assertions.assertThrows(BusinessException.class, () -> {
      userService.login(userLoginRequest, request);
    });
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                            businessException.getCode());
    Assertions.assertEquals(AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage(),
                            businessException.getMessage());
  }
}