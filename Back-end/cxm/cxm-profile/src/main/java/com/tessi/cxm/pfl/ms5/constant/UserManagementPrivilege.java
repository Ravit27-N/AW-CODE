package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserManagementPrivilege implements BaseEnumPrivilege {
  // privileges of user.
  LIST_USER(setKey(UserManagementConstants.User.LIST), "List all users.", true, false),
  CREATE_USER(setKey(UserManagementConstants.User.CREATE), "Create a user.", false, false),
  MODIFY_USER(setKey(UserManagementConstants.User.MODIFY), "Modify a user.", false, true),
  DELETE_USER(setKey(UserManagementConstants.User.DELETE), "Delete a user.", false, true),
  EDIT(setKey(UserManagementConstants.User.EDIT), "Edit a user.", true, false),

  // privileges of profile.
  CREATE_PROFILE(setKey(UserManagementConstants.Profile.CREATE), "Create a profile.", false, false),
  MODIFY_PROFILE(setKey(UserManagementConstants.Profile.MODIFY), "Modify a profile.", false, true),
  LIST_PROFILE(setKey(UserManagementConstants.Profile.LIST), "List all profiles.", true, false),
  DELETE_PROFILE(setKey(UserManagementConstants.Profile.DELETE), "Delete a profile.", false, true),
  EDIT_PROFILE(setKey(UserManagementConstants.Profile.EDIT), "Edit a profile.", true, false);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        UserManagementPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(UserManagementPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        UserManagementPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(UserManagementPrivilege.class, key));
  }

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT.concat(
        "_".concat(subKey));
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public boolean isVisibility() {
    return isVisibility;
  }

  @Override
  public boolean isModification() {
    return isModification;
  }
}
