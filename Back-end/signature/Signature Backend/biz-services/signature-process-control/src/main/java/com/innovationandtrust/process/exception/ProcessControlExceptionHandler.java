package com.innovationandtrust.process.exception;

import com.innovationandtrust.utils.aping.exception.ServiceRequestException;
import com.innovationandtrust.utils.encryption.exception.EncryptionException;
import com.innovationandtrust.utils.encryption.exception.InvalidUserTokenException;
import com.innovationandtrust.utils.exception.config.CommonExceptionHandler;
import com.innovationandtrust.utils.exception.config.HandledException;
import com.innovationandtrust.utils.exception.config.ResponseErrorHandler;
import com.innovationandtrust.utils.exception.exceptions.ApiRequestException;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidTTLValueException;
import com.innovationandtrust.utils.file.exception.FileNotFoundException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ProcessControlExceptionHandler extends CommonExceptionHandler {
  private static final String DEFAULT_MESSAGE = "Error occur";

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ServiceRequestException.class)
  protected ResponseEntity<Object> handleServiceRequestException(ServiceRequestException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleServiceIllegalArgumentException(
      IllegalArgumentException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error("IllegalArgumentException", ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(FileNotFoundException.class)
  protected ResponseEntity<Object> handleFileNotFoundExceptionException(FileNotFoundException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_FOUND, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(FileRequestException.class)
  protected ResponseEntity<Object> handleFileRequestExceptionException(FileRequestException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(InvalidOtpException.class)
  protected ResponseEntity<Object> handleServiceInvalidOtpException(InvalidOtpException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ConvertPDFFailException.class)
  protected ResponseEntity<Object> handleConvertPDFFailException(ConvertPDFFailException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidTTLValueException.class)
  protected ResponseEntity<Object> handleTTLValueException(InvalidTTLValueException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST, ex);
    errorHandler.setMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ApiRequestException.class)
  protected ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    log.error(DEFAULT_MESSAGE, e);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(InvalidUserTokenException.class)
  protected ResponseEntity<Object> handleInvalidUserTokenException(InvalidUserTokenException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED, e);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    log.error(DEFAULT_MESSAGE, e);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(EncryptionException.class)
  protected ResponseEntity<Object> handleEncryptionException(EncryptionException e) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED, e);
    errorHandler.setMessage(e.getMessage());
    errorHandler.setDebugMessage(e.getMessage());
    log.error(DEFAULT_MESSAGE, e);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ProjectRefuseExceptionHandler.class)
  protected ResponseEntity<Object> handleProjectRefusedException(RuntimeException ex) {

    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST, ex);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setKey("PROJECT_REFUSED");
    log.error(DEFAULT_MESSAGE, ex.getMessage());
    return buildResponseEntity(errorHandler);
  }
}
