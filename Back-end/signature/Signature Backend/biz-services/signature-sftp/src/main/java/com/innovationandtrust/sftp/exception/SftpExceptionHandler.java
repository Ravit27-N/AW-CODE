package com.innovationandtrust.sftp.exception;

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
public class SftpExceptionHandler extends CommonExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(InvalidSftpFileRequestException.class)
  protected ResponseEntity<Object> handleInvalidSftpFileRequestException(
      InvalidSftpFileRequestException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error(ex.getMessage(), ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error(ex.getMessage(), ex);
    return buildResponseEntity(errorHandler);
  }

  /**
   * Handles jakarta.validation.ConstraintViolationException. Thrown when @Validated fails.
   *
   * @param ex the ConstraintViolationException
   * @return the ResponseErrorHandler object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleServiceConstraintViolation(
      ConstraintViolationException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }
}
