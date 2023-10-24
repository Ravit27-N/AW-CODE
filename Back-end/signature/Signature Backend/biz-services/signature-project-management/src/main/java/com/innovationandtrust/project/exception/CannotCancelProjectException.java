package com.innovationandtrust.project.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.FORBIDDEN, key = StatusKeyConstant.CANNOT_CANCEL_PROJECT)
public class CannotCancelProjectException extends RuntimeException {
  public CannotCancelProjectException(String message) {
    super(message);
  }
}
