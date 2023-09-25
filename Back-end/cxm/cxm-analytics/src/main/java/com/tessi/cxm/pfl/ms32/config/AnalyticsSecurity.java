package com.tessi.cxm.pfl.ms32.config;

import com.tessi.cxm.pfl.shared.auth.KeycloakConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * To configure security
 *
 * @author Sokhour LACH
 * @since 26/01/2023
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class AnalyticsSecurity extends KeycloakConfiguration {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf()
        .disable()
        .cors()
        .disable()
        .authorizeRequests()
        .antMatchers("/actuator/health").permitAll()
        .antMatchers("/api/v1/**").hasAnyAuthority().anyRequest()
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
