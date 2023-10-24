package com.innovationandtrust.project.config;

import com.innovationandtrust.utils.keycloak.config.KeycloakConfig;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value =  {KeycloakProperties.class})
public class KeycloakConfigurer extends KeycloakConfig {
    public KeycloakConfigurer(final KeycloakProperties properties) {
        super(properties);
    }
}
