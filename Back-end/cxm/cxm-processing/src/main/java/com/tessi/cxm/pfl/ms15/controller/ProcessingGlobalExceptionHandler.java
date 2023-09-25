package com.tessi.cxm.pfl.ms15.controller;

import com.tessi.cxm.pfl.ms15.exception.AttachmentException;
import com.tessi.cxm.pfl.ms15.exception.SendPjFailureException;
import com.tessi.cxm.pfl.ms15.exception.UserAuthorizationFailureException;
import com.tessi.cxm.pfl.shared.exception.AbstractGlobalExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.ApiErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handling all microservice processing exceptions.
 *
 * @author Vichet CHANN
 */
@RestControllerAdvice
public class ProcessingGlobalExceptionHandler extends AbstractGlobalExceptionHandler {

  @Override
  protected ResponseEntity<Object> buildResponseEntity(ApiErrorHandler apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(AttachmentException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleBase64FailureException(
      AttachmentException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(SendPjFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleSendPjException(
      SendPjFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(UserAuthorizationFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleAuthorizationFailureException(
      UserAuthorizationFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }
}
