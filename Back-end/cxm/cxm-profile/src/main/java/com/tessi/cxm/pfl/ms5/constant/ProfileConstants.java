package com.tessi.cxm.pfl.ms5.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class of Profile.
 *
 * @author Sokhour LACH
 */
public final class ProfileConstants {

  private ProfileConstants() {
    // nothing to here.
  }

  public static final String OBJECT_KEY = "key";
  public static final String OBJECT_VALUE = "value";
  public static final String OBJECT_SUB_VALUE = "subValue";
  public static final String SUB_CREATE_TEMPLATE = "subCreateTemplate";
  public static final String EMAILING_TEMPLATE = "emailingTemplatePrivilege";
  public static final String PRIVILEGE_LEVELS = "privilegeLevels";
  public static final String VISIBILITY_LEVEL = "visibilityLevel";
  public static final String MODIFICATION_LEVEL = "modificationLevel";
  public static final String IS_VISIBILITY = "isVisibility";
  public static final String IS_MODIFICATION = "isModification";
  public static final String SUFFIX_VALIDATOR = "-Validator";
  public static final String SUFFIX_LEVEL_VALIDATOR = "-LevelValidator";

  public static final String CLIENT_FILTER = "Filler";

  public static final List<String> CLIENT_FILTERS =
      new ArrayList<>(
          List.of(
              CLIENT_FILTER.concat("1"),
              CLIENT_FILTER.concat("2"),
              CLIENT_FILTER.concat("3"),
              CLIENT_FILTER.concat("4"),
              CLIENT_FILTER.concat("5")));

  public static final class Attribute {

    private Attribute() {
    }

    public static final String FUNCTIONALITIES = "functionalities";
    public static final String CREATED_ON = "createdOn";
    public static final String MODIFIED_ON = "modifiedOn";
    public static final String DISPLAY_NAME = "displayName";
  }
}
