package com.glenncai.openbiplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is for main method to run the application.
 *
 * @author Glenn Cai
 * @version 1.0 07/18/2023
 */
@SpringBootApplication
@MapperScan("com.glenncai.openbiplatform.mapper")
public class OpenBiPlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenBiPlatformApplication.class, args);
  }

}
