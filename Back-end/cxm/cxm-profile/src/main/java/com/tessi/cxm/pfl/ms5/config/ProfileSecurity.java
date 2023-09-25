package com.tessi.cxm.pfl.ms5.config;

import com.tessi.cxm.pfl.shared.auth.AuthenticationEntryPointJwt;
import com.tessi.cxm.pfl.shared.auth.KeycloakConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class ProfileSecurity extends KeycloakConfiguration {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf()
        .disable()
        .cors()
        .disable()
        .authorizeRequests(
            auth ->
                auth.antMatchers("/api/public/**", "/api/actuator/health")
                    .permitAll()
                    //no need to /api context-path and the full path will be /api/v1/**
                    .antMatchers("/v1/**")
                    .hasAnyAuthority()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling()
        .authenticationEntryPoint(new AuthenticationEntryPointJwt())
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(11);
  }
}
