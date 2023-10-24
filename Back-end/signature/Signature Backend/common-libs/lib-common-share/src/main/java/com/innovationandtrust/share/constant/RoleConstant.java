package com.innovationandtrust.share.constant;

import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleConstant {

  public static final String SUPER_ADMIN = "super-admin";

  public static final String CORPORATE_ADMIN = "corporate-admin";

  public static final String NORMAL_USER = "end-user";

  public static final String ROLE_SIGNATORY = "signatory";

  public static final String ROLE_APPROVAL = "approval";

  public static final String ROLE_RECEIPT = "receipt";

  public static final String ROLE_VIEWER = "viewer";

  public static final String ROLE_API_NG_SIGN = "sign";

  public static final String ROLE_API_NG_APPROVAL = "approval";

  public static final String ROLE_API_NG_RECEIPT = "to";
  public static final String SIGN_COMPLETED = "SIGN_COMPLETED";

  public static Set<String> getPrivilegeRoles() {
    return Set.of(SUPER_ADMIN, CORPORATE_ADMIN);
  }

  public static boolean isPrivilegeUser(Set<String> roles) {
    return isCorporateUser(roles) || isSuperAdmin(roles);
  }

  public static boolean isCorporateUser(Set<String> roles) {
    if (roles.isEmpty()) {
      return false;
    }
    return roles.contains(RoleConstant.CORPORATE_ADMIN);
  }

  public static boolean isSuperAdmin(Set<String> roles) {
    if (roles.isEmpty()) {
      return false;
    }
    return roles.contains(RoleConstant.SUPER_ADMIN);
  }
}
