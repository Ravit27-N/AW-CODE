package com.innovationandtrust.process.config;

import com.innovationandtrust.configuration.security.CommonSecurityConfiguration;
import com.innovationandtrust.configuration.security.SecurityProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableConfigurationProperties(value = SecurityProperty.class)
public class SecurityConfig extends CommonSecurityConfiguration {

  @Autowired
  public SecurityConfig(SecurityProperty property) {
    super(property.getKeycloakBaseUrl(), property.getKeycloakRealm(), property.getOrigins());
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .cors(cors -> cors.configurationSource(super.corsConfigurationSource()))
        .securityMatcher("/actuator/**", "/v1/**")
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(this.allowedEndpoints)
                    .permitAll()
                    .requestMatchers("/v1/**")
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .jwt(
                        jwt ->
                            jwt.jwtAuthenticationConverter(
                                super.getJwtAuthenticationConverter(super.roleConverter())))
                    .accessDeniedHandler(super.accessDeniedHandler())
                    .authenticationEntryPoint(super.authenticationEntryPointJwt()))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }
}
