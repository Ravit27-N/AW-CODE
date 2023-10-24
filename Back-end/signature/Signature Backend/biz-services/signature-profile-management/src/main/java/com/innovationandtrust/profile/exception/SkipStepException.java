package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST)
public class SkipStepException extends RuntimeException {
  public SkipStepException(String message) {
    super("Step of job error: " + message);
  }
}
