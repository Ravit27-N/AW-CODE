package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

/** Exception handler for feign client. */
@HandledException(status = HttpStatus.INTERNAL_SERVER_ERROR)
public class FeignClientException extends RuntimeException {
  public FeignClientException(String message) {
    super(message);
  }
}
