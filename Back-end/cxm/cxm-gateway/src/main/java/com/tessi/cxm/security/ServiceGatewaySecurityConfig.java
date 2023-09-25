package com.tessi.cxm.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class ServiceGatewaySecurityConfig {

  private static final List<String> PUBLIC_PATH_LIST =
      List.of("/microservice-info/**", "/cxm-profile/api/public/**",
          "/cxm-campaign/api/v1/public/**", "/cxm-hub-digitalflow/**",
          "/cxm-process-control/api/v1/public/**");

  /**
   * Gateway security configuration.
   *
   * @param http ServerHttpSecurity
   * @return {@link SecurityWebFilterChain}
   */
  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    var publicPaths = PUBLIC_PATH_LIST.toArray(new String[0]);
    http.cors()
        .and()
        .securityMatcher(
            new NegatedServerWebExchangeMatcher(
                ServerWebExchangeMatchers.pathMatchers(publicPaths)))
        .authorizeExchange(
            exchanges ->
                exchanges.pathMatchers(publicPaths).permitAll().anyExchange().authenticated())
        .oauth2Client()
        .and()
        .oauth2Login(withDefaults())
        .oauth2ResourceServer()
        .jwt();

    http.csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());
    http.csrf(CsrfSpec::disable);

    return http.build();
  }
}
