package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import lombok.AllArgsConstructor;

/**
 * This enumeration is used to define the privileges of {@link EspaceValidationPrivilege}
 *
 * @author Chamrong THOR
 * @since 08/09/2022
 */
@AllArgsConstructor
public enum EspaceValidationPrivilege implements BaseEnumPrivilege {
  LIST(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.EspaceValidation.LIST),
      "List flows has status to-validate",
      true,
      false),
  VALIDATE_OR_REFUSE(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.EspaceValidation.VALIDATE_OR_REFUSE),
      "validate or refuse flows has status to-validate",
      false,
      true);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        EspaceValidationPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(EspaceValidationPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        EspaceValidationPrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(EspaceValidationPrivilege.class, key));
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

  private static String setKey(String subKey) {
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_ESPACE_VALIDATION.concat(
        "_".concat(subKey));
  }
}
