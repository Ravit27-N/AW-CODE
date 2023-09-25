package com.tessi.cxm.pfl.ms8.config;

import com.tessi.cxm.pfl.shared.auth.KeycloakConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * To configure security.
 *
 * @author Sokhour LACH
 * @since 05/01/2022
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class ProcessControlSecurity extends KeycloakConfiguration {

  private static final String[] PUBLIC_PATH_LIST = {"/v1/public/**", "/actuator/health"};

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.csrf()
        .disable()
        .cors()
        .disable()
        .authorizeRequests()
        .antMatchers(PUBLIC_PATH_LIST).permitAll()
        .antMatchers("v1/**").hasAnyAuthority().anyRequest()
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(PUBLIC_PATH_LIST);
  }
}
