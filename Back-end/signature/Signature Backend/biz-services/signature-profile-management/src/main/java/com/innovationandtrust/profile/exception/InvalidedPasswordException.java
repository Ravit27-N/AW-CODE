package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.FORBIDDEN, key = StatusKeyConstant.PASSWORD_INVALIDED)
public class InvalidedPasswordException extends RuntimeException {
  public InvalidedPasswordException(String message) {
    super(message);
  }
}
