package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Invalid role exception. */
@HandledException(status = HttpStatus.FORBIDDEN)
public class InvalidRoleException extends RuntimeException {
  public InvalidRoleException(String message) {
    super(message);
  }
}
