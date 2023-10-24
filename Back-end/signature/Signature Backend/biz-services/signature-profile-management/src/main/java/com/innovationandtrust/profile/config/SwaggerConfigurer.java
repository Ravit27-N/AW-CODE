package com.innovationandtrust.profile.config;

import com.innovationandtrust.configuration.document.SwaggerConfig;
import com.innovationandtrust.configuration.document.SwaggerProperty;
import com.innovationandtrust.configuration.security.SecurityProperty;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SecurityProperty.class, SwaggerProperty.class})
public class SwaggerConfigurer extends SwaggerConfig {
  public SwaggerConfigurer(SecurityProperty securityProperty, SwaggerProperty property) {
    super(securityProperty, property);
  }

  @Bean
  GroupedOpenApi userApi() {
    return super.groupedOpenApi("user", "/**/v1/users/**");
  }

  @Bean
  GroupedOpenApi corporateUserApi() {
    return super.groupedOpenApi("corporate-users", "/**/v1/corporate/users/**");
  }

  @Bean
  GroupedOpenApi roleApi() {
    return super.groupedOpenApi("role", "/**/v1/roles/**");
  }

  @Bean
  GroupedOpenApi signProcessApi() {
    return super.groupedOpenApi("sign-process", "/**/v1/sign-process/**");
  }

  @Bean
  GroupedOpenApi templateApi() {
    return super.groupedOpenApi("template", "/**/v1/templates/**");
  }

  @Bean
  GroupedOpenApi companyApi() {
    return super.groupedOpenApi("company", "/**/v1/companies/**");
  }

  @Bean
  GroupedOpenApi authApi() {
    return super.groupedOpenApi("auth", "/**/auth/**");
  }

  @Bean
  GroupedOpenApi adminLoginHistory() {
    return super.groupedOpenApi("login-history", "/**/v1/login-history/**");
  }

  @Bean
  GroupedOpenApi companySetting() {
    return super.groupedOpenApi("company-setting", "/**/v1/company/settings/**");
  }

  @Bean
  GroupedOpenApi dataMigration() {
    return super.groupedOpenApi("data-migration", "/**/v1/data-migrations/**");
  }
}
