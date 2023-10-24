package com.innovationandtrust.utils.exception.config;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import jakarta.persistence.EntityNotFoundException;
import java.net.ConnectException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Handling any runtime exceptions occurred. */
@Slf4j
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String DEFAULT_MESSAGE = "Error occur";

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(MismatchedInputException.class)
  protected ResponseEntity<Object> handleMismatchedInputException(
      MethodArgumentTypeMismatchException e, HttpStatusCode status) {
    log.error("Unable to map object: ", e);

    var httpStatus = HttpStatus.resolve(status.value());
    if (Objects.isNull(httpStatus)) {
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    return buildResponseEntity(
        new ResponseErrorHandler(httpStatus, "Unable to map object " + e.getMessage(), e));
  }

  /**
   * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter
   * is missing.
   *
   * @param ex MissingServletRequestParameterException
   * @return the ResponseErrorHandler object
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error(
        "Missing servlet request parameter with http header : {} and request path : {}",
        headers.getAllow(),
        request.getContextPath(),
        ex);
    var httpStatus = HttpStatus.resolve(status.value());
    if (Objects.isNull(httpStatus)) {
      httpStatus = HttpStatus.BAD_REQUEST;
    }
    String error = ex.getParameterName() + " parameter is missing";
    return buildResponseEntity(new ResponseErrorHandler(httpStatus, error, ex));
  }

  /**
   * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
   *
   * @param ex HttpMediaTypeNotSupportedException
   * @return the ResponseErrorHandler object
   */
  @Override
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error(
        "Unsupported http media type with http headers : {} and request path: {}",
        headers.getAllow(),
        request.getContextPath(),
        ex);
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    var httpStatus = HttpStatus.resolve(status.value());
    if (Objects.isNull(httpStatus)) {
      httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }
    return buildResponseEntity(
        new ResponseErrorHandler(httpStatus, builder.substring(0, builder.length() - 2), ex));
  }

  /**
   * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
   *
   * @param ex the MethodArgumentNotValidException that is thrown when @Valid validation fails
   * @return the ResponseErrorHandler object
   */
  @Override
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error(
        "Invalid method argument with http header: {} and request {}",
        headers.getAllow(),
        request.getContextPath(),
        ex);
    var errorHandler = new ResponseErrorHandler(status);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.addValidationErrors(ex.getBindingResult().getFieldErrors());
    errorHandler.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(errorHandler);
  }

  /**
   * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
   *
   * @param ex the ConstraintViolationException
   * @return the ResponseErrorHandler object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage("Validation error");
    return buildResponseEntity(errorHandler);
  }

  /**
   * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
   *
   * @param ex HttpMessageNotReadableException
   * @param request WebRequest
   * @return the ResponseErrorHandler object
   */
  @Override
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    log.error("http header: {} and request:  {}", headers.getAllow(), request.getContextPath(), ex);
    var httpStatus = HttpStatus.resolve(statusCode.value());
    if (Objects.isNull(httpStatus)) {
      httpStatus = HttpStatus.BAD_REQUEST;
    }
    return buildResponseEntity(new ResponseErrorHandler(httpStatus, ex.getMessage()));
  }

  /**
   * Handle HttpMessageNotWritableException.
   *
   * @param ex HttpMessageNotWritableException
   * @return the ResponseErrorHandler object
   */
  @Override
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String error = "Error writing JSON output http-header: {} and web request: {}";
    log.error(error, headers.getAllow(), request.getContextPath(), ex);
    var httpStatus = HttpStatus.resolve(status.value());
    if (Objects.isNull(httpStatus)) {
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return buildResponseEntity(new ResponseErrorHandler(httpStatus, ex.getMessage()));
  }

  /**
   * Handle NoHandlerFoundException.
   *
   * @param ex object of NoHandlerFoundException
   */
  @Override
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var errorHandler =
        new ResponseErrorHandler(Objects.requireNonNull(HttpStatus.resolve(status.value())));
    errorHandler.setMessage(
        String.format(
            "Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
    log.error(
        "No handler found header: {} and request: {}",
        headers.getAllow(),
        request.getContextPath());
    errorHandler.setDebugMessage(ex.getMessage());
    return buildResponseEntity(errorHandler);
  }

  @ExceptionHandler(FeignClientRequestException.class)
  protected ResponseEntity<Object> handleFeignClientRequestException(
      FeignClientRequestException ex) {
    log.error("Feign client error", ex);
    return buildResponseEntity(new ResponseErrorHandler(ex.getStatus(), ex.getMessage()));
  }

  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ExceptionHandler(ConnectException.class)
  protected ResponseEntity<Object> handleConnectException(ConnectException ex) {
    log.error(DEFAULT_MESSAGE, ex);
    var errorHandler = new ResponseErrorHandler(HttpStatus.SERVICE_UNAVAILABLE, ex);
    return buildResponseEntity(errorHandler);
  }

  /** Handle javax.persistence.EntityNotFoundException */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(new ResponseErrorHandler(HttpStatus.NOT_FOUND, ex));
  }

  /**
   * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
   *
   * @param ex the DataIntegrityViolationException
   * @return the ResponseErrorHandler object
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<Object> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    log.error(DEFAULT_MESSAGE, ex);
    if (ex.getCause() instanceof ConstraintViolationException constraint) {
      return buildResponseEntity(
          new ResponseErrorHandler(
              HttpStatus.FORBIDDEN, "Database error", constraint.getSQLException()));
    }
    return buildResponseEntity(new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex));
  }

  /**
   * Handle Exception, handle generic Exception.class
   *
   * @param ex the Exception
   * @return the ResponseErrorHandler object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.BAD_REQUEST);
    errorHandler.setMessage(
        String.format(
            "The parameter '%s' of value '%s' could not be converted to type '%s'",
            ex.getName(), ex.getValue(), ex.getRequiredType()));
    errorHandler.setDebugMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(OAuth2AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationCredentialsNotFound(
      OAuth2AuthenticationException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  protected ResponseEntity<Object> handleAuthenticationCredentialsNotFoundException(
      AuthenticationCredentialsNotFoundException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(NullPointerException.class)
  protected ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR);
    errorHandler.setMessage(ex.getMessage());
    errorHandler.setDebugMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(SizeLimitExceededException.class)
  protected ResponseEntity<Object> handleSizeLimitExceededException(SizeLimitExceededException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.UNAUTHORIZED, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(com.innovationandtrust.utils.exception.exceptions.AccessDeniedException.class)
  protected ResponseEntity<Object> handleExternalAccessDeniedException(com.innovationandtrust.utils.exception.exceptions.AccessDeniedException ex) {
    var errorHandler = new ResponseErrorHandler(HttpStatus.FORBIDDEN, ex);
    errorHandler.setMessage(ex.getMessage());
    log.error(DEFAULT_MESSAGE, ex);
    return buildResponseEntity(errorHandler);
  }

  @ExceptionHandler(RuntimeException.class)
  protected ResponseEntity<Object> handleExceptions(RuntimeException ex) {
    HandledException handledException = ex.getClass().getAnnotation(HandledException.class);

    ResponseErrorHandler errorHandler;
    if (handledException != null) {
      errorHandler = new ResponseErrorHandler(handledException.status());
      if (handledException.statusCode() != 0) {
        errorHandler.setStatusCode(handledException.statusCode());
      }
      errorHandler.setKey(handledException.key());
      errorHandler.setMessage(ex.getMessage());
      errorHandler.setDebugMessage(ex.getMessage());
      log.error(handledException.message(), ex);
    } else {
      errorHandler = new ResponseErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex);
      errorHandler.setMessage(ex.getMessage());
      log.error(DEFAULT_MESSAGE, ex);
    }

    return buildResponseEntity(errorHandler);
  }

  protected ResponseEntity<Object> buildResponseEntity(ResponseErrorHandler errorHandler) {
    return new ResponseEntity<>(errorHandler, errorHandler.getStatus());
  }
}
