package com.innovationandtrust.notification.config;

import com.innovationandtrust.configuration.document.SwaggerConfig;
import com.innovationandtrust.configuration.document.SwaggerProperty;
import com.innovationandtrust.configuration.security.SecurityProperty;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {SecurityProperty.class, SwaggerProperty.class})
public class SwaggerConfiguration extends SwaggerConfig {
  public SwaggerConfiguration(SecurityProperty keycloakProperty, SwaggerProperty swaggerProperty) {
    super(keycloakProperty, swaggerProperty);
  }

  @Bean
  GroupedOpenApi mails() {
    return super.groupedOpenApi(
            "Mail", "/**/mails/**");
  }

  @Bean
  GroupedOpenApi sms() {
    return super.groupedOpenApi(
            "SMS", "/**/sms/**");
  }
}
