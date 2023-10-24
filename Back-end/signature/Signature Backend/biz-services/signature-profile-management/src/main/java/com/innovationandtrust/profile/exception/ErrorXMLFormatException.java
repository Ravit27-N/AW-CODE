package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST)
public class ErrorXMLFormatException extends RuntimeException {
  public ErrorXMLFormatException(String message) {
    super(message);
  }
}
