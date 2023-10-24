package com.innovationandtrust.signature.identityverification.config;

import com.innovationandtrust.configuration.document.SwaggerConfig;
import com.innovationandtrust.configuration.document.SwaggerProperty;
import com.innovationandtrust.configuration.security.SecurityProperty;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger configuration class. */
@Configuration
@EnableConfigurationProperties({SecurityProperty.class, SwaggerProperty.class})
public class SwaggerConfigurer extends SwaggerConfig {
  public SwaggerConfigurer(SecurityProperty keycloakProperty, SwaggerProperty swaggerProperty) {
    super(keycloakProperty, swaggerProperty);
  }

  @Bean
  GroupedOpenApi dossierApi() {
    return super.groupedOpenApi("dossier", "/v1/dossier/**");
  }

  @Bean
  GroupedOpenApi shareIdApi() {
    return super.groupedOpenApi("share-id", "/v1/shareid/**");
  }
}
