package com.innovationandtrust.sftp.constant;

import java.util.List;

public enum FileProcessingStatus {
  IS_ZIP,
  IS_PDF,
  IS_XML,

  CONTAIN_XML,
  CONTAIN_PDF,

  VALID_FILE_NAME,
  VALID_FILE,
  ERROR_NO_ZIP_FILE,
  ERROR_NO_PDF_FILE,
  ERROR_NO_XML_FILE,

  ERROR_INVALID_FILE_NAME;

  public static boolean isError(FileProcessingStatus status) {
    return List.of(ERROR_NO_ZIP_FILE, ERROR_NO_PDF_FILE, ERROR_NO_XML_FILE, ERROR_INVALID_FILE_NAME)
        .contains(status);
  }
}
