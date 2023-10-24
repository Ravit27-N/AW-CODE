package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException{
  public AccessDeniedException() {
    super("Access denied");
  }

  public AccessDeniedException(String message) {
    super(message);
  }
}
