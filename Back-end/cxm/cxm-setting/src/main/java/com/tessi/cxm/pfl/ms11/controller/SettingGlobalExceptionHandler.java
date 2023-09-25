package com.tessi.cxm.pfl.ms11.controller;


import com.tessi.cxm.pfl.ms11.exception.ChannelMetaDataNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.ChannelMetaDataNotOrderException;
import com.tessi.cxm.pfl.ms11.exception.ChannelMetaDataNotUniqueException;
import com.tessi.cxm.pfl.ms11.exception.ChnnelMetaDataIdNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.DefaultINIConfigNotExistException;
import com.tessi.cxm.pfl.ms11.exception.DefaultSectionIgnoreModelException;
import com.tessi.cxm.pfl.ms11.exception.DepositModeNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.FileNotExistException;
import com.tessi.cxm.pfl.ms11.exception.FileNotSupportException;
import com.tessi.cxm.pfl.ms11.exception.FunctionalityNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.HubDomainNameFailureException;
import com.tessi.cxm.pfl.ms11.exception.ModelMetaDataException;
import com.tessi.cxm.pfl.ms11.exception.PhoneNumberInvalidException;
import com.tessi.cxm.pfl.ms11.exception.PortalPdfBadRequestException;
import com.tessi.cxm.pfl.ms11.exception.ResourceLibraryNotFoundException;
import com.tessi.cxm.pfl.shared.exception.ResourceTypeNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.SectionNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.SenderLabelSizeException;
import com.tessi.cxm.pfl.ms11.exception.SenderMailInvalidException;
import com.tessi.cxm.pfl.ms11.exception.UnsubscribeLinkInvalidException;
import com.tessi.cxm.pfl.shared.exception.AbstractGlobalExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.ApiErrorHandler;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.utils.ResourceStatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle {@code Cxm Setting} exception.
 *
 * @see AbstractGlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class SettingGlobalExceptionHandler extends AbstractGlobalExceptionHandler {

  @ExceptionHandler(ResourceLibraryNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleResourceLibraryNotFoundException(
      ResourceLibraryNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleResourceLibraryNotFoundException(
      FileNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SizeLimitExceededException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<Object> handleUploadAFileToLarge(SizeLimitExceededException e) {
    generateErrorMessage(e);
    return new ResponseEntity<>(
        new ApiErrorHandler(
            ResourceStatusCode.FILE_TO_LARGE.value(),
            ResourceStatusCode.FILE_TO_LARGE.getReasonPhrase()),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(FileNotSupportException.class)
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public ResponseEntity<Object> handleFileNotSupportException(FileNotSupportException e) {
    generateErrorMessage(e);
    return new ResponseEntity<>(
        new ApiErrorHandler(ResourceStatusCode.FILE_NOT_SUPPORT.value(), e.getMessage()),
        HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  private void generateErrorMessage(Throwable e) {
    log.error(String.format("In case an error occurs, %s", e.getMessage()), e);
  }


  @ExceptionHandler(CustomerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleCustomerNotFoundException(
      CustomerNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(DepositModeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleDepositModeNotFoundException(
      DepositModeNotFoundException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(FunctionalityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFunctionalityNotFoundException(
      FunctionalityNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(PortalPdfBadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handlePortalPdfBadRequestException(
      PortalPdfBadRequestException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FileNotExistException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleFileNotExistException(
      FileNotExistException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ChannelMetaDataNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleChannelMetaDataException(
      ChannelMetaDataNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ChannelMetaDataNotOrderException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleChannelNotOrderException(
      ChannelMetaDataNotOrderException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DefaultINIConfigNotExistException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleDefaultINIConfigNotExistException(
      DefaultINIConfigNotExistException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ChnnelMetaDataIdNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleChannelIdNotFoundException(
      ChnnelMetaDataIdNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SenderMailInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleSenderMailInvalidException(
      SenderMailInvalidException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(UnsubscribeLinkInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleUnsubscribeLinkInvalidException(
      UnsubscribeLinkInvalidException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ChannelMetaDataNotUniqueException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleChannelMetaDataNotUniqueException(
      ChannelMetaDataNotUniqueException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(HubDomainNameFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleChannelMetaDataNotUniqueException(
      HubDomainNameFailureException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(PhoneNumberInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleException(
      PhoneNumberInvalidException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SenderLabelSizeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleSenderLabelSizeException(
      SenderLabelSizeException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(SectionNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleSectionNotFoundException(SectionNotFoundException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ModelMetaDataException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> modelMetaDataExceptionHandler(ModelMetaDataException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DefaultSectionIgnoreModelException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> defaultSectionIgnoreModelExceptionHandler(
      DefaultSectionIgnoreModelException exception) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }
}
