package com.innovationandtrust.corporate.exception;

import com.innovationandtrust.utils.exception.config.CommonExceptionHandler;
import com.innovationandtrust.utils.exception.config.ResponseErrorHandler;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CorporateProfileExceptionHandler extends CommonExceptionHandler {
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }
}
