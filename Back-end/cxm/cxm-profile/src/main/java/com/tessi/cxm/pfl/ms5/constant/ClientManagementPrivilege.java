package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import lombok.AllArgsConstructor;

/**
 * This enumeration is used to define the privileges of {@link ClientManagementPrivilege}
 *
 * @author Chamrong THOR
 * @since 06/10/2022
 */
@AllArgsConstructor
public enum ClientManagementPrivilege implements BaseEnumPrivilege {
  // privileges of client.
  LIST_USER(setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.LIST),
      "List all clients.", false, false),
  MODIFY_USER(setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.MODIFY),
      "Modify a client.", false, false);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        ClientManagementPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(ClientManagementPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        ClientManagementPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(ClientManagementPrivilege.class, key));
  }

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_CLIENT_MANAGEMENT.concat(
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
