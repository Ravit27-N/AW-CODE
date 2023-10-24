package com.innovationandtrust.utils.feignclient;

import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
public class AuthFeignConfig {
  private final IKeycloakTokenExchange iKeycloakTokenExchange;

  public AuthFeignConfig(IKeycloakTokenExchange iKeycloakTokenExchange) {
    this.iKeycloakTokenExchange = iKeycloakTokenExchange;
  }

  protected String getAccessToken() {
    String token;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var request = RequestContextHolder.getRequestAttributes();
    if (authentication != null) {
      log.info("Requesting user is authorized...");
      token = AuthenticationUtils.getAccessToken();
    } else if (Objects.nonNull(request)
        && Objects.nonNull(request.getAttribute(AuthenticationUtils.USER_UUID, 0))) {

      var userUuid = request.getAttribute(AuthenticationUtils.USER_UUID, 0);
      log.info("Requesting user is unauthorized...");
      log.info("Exchange token with user uuid {}", userUuid);
      token = this.iKeycloakTokenExchange.getToken(String.valueOf(userUuid));
    } else {
      log.info("Getting new access token from keycloak client...");
      token = this.iKeycloakTokenExchange.getTokenTechnicalUser();
    }
    return check(token);
  }

  private String check(String token) {
    if (!StringUtils.hasText(token)) {
      throw new IllegalArgumentException("Unable to retrieve access token!");
    }
    return token;
  }
}
