package com.allweb.rms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi actuatorApi() {
    return GroupedOpenApi.builder()
        .group("Actuator")
        .pathsToMatch("/actuator/**")
        .pathsToExclude("/actuator/health/*")
        .build();
  }

  @Bean
  public OpenAPI allwebRmsAPI() {
    StringSchema schema = new StringSchema();
    return new OpenAPI()
        .components(
            new Components()
                .addParameters(
                    "myGlobalHeader",
                    new HeaderParameter()
                        .required(true)
                        .name("My-Global-Header")
                        .description("My Global Header")
                        .schema(schema)))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearer-key",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")))
        .info(
            new Info()
                .title("Allweb Recuitment Management System")
                .description("Recruitment Management System API")
                .version("v2.0.0")
                .license(
                    new License().name("Contact Us").url("https://www.allweb.com.kh/contact-us")))
        .addSecurityItem(
            new SecurityRequirement()
                .addList("bearer-key", Arrays.asList("read", "write", "openid")))
        .externalDocs(
            new ExternalDocumentation()
                .description("ALLWEB Co., Ltd.")
                .url("https://www.allweb.com.kh/"));
  }

  @Bean
  public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
    return openApi ->
        openApi.getPaths().values().stream()
            .flatMap(pathItem -> pathItem.readOperations().stream())
            .forEach(
                operation ->
                    operation.addParametersItem(
                        new HeaderParameter().$ref("#/components/parameters/myGlobalHeader")));
  }
}
