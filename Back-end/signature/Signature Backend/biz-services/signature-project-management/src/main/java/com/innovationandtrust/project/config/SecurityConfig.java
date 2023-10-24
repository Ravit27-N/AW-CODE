package com.innovationandtrust.project.config;

import com.innovationandtrust.configuration.security.CommonSecurityConfiguration;
import com.innovationandtrust.configuration.security.SecurityProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableConfigurationProperties(SecurityProperty.class)
public class SecurityConfig extends CommonSecurityConfiguration {

  @Lazy
  public SecurityConfig(final SecurityProperty securityProperty) {
    super(
        securityProperty.getKeycloakBaseUrl(),
        securityProperty.getKeycloakRealm(),
        securityProperty.getOrigins());
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
                    /*.hasAnyRole(RoleConstant.NORMAL_USER.toUpperCase())
                    .anyRequest()*/
                    .authenticated())
        .oauth2ResourceServer(
            oauth2ResourceServer ->
                oauth2ResourceServer
                    .jwt(
                        jwt ->
                            jwt.jwtAuthenticationConverter(
                                super.getJwtAuthenticationConverter(super.roleConverter())))
                    .accessDeniedHandler(super.accessDeniedHandler())
                    .authenticationEntryPoint(super.authenticationEntryPointJwt()))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
    return http.build();
  }
}
