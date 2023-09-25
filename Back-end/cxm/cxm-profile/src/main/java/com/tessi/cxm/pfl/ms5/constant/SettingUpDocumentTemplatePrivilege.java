package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.SettingUpDocumentTemplate;
import lombok.AllArgsConstructor;

/**
 * This enumeration is used to define the privileges of {@link SettingUpDocumentTemplatePrivilege}
 *
 * @author Sokhour LACH
 * @since 03/01/2022
 */
@AllArgsConstructor
public enum SettingUpDocumentTemplatePrivilege implements BaseEnumPrivilege {
  LIST(setKey(Privilege.LIST), "List the settings", true, false),
  CREATE(setKey(Privilege.CREATE), "Create a model setting", false, false),
  DEFINE_RECOGNITION_CRITERIA(
      setKey(SettingUpDocumentTemplate.DEFINE_RECOGNITION_CRITERIA),
      "Define recognition criteria: type of flow and document model",
      false,
      false),
  ASSOCIATE_DOCUMENTARY(
      setKey(SettingUpDocumentTemplate.ASSOCIATE_DOCUMENTARY),
      "Associate the documentary model with a documentary type, possibly a sector, sub-sector and production criteria",
      false,
      false),
  DEFINE_AND_IDENTIFY(
      setKey(SettingUpDocumentTemplate.DEFINE_AND_IDENTIFY),
      "Define and identify the different areas of the document via the capture wizard or by coordinates: document break, address lines, recipient email, metadata",
      false,
      false),
  EDIT(setKey(Privilege.EDIT), "Editing a setting", true, false),
  MODIFY(setKey(Privilege.MODIFY), "Modify a setting", false, true),
  DELETE(setKey(Privilege.DELETE), "Delete a setting", false, true);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerLevelValidator(
        SettingUpDocumentTemplatePrivilege.class
            .getName()
            .concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(SettingUpDocumentTemplatePrivilege.class, key));
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
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_SETTING_UP_DOCUMENT_TEMPLATE.concat(
        "_".concat(subKey));
  }
}
