package com.tessi.cxm.pfl.ms8.controller;

import com.tessi.cxm.pfl.ms8.exception.BackgroundFileDuplicatedException;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileJDBCException;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileMissingException;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileNotFoundException;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileSizeNotAcceptableException;
import com.tessi.cxm.pfl.ms8.exception.Base64NotSupporterException;
import com.tessi.cxm.pfl.ms8.exception.ConfigSignatureAttributeNotFoundException;
import com.tessi.cxm.pfl.ms8.exception.FileNotFoundException;
import com.tessi.cxm.pfl.ms8.exception.ScheduleFailedException;
import com.tessi.cxm.pfl.ms8.exception.WatermarkDuplicatedException;
import com.tessi.cxm.pfl.ms8.exception.WatermarkNotFoundException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.exception.AbstractGlobalExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.ApiErrorHandler;
import com.tessi.cxm.pfl.shared.utils.ResourceStatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle {@code Process Control} exception.
 *
 * @see AbstractGlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class ProcessControlGlobalExceptionHandler extends AbstractGlobalExceptionHandler {

  @ExceptionHandler(ExecutionException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleExecutionException(ExecutionException e) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ScheduleFailedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleScheduleFailedException(ScheduleFailedException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(Base64NotSupporterException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleBase64FileNotSupportException(
      Base64NotSupporterException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(BackgroundFileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleBase64FileNotSupportException(
      BackgroundFileNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(BackgroundFileJDBCException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleBackgroundFileJDBCException(
      BackgroundFileJDBCException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(BackgroundFileMissingException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<Object> handleBackgroundFileMissingException(
      BackgroundFileMissingException e) {
    var apiError = new ApiErrorHandler(4005, e.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(BackgroundFileDuplicatedException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleBackgroundFileDuplicatedException(
      BackgroundFileDuplicatedException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.CONFLICT);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(BackgroundFileSizeNotAcceptableException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ResponseEntity<Object> handleBackgroundFileSizeNotAcceptableException(
      BackgroundFileSizeNotAcceptableException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_ACCEPTABLE);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SizeLimitExceededException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<Object> handleUploadAFileToLarge(SizeLimitExceededException e) {
    return new ResponseEntity<>(
        new ApiErrorHandler(
            ResourceStatusCode.FILE_TO_LARGE.value(),
            ResourceStatusCode.FILE_TO_LARGE.getReasonPhrase()),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(ConfigSignatureAttributeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleConfigSignatureAttributeNotFoundException(
      ConfigSignatureAttributeNotFoundException e) {
    var apiError = new ApiErrorHandler(4006, e.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(WatermarkNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleWatermarkNotFoundException(
      WatermarkNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(WatermarkDuplicatedException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleWatermarkDuplicatedException(
      WatermarkDuplicatedException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.CONFLICT);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }
}
