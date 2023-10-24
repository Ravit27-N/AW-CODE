package com.innovationandtrust.share.constant;

import java.util.Arrays;

public enum SignatureRole {
  SUPER_ADMIN(RoleConstant.SUPER_ADMIN),
  CORPORATE_ADMIN(RoleConstant.CORPORATE_ADMIN),
  NORMAL_USER(RoleConstant.NORMAL_USER);

  private final String value;

  SignatureRole(String value) {
    this.value = value;
  }

  public static boolean isSystemRole(String... roles) {
    return Arrays.stream(roles)
        .anyMatch(
            role -> Arrays.stream(values()).map(SignatureRole::getValue).toList().contains(role));
  }

  public String getValue() {
    return this.value;
  }
}
