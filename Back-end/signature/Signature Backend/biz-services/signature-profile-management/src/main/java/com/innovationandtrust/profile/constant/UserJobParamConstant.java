package com.innovationandtrust.profile.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserJobParamConstant {
  public static final String TIME = "time";
  public static final String USER_ID = "userId";
  public static final String ERROR_USERS = "errorUsers";
  public static final String SOURCE_FILE_PATH = "sourceFilePath";
  public static final String ERROR_FILE_PATH = "errorFilesPath";
  private static final String[] FIELD_HEADER =
      new String[] {
        "firstName",
        "lastName",
        "email",
        "phone",
        "password",
        "functional",
        "userAccessId",
        "businessId"
      };
  public static final List<String> CSV_HEADER = List.of(FIELD_HEADER);
}
