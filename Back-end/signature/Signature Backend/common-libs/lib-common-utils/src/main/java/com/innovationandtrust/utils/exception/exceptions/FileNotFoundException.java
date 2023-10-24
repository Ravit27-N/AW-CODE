package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.NOT_FOUND, key = StatusKeyConstant.FILE_NOT_FOUND)
public class FileNotFoundException extends RuntimeException {
  public FileNotFoundException(String message) {
    super(message);
  }
}
