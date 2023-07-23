package com.glenncai.openbiplatform.controller;

import com.glenncai.openbiplatform.common.BaseResponse;
import com.glenncai.openbiplatform.common.BaseResult;
import com.glenncai.openbiplatform.model.dto.user.UserLoginRequest;
import com.glenncai.openbiplatform.model.dto.user.UserRegisterRequest;
import com.glenncai.openbiplatform.model.entity.User;
import com.glenncai.openbiplatform.model.vo.LoginUserVO;
import com.glenncai.openbiplatform.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is for user controller (API)
 *
 * @author Glenn Cai
 * @version 1.0 07/20/2023
 */
@Api(tags = "User Controller")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

  @Resource
  private UserService userService;

  /**
   * User register api
   *
   * @param userRegisterRequest user register request body
   * @param request             http request
   * @return the id of the newly created user
   */
  @ApiOperation(value = "User register")
  @PostMapping("/register")
  public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest,
                                     HttpServletRequest request) {
    long result = userService.register(userRegisterRequest, request);
    return BaseResult.success(result);
  }

  /**
   * User login api
   *
   * @param userLoginRequest user login request body
   * @param request          http request
   * @return filtered user info
   */
  @ApiOperation(value = "User login")
  @PostMapping("/login")
  public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest,
                                         HttpServletRequest request) {
    LoginUserVO result = userService.login(userLoginRequest, request);
    return BaseResult.success(result);
  }

  /**
   * User logout api
   *
   * @param request http request
   * @return success message
   */
  @ApiOperation(value = "User logout")
  @PostMapping("/logout")
  public BaseResponse<Boolean> logout(HttpServletRequest request) {
    boolean result = userService.logout(request);
    return BaseResult.success(result);
  }

  /**
   * Get current login user filtered info api
   *
   * @param request http request
   * @return current login user filtered info
   */
  @ApiOperation(value = "Get current login user")
  @PostMapping("/get/login")
  public BaseResponse<LoginUserVO> getCurrentLoginUser(HttpServletRequest request) {
    User user = userService.getCurrentLoginUser(request);
    return BaseResult.success(userService.getCurrentLoginUserVO(user));
  }
}
