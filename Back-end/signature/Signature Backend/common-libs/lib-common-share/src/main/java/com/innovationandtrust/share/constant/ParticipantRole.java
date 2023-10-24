package com.innovationandtrust.share.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public enum ParticipantRole {
  APPROVAL(RoleConstant.ROLE_APPROVAL, RoleConstant.ROLE_API_NG_APPROVAL, 1),

  SIGNATORY(RoleConstant.ROLE_SIGNATORY, RoleConstant.ROLE_API_NG_SIGN, 2),

  RECEIPT(RoleConstant.ROLE_RECEIPT, RoleConstant.ROLE_API_NG_RECEIPT, 3),
  VIEWER(RoleConstant.ROLE_VIEWER, RoleConstant.ROLE_VIEWER, 4),
  ;

  private static final Map<String, ParticipantRole> BY_ROLE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_ROLE.put(v.role, v));
  }

  @Getter private final String role;
  @Getter private final String apiNgRole;
  @Getter private final int order;

  ParticipantRole(String role, String apiNgRole, int order) {
    this.role = role;
    this.apiNgRole = apiNgRole;
    this.order = order;
  }

  public static ParticipantRole getByRole(String role) {
    return BY_ROLE.get(role);
  }

  public static boolean isValidRole(String role) {
    var key = BY_ROLE.get(role);
    return key != null;
  }
}
