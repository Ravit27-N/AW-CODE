package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.INTERNAL_SERVER_ERROR)
public class MailTemplateException extends RuntimeException {
  public MailTemplateException(String message) {
    super(message);
  }
}
