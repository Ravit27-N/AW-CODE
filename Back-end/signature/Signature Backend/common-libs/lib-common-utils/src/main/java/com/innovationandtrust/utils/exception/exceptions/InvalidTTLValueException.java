package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST)
public class InvalidTTLValueException extends RuntimeException {
  public InvalidTTLValueException(String message) {
    super(message);
  }
}
