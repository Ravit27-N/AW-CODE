package com.allweb.rms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

  private static final String[] IGNORE_ENDPOINTS = {
      "/v3/api-docs/**",
      "/configuration/ui",
      "/swagger-resources/**",
      "/configuration/security",
      "/swagger-ui/index.html",
      "/webjars/**",
      "/swagger-ui/**",
      "/api/v1/connector/**",
      "/api/v1/company/profile/**",
      "/actuator/info",
      "/actuator/health"
  };
  private final KeycloakRealmRoleConverter keycloakRealmRoleConverter;
  private final AuthenticationEntryPoint authenticationEntryPoint;
  private final UriBearerTokenResolverProxy uriBearerTokenResolverProxy;

  @Autowired
  public SpringSecurityConfig(
      KeycloakRealmRoleConverter keycloakRealmRoleConverter,
      AuthenticationEntryPoint authenticationEntryPoint,
      UriBearerTokenResolverProxy uriBearerTokenResolverProxy) {
    this.keycloakRealmRoleConverter = keycloakRealmRoleConverter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.uriBearerTokenResolverProxy = uriBearerTokenResolverProxy;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .anonymous()
        .disable()
        .cors()
        .and()
        .authorizeHttpRequests(auth -> auth.requestMatchers(IGNORE_ENDPOINTS).permitAll())
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2ResourceServer(
            oauth2ResourceServer ->
                oauth2ResourceServer
                    .bearerTokenResolver(this.uriBearerTokenResolverProxy)
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter()))
                    .accessDeniedHandler(accessDeniedHandler())
                    .authenticationEntryPoint(authenticationEntryPoint))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }

  private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(keycloakRealmRoleConverter);

    return jwtAuthenticationConverter;
  }

  @Bean
  public BearerTokenAccessDeniedHandler accessDeniedHandler() {
    return new BearerTokenAccessDeniedHandler();
  }
}
