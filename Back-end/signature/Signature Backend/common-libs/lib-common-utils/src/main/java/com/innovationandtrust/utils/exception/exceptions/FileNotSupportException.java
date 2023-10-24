package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST, key = StatusKeyConstant.FILE_NOT_SUPPORT)
public class FileNotSupportException extends RuntimeException {
  public FileNotSupportException() {
    super("File not supported.");
  }

  public FileNotSupportException(String type) {
    super("This file type " + type + " is not supported.");
  }
}
