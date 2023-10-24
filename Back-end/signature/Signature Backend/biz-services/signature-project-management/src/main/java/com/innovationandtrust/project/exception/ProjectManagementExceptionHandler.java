package com.innovationandtrust.project.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.innovationandtrust.utils.aping.exception.ServiceRequestException;
import com.innovationandtrust.utils.exception.config.CommonExceptionHandler;
import com.innovationandtrust.utils.exception.config.ResponseErrorHandler;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/** Project Exception handler used to handle error. */
@Slf4j
@ControllerAdvice
public class ProjectManagementExceptionHandler extends CommonExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    log.error("Entity not found exception ", ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(FileRequestException.class)
  protected ResponseEntity<Object> handleFileRequestException(FileRequestException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    log.error("File request error exception ", e);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidProjectArgException.class)
  protected ResponseEntity<Object> handleInvalidProjectArgException(InvalidProjectArgException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    errorHandler.setKey(e.getKey());
    log.error("File request error exception ", e);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ServiceRequestException.class)
  protected ResponseEntity<Object> handleServiceRequestException(
      MaxUploadSizeExceededException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error("Max upload size ", ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    log.error("Illegal Argument Exception ", e);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidRequestException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(InvalidRequestException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    return buildResponseEntity(errorHandler);
  }
}
