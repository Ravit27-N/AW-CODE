package com.innovationandtrust.configuration.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * To share common security configuration of each microservice.
 *
 * @version 1.0.0
 * @since 20 March 2023
 */
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, proxyTargetClass = true, jsr250Enabled = true)
public class CommonSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  private static final String JWK_ENDPOINT = "protocol/openid-connect/certs";
  protected final String[] allowedEndpoints = {"/actuator/health", "/actuator/info"};
  private final String jwkSetUri;

  @Getter @Setter private List<String> origins;

  public CommonSecurityConfiguration(final String keycloakUrl, final String keycloakRealm) {
    this.jwkSetUri = String.format("%s/realms/%s/%s", keycloakUrl, keycloakRealm, JWK_ENDPOINT);
  }

  public CommonSecurityConfiguration(
      final String keycloakUrl, final String keycloakRealm, final List<String> origins) {
    this(keycloakUrl, keycloakRealm);
    this.origins = origins;
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) {
    KeycloakAuthenticationProvider authenticationProvider = new KeycloakAuthenticationProvider();
    var grantedAuthority = new SimpleAuthorityMapper();
    grantedAuthority.setPrefix("ROLE_");
    authenticationProvider.setGrantedAuthoritiesMapper(grantedAuthority);
    auth.authenticationProvider(authenticationProvider);
  }

  @Bean
  public CustomAuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  @Bean
  public Converter<Jwt, Collection<GrantedAuthority>> roleConverter() {
    return new KeycloakRealmRoleConverter();
  }

  @Bean
  public AuthenticationEntryPointJwt authenticationEntryPointJwt() {
    return new AuthenticationEntryPointJwt();
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return new AccessDeniedHandlerJwt();
  }

  /**
   * JWT authentication converter.
   *
   * @param authoritiesConverter object of {@link Converter}
   */
  protected Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter(
      @Autowired Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter) {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

    return jwtAuthenticationConverter;
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }

  @Bean
  public KeycloakConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  protected CorsConfigurationSource corsConfigurationSource() {
    var corsConfig = new CorsConfiguration();
    corsConfig.setAllowCredentials(true);
    corsConfig.setAllowedOrigins(origins);
    corsConfig.setMaxAge(3600L);
    corsConfig.addAllowedHeader("*");
    corsConfig.addAllowedMethod("*");
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }

  @Bean
  @ConditionalOnProperty(prefix = "signature.feign-client", name = "enabled", havingValue = "true")
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository registrationRepository,
      OAuth2AuthorizedClientService authorizedClientService) {
    var authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .clientCredentials()
            .refreshToken()
            .build();
    var authorizedClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            registrationRepository, authorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    return authorizedClientManager;
  }
}
