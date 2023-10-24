package com.innovationandtrust.utils.chain.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class BearerAuthentication {

  public static final String PREFIX_TOKEN = "Bearer ";
  public static final String UNAUTHORIZED = "Unauthorized";
  public static final String FAKE_USERNAME = PREFIX_TOKEN + UNAUTHORIZED;
  private BearerAuthentication() {}

  /**
   * To get token from {@link HttpHeaders} without bearer prefix.
   *
   * @param headers object of {@link HttpHeaders}
   * @return token as {@link String}
   */
  public static String getToken(HttpHeaders headers) {
    var authorizationToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authorizationToken)) {
      authorizationToken = authorizationToken.replace(BearerAuthentication.PREFIX_TOKEN, "");
    }
    return authorizationToken;
  }

  /**
   * To get token from {@link HttpHeaders} with bearer prefix.
   *
   * @param headers object of {@link HttpHeaders}
   * @return token as {@link String}
   */
  public static String getTokenWithPrefix(HttpHeaders headers) {
    var authorizationToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authorizationToken)) {
      return authorizationToken;
    }
    throw new IllegalStateException("Http header not contain any authorization header!");
  }
}
