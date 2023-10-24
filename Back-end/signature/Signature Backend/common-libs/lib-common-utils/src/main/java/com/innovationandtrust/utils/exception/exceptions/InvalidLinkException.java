package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Invalid link exception. */
@HandledException(status = HttpStatus.BAD_REQUEST)
public class InvalidLinkException extends RuntimeException {
  public InvalidLinkException(String message) {
    super(message);
  }
}
