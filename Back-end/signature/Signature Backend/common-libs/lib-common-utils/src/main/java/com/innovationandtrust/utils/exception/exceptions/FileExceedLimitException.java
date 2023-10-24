package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST, key = StatusKeyConstant.FILE_EXCEED_LIMIT)
public class FileExceedLimitException extends RuntimeException {
  public FileExceedLimitException() {
    super("File size exceeds the limit.");
  }
}
