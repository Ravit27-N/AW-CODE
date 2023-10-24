package com.innovationandtrust.signature.identityverification.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Exception class for user in status banned. */
@HandledException(status = HttpStatus.FORBIDDEN)
public class UserNotAllowedException extends RuntimeException {
  public UserNotAllowedException(String message) {
    super(message);
  }
}
