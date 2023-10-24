package com.innovationandtrust.utils.authenticationUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationUtils {

  public static final String RESOURCE_ACCESS = "resource_access";
  public static final String PREFERRED_USER = "preferred_user";
  public static final String SUBJECT = "sub";
  public static final String EMAIL_VERIFY = "email_verified";
  public static final String ACCOUNT = "account";
  public static final String ROLES = "roles";
  public static final String USER_COMPANY = "USER_COMPANY";
  public static final String USER_ID = "USER_ID";
  public static final String USER_UUID = "USER_UUID";
  public static final String X_GRAVITEE_TRANSACTION = "X-Gravitee-Transaction-Id";
  public static final String PREFERRED_USERNAME = "preferred_username";
  public static final String EMAIL = "email";
  public static final String NAME = "name";
  public static final String EXPIRE = "exp";
  public static final String REALM_ACCESS = "realm_access";
  private static final String message = "There is no attribute {} for this authorized user...";

  /**
   * Extract authenticated user from Spring security context.
   *
   * @return {@link AuthenticatedUser}
   */
  public static AuthenticatedUser getAuthenticatedUser(Authentication authentication) {
    if (authentication != null) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      Map<String, Object> claims = jwt.getClaims();
      return new AuthenticatedUser(
          String.valueOf(claims.get(SUBJECT)),
          String.valueOf(claims.get(EMAIL_VERIFY)),
          getAuthorities(jwt));
    }
    throw new BadCredentialsException("Bad credentials");
  }

  private static List<String> getAuthorities(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
    Map<String, Object> account =
        new ObjectMapper().convertValue(resourceAccess.get(ACCOUNT), new TypeReference<>() {});
    return Collections.singletonList(String.valueOf(account.get(ROLES)));
  }

  public static HttpServletRequest getRequestHeaders() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    return Objects.nonNull(requestAttributes)
        ? ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest()
        : null;
  }

  private static String getClaimByKey(String key) {
    Jwt jwt = getJwt();
    Map<String, Object> claims = Objects.nonNull(jwt) ? jwt.getClaims() : Collections.emptyMap();
    if (!claims.isEmpty()) {
      var claim = claims.get(key);
      return Objects.nonNull(claim) ? claim.toString() : null;
    }

    return null;
  }

  private static Jwt getJwt() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.info("Requesting user is authorized...");
      return (Jwt) authentication.getPrincipal();
    }

    log.info("Requesting user is unauthorized...");
    return null;
  }

  /**
   * To set user uuid for feign client exchange access token.
   *
   * @param userUuid refers to incoming request user uuid
   */
  public static void setUserUuid(String userUuid) {
    log.info("Setting user uuid to request context holder...");
    var request = RequestContextHolder.getRequestAttributes();
    if (Objects.nonNull(request)) {
      request.setAttribute(USER_UUID, userUuid, 0);
    }
  }

  public static String getAccessToken() {
    log.info("Getting user access token from JWT...");
    Jwt jwt = getJwt();
    return Objects.nonNull(jwt) ? jwt.getTokenValue() : null;
  }

  public static String getUserUuid() {
    log.info("Getting user uuid from token...");
    Jwt jwt = getJwt();
    return Objects.nonNull(jwt) ? jwt.getClaim(SUBJECT) : null;
  }

  public static String getUserEmail() {
    log.info("Getting user email from token...");
    Jwt jwt = getJwt();
    return Objects.nonNull(jwt) ? jwt.getClaim(EMAIL) : null;
  }

  public static String getUserName() {
    log.info("Getting user username from token...");
    Jwt jwt = getJwt();
    return Objects.nonNull(jwt) ? jwt.getClaim(PREFERRED_USERNAME) : null;
  }

  public static Set<String> getUserRoles() {
    log.info("Getting user ream roles from token...");
    Jwt jwt = getJwt();
    if (Objects.isNull(jwt)) {
      log.warn("Cannot get user ream roles...");
      return Collections.emptySet();
    }
    Map<String, Object> reamAccess = jwt.getClaim(REALM_ACCESS);
    Set<String> roles =
        new ObjectMapper().convertValue(reamAccess.get(ROLES), new TypeReference<>() {});
    if (Objects.isNull(roles)) {
      log.warn("Cannot get user ream roles...");
      return Collections.emptySet();
    }
    return roles;
  }

  public static Long getCompanyId() {
    log.info("Getting company id from token...");
    var companyId = getCompanyValue(0);
    return companyId != null ? Long.valueOf(Objects.requireNonNull(companyId)) : null;
  }

  public static String getCompanyName() {
    log.info("Getting company name from token...");
    return getCompanyValue(1);
  }

  public static String getCompanyUuid() {
    log.info("Getting company uuid from token...");
    return getCompanyValue(2);
  }

  private static String getCompanyValue(int index) {
    // Company Info Example: "USER_COMPANY" = "<CompanyId>;<CompanyName>;<CompanyUuid>"
    String userCompany = getClaimByKey(USER_COMPANY);
    if (!StringUtils.hasText(userCompany)) {
      log.warn(message, USER_COMPANY);
      return null;
    }
    try {
      return userCompany.split(";")[index];
    } catch (Exception ignored) {
      log.warn("Warning no attribute found in your specific index {}", index);
      return null;
    }
  }

  public static Long getUserId() {
    log.info("Getting authorized user id...");
    String userId = getClaimByKey(USER_ID);
    if (!StringUtils.hasText(userId)) {
      log.warn(message, USER_ID);
      return null;
    }
    return Long.valueOf(Objects.requireNonNull(userId));
  }

  public static String getUserFullName() {
    log.info("Getting authorized user id...");
    String name = getClaimByKey(NAME);
    if (!StringUtils.hasText(name)) {
      log.warn(message, NAME);
      return null;
    }
    return name;
  }

  public static String getGraviteeTransaction() {
    HttpServletRequest request = getRequestHeaders();
    if (Objects.isNull(request)) {
      log.warn("There is no header name {} in request headers", X_GRAVITEE_TRANSACTION);
      return "";
    }
    var headers = request.getHeaders(X_GRAVITEE_TRANSACTION);
    return headers.hasMoreElements() ? headers.nextElement() : "";
  }
}
