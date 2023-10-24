package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Invalid signature level. */
@HandledException(status = HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends RuntimeException {
  public ForbiddenRequestException(String message) {
    super(message);
  }
}
