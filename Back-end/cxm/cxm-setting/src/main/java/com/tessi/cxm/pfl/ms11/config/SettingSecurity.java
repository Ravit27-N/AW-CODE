package com.tessi.cxm.pfl.ms11.config;

import com.tessi.cxm.pfl.shared.auth.KeycloakConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SettingSecurity extends KeycloakConfiguration {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);

    http.csrf()
        .disable()
        .cors()
        .disable()
        .authorizeRequests()
        .antMatchers("/api/v1/**")
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
