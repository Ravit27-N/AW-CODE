package com.innovationandtrust.corporate.config;

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
  public SwaggerConfigurer(SecurityProperty keycloakProperty, SwaggerProperty swaggerProperty) {
    super(keycloakProperty, swaggerProperty);
  }

  @Bean
  GroupedOpenApi businessUnitApi() {
    return super.groupedOpenApi("business-unit", "/**/v1/business-units/**");
  }

  @Bean
  GroupedOpenApi corporateUserApi() {
    return super.groupedOpenApi("company-detail", "/**/v1/company/details/**");
  }

  @Bean
  GroupedOpenApi corporateSettingApi() {
    return super.groupedOpenApi("corporate-setting", "/**/v1/corporate/settings/**");
  }

  @Bean
  GroupedOpenApi corporateSettingPublicApi() {
    return super.groupedOpenApi("corporate-setting-public", "/**/corporate-settings/**");
  }

  @Bean
  GroupedOpenApi employeeApi() {
    return super.groupedOpenApi("employee", "/**/v1/employees/**");
  }

  @Bean
  GroupedOpenApi userAccess() {
    return super.groupedOpenApi("user-access", "/**/v1/user-access/**");
  }

  @Bean
  GroupedOpenApi templateDetailApi() {
    return super.groupedOpenApi("template-detail", "/**/v1/template-details/**");
  }

  @Bean
  GroupedOpenApi dashboardApi() {
    return super.groupedOpenApi("dashboard", "/**/v1/dashboard/**");
  }

  @Bean
  GroupedOpenApi folderApi() {
    return super.groupedOpenApi("folder", "/**/v1/folders/**");
  }
}
