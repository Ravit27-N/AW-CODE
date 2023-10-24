package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Exception handler for Unauthorized project. */
@HandledException(status = HttpStatus.BAD_REQUEST, message = "Unauthorized ")
public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }
}
