package com.allweb.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@OpenAPIDefinition
@AllArgsConstructor
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI(
      @Value("${openapi.service.title}") String serviceTitle,
      @Value("${openapi.service.description}") String description,
      @Value("${openapi.service.version}") String serviceVersion) {
    return new OpenAPI()
        .info(new io.swagger.v3.oas.models.info.Info()
            .title(serviceTitle)
            .description(description)
            .version(serviceVersion));
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(r -> r.path("/rms-service/v3/api-docs").and().method(HttpMethod.GET)
            .uri("lb://aw-rms-service"))
        .build();
  }
}