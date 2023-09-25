package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommunicationInteractivePrivilege implements BaseEnumPrivilege {

  LIST_MODEL(setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.CommunicationInteractive.LIST),
      "List all interactive model.", true, false);


  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        CommunicationInteractivePrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(CommunicationInteractivePrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        CommunicationInteractivePrivilege.class.getName()
            .concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(CommunicationInteractivePrivilege.class, key));
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

  private static String setKey(String value) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_COMMUNICATION_INTERACTIVE.concat(
        "_".concat(value));
  }
}
