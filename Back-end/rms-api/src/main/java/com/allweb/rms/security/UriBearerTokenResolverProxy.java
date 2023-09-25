package com.allweb.rms.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class UriBearerTokenResolverProxy implements BearerTokenResolver {
  private final DefaultBearerTokenResolver defaultTokenResolver = new DefaultBearerTokenResolver();

  @Override
  public String resolve(HttpServletRequest request) {
    String antPattern = "**/api/v1/candidate/*/view/**";
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    if (antPathMatcher.match(antPattern, request.getRequestURL().toString())) {
      defaultTokenResolver.setAllowUriQueryParameter(true);
      return defaultTokenResolver.resolve(request);
    }
    this.defaultTokenResolver.setAllowUriQueryParameter(false);
    return defaultTokenResolver.resolve(request);
  }
}
