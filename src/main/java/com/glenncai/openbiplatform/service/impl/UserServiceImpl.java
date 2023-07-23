package com.glenncai.openbiplatform.service.impl;

import static com.glenncai.openbiplatform.constant.UserConstant.LOGIN_USER_STAGE;
import static com.glenncai.openbiplatform.utils.NetUtils.getClientIpAddress;
import static com.glenncai.openbiplatform.utils.UserUtils.encryptPassword;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.exception.enums.AuthExceptionEnum;
import com.glenncai.openbiplatform.mapper.IpLimitMapper;
import com.glenncai.openbiplatform.mapper.UserMapper;
import com.glenncai.openbiplatform.model.dto.user.UserAddRequest;
import com.glenncai.openbiplatform.model.dto.user.UserLoginRequest;
import com.glenncai.openbiplatform.model.dto.user.UserRegisterRequest;
import com.glenncai.openbiplatform.model.entity.User;
import com.glenncai.openbiplatform.model.enums.UserRoleEnum;
import com.glenncai.openbiplatform.model.vo.LoginUserVO;
import com.glenncai.openbiplatform.model.vo.UserVO;
import com.glenncai.openbiplatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This class is for user service implementation
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  private static final String usernameValidateRegex = "^[a-zA-Z0-9]{4,16}$";

  @Resource
  IpLimitMapper ipLimitMapper;

  @Resource
  private UserMapper userMapper;

  /**
   * User register
   *
   * @param userRegisterRequest user register request body
   * @return the id of the newly created user
   */
  @Override
  public long register(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
    String username = userRegisterRequest.getUsername();
    String password = userRegisterRequest.getPassword();
    String confirmPassword = userRegisterRequest.getConfirmPassword();
    String clientIp = getClientIpAddress(request);

    if (StringUtils.isAnyBlank(username, password, confirmPassword)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_EMPTY_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_EMPTY_ERROR.getMessage());
    }
    if (username.length() < 4 || username.length() > 16) {
      throw new BusinessException(AuthExceptionEnum.AUTH_USERNAME_LENGTH_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_USERNAME_LENGTH_ERROR.getMessage());
    }
    if (!username.matches(usernameValidateRegex)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_USERNAME_FORMAT_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_USERNAME_FORMAT_ERROR.getMessage());
    }
    if (password.contains(" ")) {
      throw new BusinessException(AuthExceptionEnum.AUTH_PASSWORD_SPACE_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_PASSWORD_SPACE_ERROR.getMessage());
    }
    if (password.length() < 8) {
      throw new BusinessException(AuthExceptionEnum.AUTH_PASSWORD_LENGTH_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_PASSWORD_LENGTH_ERROR.getMessage());
    }
    if (!password.equals(confirmPassword)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_PASSWORD_NOT_MATCH_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_PASSWORD_NOT_MATCH_ERROR.getMessage());
    }

    synchronized (username.intern()) {
      // Check if username already exists
      checkUserExist(username);

      // Encrypt password with MD5
      String encryptedPassword = encryptPassword(password);

      // Create user
      User user = new User();
      user.setUsername(username);
      user.setPassword(encryptedPassword);
      boolean createResult = this.save(user);
      if (!createResult) {
        log.error("Client IP: {}, Username: {}, Error message: Create user failed", clientIp,
                  username);
        throw new BusinessException(AuthExceptionEnum.AUTH_CREATE_USER_ERROR.getCode(),
                                    AuthExceptionEnum.AUTH_CREATE_USER_ERROR.getMessage());
      }

      log.info("Client IP: {}, Username: {}, Success message: Create user successfully", clientIp,
               username);
      return user.getId();
    }
  }

  /**
   * User login
   *
   * @param userLoginRequest user login request body
   * @param request          http request
   * @return filtered user info
   */
  @Override
  public LoginUserVO login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
    String username = userLoginRequest.getUsername();
    String password = userLoginRequest.getPassword();
    String clientIp = getClientIpAddress(request);

    if (StringUtils.isAnyBlank(username, password)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage());
    }
    if (username.length() < 4 || username.length() > 16) {
      throw new BusinessException(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage());
    }
    if (!username.matches(usernameValidateRegex)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage());
    }
    if (password.contains(" ")) {
      throw new BusinessException(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage());
    }
    if (password.length() < 8) {
      throw new BusinessException(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage());
    }

    // Encrypt password with MD5
    String encryptedPassword = encryptPassword(password);

    // Check if username and password are correct
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    queryWrapper.eq("password", encryptedPassword);
    User user = userMapper.selectOne(queryWrapper);

    if (user == null) {
      log.error("Client IP: {}, Username: {}, Error message: Username or password is incorrect",
                clientIp, username);
      throw new BusinessException(AuthExceptionEnum.AUTH_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_LOGIN_ERROR.getMessage());
    }

    // Update login ip
    user.setLoginIp(clientIp);
    boolean updateIpResult = this.updateById(user);
    if (!updateIpResult) {
      log.error("Client IP: {}, Username: {}, Error message: Update login ip failed", clientIp,
                username);
      throw new BusinessException(AuthExceptionEnum.AUTH_UPDATE_LOGIN_IP_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_UPDATE_LOGIN_IP_ERROR.getMessage());
    }

    log.info("Client IP: {}, Username: {}, Success message: Login successfully", clientIp,
             username);
    request.getSession().setAttribute(LOGIN_USER_STAGE, user);
    return getCurrentLoginUserVO(user);
  }

  /**
   * Get current login user info
   *
   * @param request http request
   * @return user info
   */
  @Override
  public User getCurrentLoginUser(HttpServletRequest request) {
    Object userObj = request.getSession().getAttribute(LOGIN_USER_STAGE);
    User currentUser = (User) userObj;
    if (currentUser == null || currentUser.getId() == null) {
      throw new BusinessException(AuthExceptionEnum.AUTH_NOT_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_NOT_LOGIN_ERROR.getMessage());
    }
    long userId = currentUser.getId();
    currentUser = this.getById(userId);
    if (currentUser == null) {
      throw new BusinessException(AuthExceptionEnum.AUTH_NOT_LOGIN_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_NOT_LOGIN_ERROR.getMessage());
    }
    return currentUser;
  }

  /**
   * Get current login user info (allow not login)
   *
   * @param request http request
   * @return user info
   */
  @Override
  public User getCurrentLoginUserPermitNull(HttpServletRequest request) {
    Object userObj = request.getSession().getAttribute(LOGIN_USER_STAGE);
    User currentUser = (User) userObj;
    if (currentUser == null || currentUser.getId() == null) {
      return null;
    }
    long userId = currentUser.getId();
    return this.getById(userId);
  }

  /**
   * To check if current user is admin
   *
   * @param request http request
   * @return true if current user is admin
   */
  @Override
  public boolean isAdmin(HttpServletRequest request) {
    Object userObj = request.getSession().getAttribute(LOGIN_USER_STAGE);
    User currentUser = (User) userObj;
    return isAdmin(currentUser);
  }

  /**
   * To check if current user is admin
   *
   * @param user user entity
   * @return true if current user is admin
   */
  @Override
  public boolean isAdmin(User user) {
    return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getRole());
  }

  /**
   * User logout
   *
   * @param request http request
   * @return true if logout successfully
   */
  @Override
  public boolean logout(HttpServletRequest request) {
    if (request == null) {
      throw new BusinessException(AuthExceptionEnum.AUTH_EMPTY_HTTP_REQUEST_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_EMPTY_HTTP_REQUEST_ERROR.getMessage());
    }

    if (request.getSession().getAttribute(LOGIN_USER_STAGE) == null) {
      throw new BusinessException(AuthExceptionEnum.AUTH_OPERATION_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_OPERATION_ERROR.getMessage());
    }

    // Remove user info from session
    request.getSession().removeAttribute(LOGIN_USER_STAGE);
    return true;
  }

  /**
   * Get current login user's filtered info
   *
   * @param user user entity
   * @return filtered user info
   */
  @Override
  public LoginUserVO getCurrentLoginUserVO(User user) {
    if (user == null) {
      return null;
    }
    LoginUserVO loginUserVO = new LoginUserVO();
    BeanUtils.copyProperties(user, loginUserVO);
    return loginUserVO;
  }

  /**
   * Get user's filtered info
   *
   * @param user user entity
   * @return filtered user info
   */
  @Override
  public UserVO getUserVO(User user) {
    if (user == null) {
      return null;
    }
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    return userVO;
  }

  /**
   * Get users' filtered info
   *
   * @param userList user entity list
   * @return filtered user info list
   */
  @Override
  public List<UserVO> getUserVO(List<User> userList) {
    if (CollectionUtils.isEmpty(userList)) {
      return new ArrayList<>();
    }
    return userList.stream().map(this::getUserVO).toList();
  }

  /**
   * Check if user exist
   *
   * @param username username
   */
  @Override
  public void checkUserExist(String username) {
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    long count = userMapper.selectCount(queryWrapper);
    if (count > 0) {
      throw new BusinessException(AuthExceptionEnum.AUTH_USERNAME_EXIST_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_USERNAME_EXIST_ERROR.getMessage());
    }
  }

  /**
   * Add new user (only admin can do this)
   *
   * @param userAddRequest user add request body
   * @param request        http request
   * @return the id of the newly created user
   */
  @Override
  public long addUser(UserAddRequest userAddRequest, HttpServletRequest request) {
    String clientIp = getClientIpAddress(request);
    String username = userAddRequest.getUsername();
    String password = userAddRequest.getPassword();
    String role = userAddRequest.getRole();

    if (StringUtils.isAnyBlank(username, password, role)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_EMPTY_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_EMPTY_ERROR.getMessage());
    }
    if (username.length() < 4 || username.length() > 16) {
      throw new BusinessException(AuthExceptionEnum.AUTH_USERNAME_LENGTH_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_USERNAME_LENGTH_ERROR.getMessage());
    }
    if (!username.matches(usernameValidateRegex)) {
      throw new BusinessException(AuthExceptionEnum.AUTH_USERNAME_FORMAT_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_USERNAME_FORMAT_ERROR.getMessage());
    }
    if (password.contains(" ")) {
      throw new BusinessException(AuthExceptionEnum.AUTH_PASSWORD_SPACE_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_PASSWORD_SPACE_ERROR.getMessage());
    }
    if (password.length() < 8) {
      throw new BusinessException(AuthExceptionEnum.AUTH_PASSWORD_LENGTH_ERROR.getCode(),
                                  AuthExceptionEnum.AUTH_PASSWORD_LENGTH_ERROR.getMessage());
    }

    synchronized (username.intern()) {
      checkUserExist(username);

      String encryptedPassword = encryptPassword(password);
      User user = new User();
      user.setUsername(username);
      user.setPassword(encryptedPassword);
      user.setRole(role);
      boolean createResult = this.save(user);
      if (!createResult) {
        log.error("Client IP: {}, Username: {}, Error message: Create user by admin failed",
                  clientIp,
                  username);
        throw new BusinessException(AuthExceptionEnum.AUTH_CREATE_USER_ERROR.getCode(),
                                    AuthExceptionEnum.AUTH_CREATE_USER_ERROR.getMessage());
      }

      log.info("Client IP: {}, Username: {}, Success message: Create user by admin successfully",
               clientIp, username);
      return user.getId();
    }
  }
}




