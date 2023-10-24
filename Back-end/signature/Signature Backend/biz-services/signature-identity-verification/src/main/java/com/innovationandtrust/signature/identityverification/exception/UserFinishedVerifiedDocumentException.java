package com.innovationandtrust.signature.identityverification.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Exception class for user finished verified document. */
@HandledException(status = HttpStatus.FORBIDDEN)
public class UserFinishedVerifiedDocumentException extends RuntimeException {
  public UserFinishedVerifiedDocumentException(String message) {
    super(message);
  }
}
