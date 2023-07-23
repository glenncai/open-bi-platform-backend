package com.glenncai.openbiplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is for global cors config
 *
 * @author Glenn Cai
 * @version 1.0 07/20/2023
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  /**
   * @param registry cors registry
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowCredentials(true) // Allow cookie
            .allowedOriginPatterns(
                "http://127.0.0.1:3000, http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("*");
  }
}
