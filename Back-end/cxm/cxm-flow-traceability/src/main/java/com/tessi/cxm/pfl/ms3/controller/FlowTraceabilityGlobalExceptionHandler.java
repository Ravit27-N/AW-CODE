package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.exception.Base64FileContentNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.ChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.DepositNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.ElementAssociationNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FileMetadataBuilderException;
import com.tessi.cxm.pfl.ms3.exception.FlowDepositNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentDetailsNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentStatusNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowHistoryNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowValidationBadRequestException;
import com.tessi.cxm.pfl.ms3.exception.FlowValidationInternalErrorException;
import com.tessi.cxm.pfl.ms3.exception.HubDigitalFlowNotStartException;
import com.tessi.cxm.pfl.ms3.exception.SendingChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingSubChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.StatusNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SubChannelNotFoundException;
import com.tessi.cxm.pfl.shared.exception.AbstractGlobalExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.ApiErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle FlowTraceability exception.
 *
 * @see AbstractGlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class FlowTraceabilityGlobalExceptionHandler extends AbstractGlobalExceptionHandler {

  @ExceptionHandler(FlowTraceabilityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowTraceabilityNotFound(
      FlowTraceabilityNotFoundException ex) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowHistoryNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowHistoryNotFound(FlowHistoryNotFoundException ex) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowDocumentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowDocumentNotFound(FlowDocumentNotFoundException ex) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ChannelNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleChannelNotFoundException(ChannelNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(StatusNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleStatusNotFoundException(StatusNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SubChannelNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleSubChannelNotFoundException(SubChannelNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DepositNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleDepositNotFoundException(DepositNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowDocumentStatusNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowDocumentStatusNotFoundException(
      FlowDocumentStatusNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SendingSubChannelNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleSendingSubChannelNotFoundException(
      SendingSubChannelNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SendingChannelNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleSendingChannelNotFoundException(
      SendingChannelNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ElementAssociationNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleElementAssociationNotFoundException(
      ElementAssociationNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowDocumentDetailsNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowDocumentDetailsNotFoundException(
      FlowDocumentDetailsNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowValidationInternalErrorException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleFlowDocumentDetailsNotFoundException(
      FlowValidationInternalErrorException e) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowValidationBadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleFlowDocumentDetailsNotFoundException(
      FlowValidationBadRequestException e) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FlowDepositNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowDepositNotFoundException(FlowDepositNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(HubDigitalFlowNotStartException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleFlowDepositNotFoundException(HubDigitalFlowNotStartException e) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(Base64FileContentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFileNotFoundException(Base64FileContentNotFoundException e) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FileMetadataBuilderException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleFileUploadFailureException(FileMetadataBuilderException e) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }
}
