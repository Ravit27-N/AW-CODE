package com.tessi.cxm.pfl.ms5.util;

import com.tessi.cxm.pfl.ms5.entity.UserRequestResetPassword;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.keycloak.representations.idm.UserRepresentation;

@Slf4j
public class UserRequestResetPasswordAdapter {

  private static final int MINUTE_TIME = 15;

  private UserRequestResetPasswordAdapter() {
  }

  public static UserRequestResetPassword build(UserRepresentation userRepresentation) {
    return UserRequestResetPassword.builder()
        .keycloakUserId(userRepresentation.getId())
        .email(userRepresentation.getEmail())
        .username(userRepresentation.getUsername())
        .firstName(userRepresentation.getFirstName())
        .lastName(userRepresentation.getLastName())
        .origin(userRepresentation.getOrigin())
        .enabled(userRepresentation.isEnabled())
        .emailVerified(userRepresentation.isEmailVerified())
        .createdTimestamp(userRepresentation.getCreatedTimestamp())
        .token(getTokenGenerator())
        .expiredDate(getExpiredDate())
        .createdAt(new Date())
        .createdBy(userRepresentation.getUsername())
        .build();
  }

  public static String getTokenGenerator() {
    return UUID.randomUUID().toString();
  }

  public static Date getExpiredDate() {
    Date targetTime = Calendar.getInstance().getTime();
    return DateUtils.addMinutes(targetTime, MINUTE_TIME);
  }
}
