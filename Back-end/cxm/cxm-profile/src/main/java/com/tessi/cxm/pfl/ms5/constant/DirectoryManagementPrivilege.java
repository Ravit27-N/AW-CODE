package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.util.EnumUtils;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import lombok.AllArgsConstructor;

/**
 * Enumeration of privileges of <b>Directory</b>
 */
@AllArgsConstructor
public enum DirectoryManagementPrivilege implements BaseEnumPrivilege {

  /**
   * Directory definition.
   */
  LIST_DIRECTORY_DEFINITION(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryDefinition.LIST),
      "List all directory.", true, false),
  CREATE_DIRECTORY_DEFINITION(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryDefinition.CREATE),
      "Create a directory.", false, false),
  MODIFY_DIRECTORY_DEFINITION(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryDefinition.MODIFY),
      "Modify a directory.", false, true),
  DELETE_DIRECTORY_DEFINITION(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryDefinition.DELETE),
      "Delete a directory.", false, true),
  EDIT_DIRECTORY_DEFINITION(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryDefinition.EDIT),
      "Edit a directory.", true, false),

  /**
   * Directory feed.
   */
  LIST_USER_DIRECTORY(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.LIST),
      "List all directories of user's client.",
      true,
      false),
  MANUAL_POPULATE_DIRECTORY(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.MANUAL_POPULATE),
      "Populate a directory by manually adding data.",
      false,
      true),
  IMPORT_DIRECTORY_DATA(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.IMPORT),
      "Populate a directory by importing a data file.",
      false,
      true),
  MODIFY_DIRECTORY_DATA(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.MODIFY),
      "Modify existing data from a directory.",
      false,
      true),
  DELETE_DIRECTORY_DATA(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.DELETE),
      "Delete existing data from a directory.",
      false,
      true),
  EDIT_DIRECTORY_DATA(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.EDIT),
      "Edit a directory (enables privileges for the following sub-feature).",
      true,
      false),
  EXPORT_DIRECTORY_DATA(
      setKey(com.tessi.cxm.pfl.shared.utils.ProfileConstants.DirectoryFeed.EXPORT),
      "Export a directory.", true, false);

  private final String key;
  private final String value;
  private final boolean isVisibility;
  private final boolean isModification;

  static {
    PrivilegeKeyValidator.registerKeyExistingValidator(
        DirectoryManagementPrivilege.class.getName().concat(ProfileConstants.SUFFIX_VALIDATOR),
        key -> EnumUtils.keyExists(DirectoryManagementPrivilege.class, key));

    PrivilegeKeyValidator.registerLevelValidator(
        DirectoryManagementPrivilege.class.getName()
            .concat(ProfileConstants.SUFFIX_LEVEL_VALIDATOR),
        key -> EnumUtils.getPrivilegeLevel(DirectoryManagementPrivilege.class, key));
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
    return com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_DIRECTORY_MANAGEMENT.concat(
        "_".concat(value));
  }
}
