package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST)
public class InvalidTemplateException extends IllegalArgumentException {
  public InvalidTemplateException(String message) {
    super(message);
  }

  public InvalidTemplateException() {
    super("Invalid template!");
  }
}
