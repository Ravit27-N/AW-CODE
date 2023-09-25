package com.allweb.rms.security.utils;

import com.allweb.rms.exception.EmailNotFoundException;
import com.allweb.rms.security.AuthenticatedUser;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

  /**
   * Extract authenticated user from Spring security context.
   *
   * @return {@link AuthenticatedUser}
   */
  public AuthenticatedUser getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      Map<String, Object> claims = jwt.getClaims();
      String email = String.valueOf(claims.get("email_verified"));
      if (email.equals("null")) {
        throw new EmailNotFoundException();
      }
      return new AuthenticatedUser(
          claims.get("preferred_username").toString(), email, getAuthorities(jwt));
    }
    throw new BadCredentialsException("Bad credentials");
  }

  @SuppressWarnings("unchecked")
  private List<String> getAuthorities(Jwt jwt) {
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");
    return Collections.singletonList(String.valueOf(account.get("roles")));
  }
}
