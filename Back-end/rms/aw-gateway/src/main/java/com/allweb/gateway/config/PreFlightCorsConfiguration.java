package com.allweb.gateway.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Component
public class PreFlightCorsConfiguration {

  @Value("${cors.allowed.origins}")
  private List<String> corsOriginConfigs;

  @Bean
  public CorsWebFilter corsFilter(@Autowired CorsConfigurationSource corsConfigurationSource) {
    return new CorsWebFilter(corsConfigurationSource);
  }

  @Bean
  public CorsResponseHeaderFilter corsResponseHeaderFilter() {
    return new CorsResponseHeaderFilter();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    var corsConfig = new CorsConfiguration();
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedOrigins(corsOriginConfigs);
    corsConfig.setMaxAge(3600L);
    corsConfig.addAllowedHeader("*");
    corsConfig.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }
}
