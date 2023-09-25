package com.tessi.cxm.pfl.ms32.controller;

import com.tessi.cxm.pfl.ms32.exception.CSVFailureException;
import com.tessi.cxm.pfl.ms32.exception.ChannelNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.CriterialDistributionNotActive;
import com.tessi.cxm.pfl.ms32.exception.DateInvalidException;
import com.tessi.cxm.pfl.ms32.exception.DateTypeNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.FillerNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.FilterPreferenceBadRequestException;
import com.tessi.cxm.pfl.ms32.exception.FilterPreferenceJDBCException;
import com.tessi.cxm.pfl.ms32.exception.FilterPreferenceNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.FlowDocumentReportNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.SubChannelNotFoundException;
import com.tessi.cxm.pfl.shared.exception.AbstractGlobalExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.ApiErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnalyticGlobalExceptionController extends AbstractGlobalExceptionHandler {

  protected ResponseEntity<Object> buildResponseEntity(ApiErrorHandler apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(FilterPreferenceJDBCException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleUserFilterPreferenceJDBCException(
      FilterPreferenceJDBCException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(FilterPreferenceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleUserFilterPreferenceNotFoundException(
      FilterPreferenceNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(DateTypeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleDateTypeNotFoundException(
      DateTypeNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(FilterPreferenceBadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleUserFilterPreferenceBadRequestException(
      FilterPreferenceBadRequestException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(FlowDocumentReportNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFlowDocumentReportNotFoundException(
      FlowDocumentReportNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(DateInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleDateNotFormatException(
      DateInvalidException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(ChannelNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleChannelNotFoundException(
      ChannelNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(SubChannelNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleSubChannelNotFoundException(
      SubChannelNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(FillerNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleFillerNotFoundException(
      FillerNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(CriterialDistributionNotActive.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Object> handleFillerNotFoundException(
      CriterialDistributionNotActive ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex));
  }

  @ExceptionHandler(CSVFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleCSVFailureException(
      CSVFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }
}
