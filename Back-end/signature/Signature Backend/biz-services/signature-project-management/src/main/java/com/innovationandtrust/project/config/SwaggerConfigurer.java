package com.innovationandtrust.project.config;

import com.innovationandtrust.configuration.document.SwaggerConfig;
import com.innovationandtrust.configuration.document.SwaggerProperty;
import com.innovationandtrust.configuration.security.SecurityProperty;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SwaggerProperty.class, KeycloakProperties.class})
public class SwaggerConfigurer extends SwaggerConfig {
  public SwaggerConfigurer(SecurityProperty keycloakProperty, SwaggerProperty swaggerProperty) {
    super(keycloakProperty, swaggerProperty);
  }

  @Bean
  GroupedOpenApi projectApi() {
    return super.groupedOpenApi("project", "/**/v1/projects/**");
  }

  @Bean
  GroupedOpenApi signatoryApi() {
    return super.groupedOpenApi("signatory", "/**/v1/signatories/**");
  }

  @Bean
  GroupedOpenApi documentApi() {
    return super.groupedOpenApi("document", "/**/v1/documents/**");
  }
  @Bean
  GroupedOpenApi projectDetailApi() {
    return super.groupedOpenApi("project-detail", "/**/v1/project-details/**");
  }
}
