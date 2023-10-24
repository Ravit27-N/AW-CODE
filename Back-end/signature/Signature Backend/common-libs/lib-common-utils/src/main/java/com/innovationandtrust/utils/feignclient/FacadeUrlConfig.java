package com.innovationandtrust.utils.feignclient;

import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import feign.RequestTemplate;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@NoArgsConstructor
public class FacadeUrlConfig implements FacadeUrl {
  private String userUuid;
  private String uuid;

  @Autowired private IKeycloakProvider keycloakProvider;

  public String getUuid() {
    this.setAuthenticatedUserId();
    return this.uuid;
  }

  public String getUserUuid() {
    this.setAuthenticatedUserId();
    return this.userUuid;
  }

  public void setUserUuid(String userUuid) {
    this.userUuid = userUuid;
  }

  public void facadeRequest(RequestTemplate requestTemplate) {
    var url = this.getFacadeUrl(requestTemplate);
    if (Objects.nonNull(url)) {
      requestTemplate.uri(url);
      requestTemplate.target(this.getBaseUrl(requestTemplate));
    }
  }

  private String getFacadeUrl(RequestTemplate requestTemplate) {
    var baseurlOnly = this.getBaseUrl(requestTemplate);
    var noBasePath = requestTemplate.feignTarget().url().replace(baseurlOnly, "");
    var url = requestTemplate.request().url();
    if (this.isRetryUrl(url)) {
      return null;
    }
    String facadeUrl;
    if (!containContextPath(noBasePath)) {
      facadeUrl = URI.create(noBasePath + "/" + url).normalize().toString();
    } else {
      var companyUuid = this.getUuid() + "/";
      if (Objects.equals(this.uuid, "null") || !StringUtils.hasText(this.uuid)) {
        log.warn("company UUID is null");
        companyUuid = "";
      }
      facadeUrl = URI.create(companyUuid + noBasePath + "/" + url).normalize().toString();
    }
    log.info("Facade url: " + facadeUrl);
    return normalizeUri(facadeUrl);
  }

  private String getBaseUrl(RequestTemplate requestTemplate) {
    return normalizeUri(CommonUsages.getBaseUrlFromString(requestTemplate.feignTarget().url()));
  }

  private boolean isRetryUrl(String currentUrl) {
    // the value of retrying url contains the baseURL
    return StringUtils.hasText(CommonUsages.getBaseUrlFromString(currentUrl));
  }

  private boolean containContextPath(String url) {
    var containApi = url.contains("api/");
    // to prevent with context path contain "api", such as "api-ng"
    var contextAndPrefix = url.split(containApi ? "api/" : "api");
    if (contextAndPrefix.length == 0) {
      return false;
    } else {
      return StringUtils.hasText(contextAndPrefix[0]) && !Objects.equals(contextAndPrefix[0], "/");
    }
  }

  private void setAuthenticatedUserId() {
    try {
      this.getUserInfo()
          .ifPresent(
              value ->
                  this.uuid =
                      Objects.nonNull(value.getSystemUser().getCompany())
                          ? value.getSystemUser().getCompany().getUuid()
                          : null);
    } catch (Exception e) {
      log.warn("Unable to set companyUuid and userId " + e.getMessage());
    }
  }

  private Optional<KeycloakUserResponse> getUserInfo() {
    try {
      String reqUserUuid = this.getAuthenticatedUserUuid();
      var request = RequestContextHolder.getRequestAttributes();
      if (Objects.isNull(reqUserUuid) && Objects.nonNull(request)) {
        reqUserUuid = String.valueOf(request.getAttribute(AuthenticationUtils.USER_UUID, 0));
      }
      if (Objects.isNull(reqUserUuid)) {
        // This statement must be the last option
        reqUserUuid = this.userUuid;
      }
      log.info("User uuid: " + reqUserUuid);
      return this.keycloakProvider.getUserInfo(reqUserUuid);
    } catch (Exception e) {
      log.warn("Error getting user info in keycloak:" + e.getMessage());
    }
    return Optional.empty();
  }

  private String getAuthenticatedUserUuid() {
    try {
      return AuthenticationUtils.getAuthenticatedUser(
              SecurityContextHolder.getContext().getAuthentication())
          .getUuid();
    } catch (Exception e) {
      log.warn("Error getting authenticated user uuid: " + e.getMessage());
      return null;
    }
  }

  public String normalizeUri(String uri) {
    return URI.create(uri).normalize().toString();
  }
}
