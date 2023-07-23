package com.glenncai.openbiplatform.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This class contains the configuration for generating Swagger API
 * documentation using Knife4j. It enables Swagger 2 and Knife4j and
 * configures a Docket bean for building the API docs.
 *
 * @author Glenn Cai
 * @version 1.0 07/19/2023
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Profile({"dev", "test"})
public class Knife4jConfig {

  private final OpenApiExtensionResolver openApiExtensionResolver;

  @Autowired
  public Knife4jConfig(OpenApiExtensionResolver openApiExtensionResolver) {
    this.openApiExtensionResolver = openApiExtensionResolver;
  }

  /**
   * Configures a Docket bean to generate Swagger API documentation. The Docket bean defines the
   * RESTful endpoints and routes to include in the API documentation.
   *
   * @return A configured Docket instance for the Swagger documentation
   */
  @Bean(value = "defaultApi2")
  public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.glenncai.openbiplatform.controller"))
        .paths(PathSelectors.any())
        .build()
        .extensions(openApiExtensionResolver.buildSettingExtensions());
  }

  /**
   * Creates an ApiInfoBuilder with information about the API documentation.
   *
   * @return The ApiInfoBuilder instance
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Open BI Platform RESTful API")
        .description("API documentation for the Open BI Platform")
        .version("1.0")
        .build();
  }
}
