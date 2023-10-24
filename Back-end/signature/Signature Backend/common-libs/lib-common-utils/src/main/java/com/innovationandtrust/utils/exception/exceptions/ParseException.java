package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Parse Exception ")
public class ParseException extends RuntimeException {
  public ParseException(String message) {
    super(message);
  }
}
