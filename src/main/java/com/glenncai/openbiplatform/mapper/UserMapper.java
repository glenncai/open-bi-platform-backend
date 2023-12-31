package com.glenncai.openbiplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glenncai.openbiplatform.model.entity.User;

/**
 * This class is for user mapper
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
public interface UserMapper extends BaseMapper<User> {

  /**
   * Custom login sql query
   *
   * @param username username
   * @param password password
   * @return user info
   */
  User login(String username, String password);
}




