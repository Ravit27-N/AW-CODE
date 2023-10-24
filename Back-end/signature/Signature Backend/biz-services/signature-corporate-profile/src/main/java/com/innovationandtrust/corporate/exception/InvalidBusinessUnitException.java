package com.innovationandtrust.corporate.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Invalid business unit exception. */
@HandledException(status = HttpStatus.BAD_REQUEST, message = "Invalid Business ")
public class InvalidBusinessUnitException extends RuntimeException {
  public InvalidBusinessUnitException(String message) {
    super(message);
  }
}
