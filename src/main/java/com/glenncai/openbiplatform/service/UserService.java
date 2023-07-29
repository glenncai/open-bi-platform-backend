package com.glenncai.openbiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glenncai.openbiplatform.model.dto.user.request.UserAddRequest;
import com.glenncai.openbiplatform.model.dto.user.request.UserLoginRequest;
import com.glenncai.openbiplatform.model.dto.user.request.UserRegisterRequest;
import com.glenncai.openbiplatform.model.entity.User;
import com.glenncai.openbiplatform.model.vo.LoginUserVO;
import com.glenncai.openbiplatform.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * This interface is for user service
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
public interface UserService extends IService<User> {

  /**
   * User register
   *
   * @param userRegisterRequest user register request body
   * @param request             http request
   * @return the id of the newly created user
   */
  long register(UserRegisterRequest userRegisterRequest, HttpServletRequest request);

  /**
   * User login
   *
   * @param userLoginRequest user login request body
   * @param request          http request
   * @return filtered user info
   */
  LoginUserVO login(UserLoginRequest userLoginRequest, HttpServletRequest request);

  /**
   * Get current login user info
   *
   * @param request http request
   * @return filtered user info
   */
  User getCurrentLoginUser(HttpServletRequest request);

  /**
   * Get current login user info (allow not login)
   *
   * @param request http request
   * @return filtered user info
   */
  User getCurrentLoginUserPermitNull(HttpServletRequest request);

  /**
   * To check if current login user is admin
   *
   * @param request http request
   * @return true if current login user is admin
   */
  boolean isAdmin(HttpServletRequest request);

  /**
   * To check if current login user is admin
   *
   * @param user user entity
   * @return true if current login user is admin
   */
  boolean isAdmin(User user);

  /**
   * User logout
   *
   * @param request http request
   * @return true if logout successfully
   */
  boolean logout(HttpServletRequest request);

  /**
   * Get current login user's filtered info
   *
   * @param user user entity
   * @return filtered user info
   */
  LoginUserVO getCurrentLoginUserVO(User user);

  /**
   * Get user's filtered info
   *
   * @param user user entity
   * @return filtered user info
   */
  UserVO getUserVO(User user);

  /**
   * Get users' filtered info
   *
   * @param userList user entity list
   * @return filtered user info list
   */
  List<UserVO> getUserVO(List<User> userList);

  /**
   * Check if user exist
   *
   * @param username username
   */
  void checkUserExist(String username);

  /**
   * Add new user (only admin can do this)
   *
   * @param userAddRequest user add request body
   * @param request        http request
   * @return the id of the newly created user
   */
  long addUser(UserAddRequest userAddRequest, HttpServletRequest request);
}
