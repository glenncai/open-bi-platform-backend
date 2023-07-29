package com.glenncai.openbiplatform.config;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is for MyBatis Plus pagination config
 *
 * @author Glenn Cai
 * @version 1.0 07/26/2023
 */
@Configuration
@MapperScan("com.glenncai.openbiplatform.mapper")
public class MyBatisPlusConfig {

  /**
   * Pagination interceptor
   *
   * @return mybatis plus interceptor
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
  }
}
