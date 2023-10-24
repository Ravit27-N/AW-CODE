package com.innovationandtrust.corporate.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Error file request ")
public class FileRequestException extends RuntimeException {
  public FileRequestException(String message) {
    super(message);
  }
}
