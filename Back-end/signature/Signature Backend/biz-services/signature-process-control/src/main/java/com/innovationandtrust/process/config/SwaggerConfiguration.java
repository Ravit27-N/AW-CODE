package com.innovationandtrust.process.config;

import com.innovationandtrust.configuration.document.SwaggerConfig;
import com.innovationandtrust.configuration.document.SwaggerProperty;
import com.innovationandtrust.configuration.security.SecurityProperty;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {SwaggerProperty.class})
public class SwaggerConfiguration extends SwaggerConfig {

  public SwaggerConfiguration(SecurityProperty keycloakProperty, SwaggerProperty swaggerProperty) {
    super(keycloakProperty, swaggerProperty);
  }

  @Bean
  GroupedOpenApi processCtrlApi() {
    return super.groupedOpenApi("Process Control", "/**/v1/process-controls/**");
  }

  @Bean
  GroupedOpenApi signInfoApi() {
    return super.groupedOpenApi(
        "Signing Process", "/**/sign-info/**", "/**/otp/**", "/**/sign/**", "/**/documents/**", "/**/validate/document/**");
  }

  @Bean
  GroupedOpenApi approvalApi() {
    return super.groupedOpenApi(
        "Approval Process", "/**/approval/**");
  }

  @Bean
  GroupedOpenApi refuseApi() {
    return super.groupedOpenApi(
            "Refuse Process", "/**/refuse/**");
  }

  @Bean
  GroupedOpenApi webHookApi() {
    return super.groupedOpenApi(
        "Webhook EID", "/**/webhook/**");
  }
}
