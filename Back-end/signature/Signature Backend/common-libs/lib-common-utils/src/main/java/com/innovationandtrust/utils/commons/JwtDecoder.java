package com.innovationandtrust.utils.commons;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/** Common jwt functionality. */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtDecoder {

  /**
   * Check token expired.
   *
   * @param token the token string
   * @return true if expired
   */
  public static boolean isExpired(String token) {
    if (StringUtils.hasText(token)) {
      DecodedJWT jwt = JWT.decode(token);
      if (!jwt.getExpiresAt().before(new Date())) {
        return false;
      }
    }
    log.info("Token expired!");
    return true;
  }

  public static String getEmail(String token){
    if (StringUtils.hasText(token)) {
      DecodedJWT jwt = JWT.decode(token);
      var email = jwt.getClaims().get("email").toString();
      if (Objects.nonNull(email)) {
        return email;
      }
    }

    return null;
  }
}
