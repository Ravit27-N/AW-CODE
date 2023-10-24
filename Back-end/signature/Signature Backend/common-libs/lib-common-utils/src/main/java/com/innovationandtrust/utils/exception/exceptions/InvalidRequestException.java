package com.innovationandtrust.utils.exception.exceptions;


import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Invalid signature level. */
@HandledException(status = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {
  public InvalidRequestException(String message) {
    super(message);
  }
}
