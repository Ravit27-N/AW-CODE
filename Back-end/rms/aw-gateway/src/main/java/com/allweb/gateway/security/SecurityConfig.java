package com.allweb.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    // to redirect to oauth2 login page.
    http.csrf()
        .disable()
        .authorizeExchange()
        .pathMatchers("/rms-service/api/v1/company/profile", "/rms-service/api/v1/connector/**")
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .oauth2Client()
        .and()
        .oauth2Login(Customizer.withDefaults())
        .logout(Customizer.withDefaults())
        .oauth2Client()
        .and()
        .oauth2ResourceServer()
            // bearerTokenConverter use to check if the token is vail or not
        .bearerTokenConverter(serverAuthenticationConverter())
        .jwt();
    return http.build();
  }

  private ServerAuthenticationConverter serverAuthenticationConverter() {
    ServerBearerTokenAuthenticationConverter serverAuthenticationConverter =
        new ServerBearerTokenAuthenticationConverter();
    serverAuthenticationConverter.setAllowUriQueryParameter(true);
    return serverAuthenticationConverter;
  }
}
