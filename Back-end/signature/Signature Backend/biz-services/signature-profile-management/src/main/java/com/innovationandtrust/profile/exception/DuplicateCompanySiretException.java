package com.innovationandtrust.profile.exception;

import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.constant.StatusKeyConstant;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.BAD_REQUEST, key = StatusKeyConstant.DUPLICATE_COMPANY_SIRET)
public class DuplicateCompanySiretException extends RuntimeException {
  public DuplicateCompanySiretException(String message) {
    super(message);
  }
}
