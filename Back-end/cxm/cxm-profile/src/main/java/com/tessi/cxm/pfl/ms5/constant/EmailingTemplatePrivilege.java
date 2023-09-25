package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EmailingTemplatePrivilege implements BaseEnumPrivilege {
  LIST_MODEL(setKey(Privilege.LIST), "List the model", true, false),
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
        EmailingTemplatePrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(EmailingTemplatePrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        EmailingTemplatePrivilege.class.getName().concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(EmailingTemplatePrivilege.class, key));
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
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_TEMPLATE_MODEL.concat(
        "_".concat(subKey));
  }
}
