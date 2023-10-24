package com.innovationandtrust.signature.identityverification.exception;

import com.innovationandtrust.utils.exception.config.CommonExceptionHandler;
import com.innovationandtrust.utils.exception.config.ResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/** ExtendedExceptionHandler. */
@RestControllerAdvice
@Slf4j
public class ExtendedExceptionHandler extends CommonExceptionHandler {

  /**
   * Handle WebClientResponseException.
   *
   * @param ex WebClientResponseException
   * @return ResponseEntity
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleUnExpectedException(Exception ex) {
    log.error("Exception: ", ex);
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  /**
   * handleWebClientResponseException.
   *
   * @param ex WebClientResponseException.
   * @return ResponseEntity.
   */
  @ExceptionHandler(WebClientResponseException.class)
  public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
    log.error("error while calling external api: ", ex);
    var errorHandler = new ResponseErrorHandler(ex.getStatusCode());
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }
}
