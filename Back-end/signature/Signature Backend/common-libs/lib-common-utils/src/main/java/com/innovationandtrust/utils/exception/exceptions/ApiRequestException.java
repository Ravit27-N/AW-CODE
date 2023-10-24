package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Exception handler for api request. */
@HandledException(status = HttpStatus.BAD_REQUEST, message = "Error API request ")
public class ApiRequestException extends RuntimeException {
  public ApiRequestException(String message) {
    super(message);
  }
}
