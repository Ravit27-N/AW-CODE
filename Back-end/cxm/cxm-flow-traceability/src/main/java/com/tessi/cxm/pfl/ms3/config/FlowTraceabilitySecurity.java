package com.tessi.cxm.pfl.ms3.config;

import com.tessi.cxm.pfl.shared.auth.KeycloakConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class FlowTraceabilitySecurity extends KeycloakConfiguration {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf()
        .disable()
        .cors()
        .disable()
        .authorizeRequests()
        .antMatchers("/api/v1/**")
        .hasAuthority("TEST_2")
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
