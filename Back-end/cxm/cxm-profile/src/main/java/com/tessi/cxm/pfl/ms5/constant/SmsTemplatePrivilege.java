package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import lombok.AllArgsConstructor;

/**
 * Enumeration of Design Sms Template.
 *
 * @author Sokhour LACH
 */
@AllArgsConstructor
public enum SmsTemplatePrivilege implements BaseEnumPrivilege {
  LIST_EXITING_MODEL(setKey(Privilege.LIST), "List exiting models", true, false),
  FROM_SCRATCH(
      setKey(Privilege.SubCreateTemplate.FROM_SCRATCH),
      "Create a template from scratch",
      false,
      false),
  BY_DUPLICATE(
      setKey(Privilege.SubCreateTemplate.BY_DUPLICATE),
      "Create a template by duplicate",
      true,
      false),
  EDITING_TEMPLATE(setKey(Privilege.EDIT), "Editing a template", true, false),
  MODIFY_MODEL(setKey(Privilege.MODIFY), "Modify a model", false, true),
  DELETE_MODEL(setKey(Privilege.DELETE), "Delete a model", false, true);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        SmsTemplatePrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(SmsTemplatePrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        SmsTemplatePrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(SmsTemplatePrivilege.class, key));
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
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_SMS_TEMPLATE.concat(
        "_".concat(subKey));
  }
}
