package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ManagementLibraryResourcePrivilege implements BaseEnumPrivilege {
  LIST(setKey(Privilege.LIST), "List the resources", true, false),
  CREATE(setKey(Privilege.CREATE), "Create a resource", false, false),
  DELETE(setKey(Privilege.DELETE), "Delete a resource", false, true);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        ManagementLibraryResourcePrivilege.class
            .getName()
            .concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(ManagementLibraryResourcePrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        ManagementLibraryResourcePrivilege.class
            .getName()
            .concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(ManagementLibraryResourcePrivilege.class, key));
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public boolean isVisibility() {
    return isVisibility;
  }

  public boolean isModification() {
    return isModification;
  }

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE.concat(
        "_".concat(subKey));
  }
}
