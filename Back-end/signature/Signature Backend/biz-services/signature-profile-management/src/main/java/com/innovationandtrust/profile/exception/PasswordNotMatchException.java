package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST, key = StatusKeyConstant.PASSWORD_NOT_MATCH)
public class PasswordNotMatchException extends RuntimeException {
  public PasswordNotMatchException(String message) {
    super(message);
  }
}
