package com.allweb.rms.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.allweb.rms.exception.ActivityNotFoundException;
import com.allweb.rms.exception.AdvancedSearchBadRequestException;
import com.allweb.rms.exception.ApiError;
import com.allweb.rms.exception.CandidateNotFoundException;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.CandidateStatusTitleConflictException;
import com.allweb.rms.exception.EmailConflictException;
import com.allweb.rms.exception.EmailNotFoundException;
import com.allweb.rms.exception.FileNotFoundException;
import com.allweb.rms.exception.GroupNotFoundException;
import com.allweb.rms.exception.InterviewNotFoundException;
import com.allweb.rms.exception.InterviewStatusInactiveException;
import com.allweb.rms.exception.JobDescriptionNotFoundException;
import com.allweb.rms.exception.MailConfigurationNotFoundException;
import com.allweb.rms.exception.MailTemplateNotFoundException;
import com.allweb.rms.exception.ModuleNotFoundException;
import com.allweb.rms.exception.RelationDatabaseException;
import com.allweb.rms.exception.ReminderNotFoundException;
import com.allweb.rms.exception.ReminderTypeNotFoundException;
import com.allweb.rms.exception.ResultNotFoundException;
import com.allweb.rms.exception.SystemMailConfigurationNotFoundException;
import com.allweb.rms.exception.UniversityNameConflictException;
import com.allweb.rms.exception.UniversityNotFoundException;
import com.allweb.rms.exception.UserKeycloakNotFoundException;
import com.allweb.rms.exception.UserRoleConflictException;
import com.allweb.rms.exception.UserRoleNotFoundException;
import javax.mail.internet.AddressException;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.ElasticsearchException;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.MappingException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Log4j2
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter
   * is missing.
   *
   * @param ex MissingServletRequestParameterException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String error = ex.getParameterName() + " parameter is missing";
    return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
  }

  /**
   * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
   *
   * @param ex HttpMediaTypeNotSupportedException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    return buildResponseEntity(
        new ApiError(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
  }

  /**
   * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
   *
   * @param ex the MethodArgumentNotValidException that is thrown when @Valid validation fails
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
   *
   * @param ex the ConstraintViolationException
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
      jakarta.validation.ConstraintViolationException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getConstraintViolations());
    return buildResponseEntity(apiError);
  }

  /**
   * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
   *
   * @param ex HttpMessageNotReadableException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;
    log.info(
        "{} to {}",
        servletWebRequest.getHttpMethod(),
        servletWebRequest.getRequest().getServletPath());
    String error = "Malformed JSON request";
    return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
  }

  /**
   * Handle HttpMessageNotWritableException.
   *
   * @param ex HttpMessageNotWritableException
   * @param headers HttpHeaders
   * @param status HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String error = "Error writing JSON output";
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
  }

  /**
   * Handle NoHandlerFoundException.
   *
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(
        String.format(
            "Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /** Handle javax.persistence.EntityNotFoundException */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
  protected ResponseEntity<Object> handleEntityNotFound(
      jakarta.persistence.EntityNotFoundException ex) {
    return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex));
  }

  /**
   * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
   *
   * @param ex the DataIntegrityViolationException
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<Object> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, WebRequest request) {
    if (ex.getCause() instanceof ConstraintViolationException) {
      return buildResponseEntity(
          new ApiError(
              HttpStatus.FORBIDDEN,
              "Database error",
              ((ConstraintViolationException) ex.getCause()).getSQLException()));
    }
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
  }

  /**
   * Handle Exception, handle generic Exception.class
   *
   * @param ex the Exception
   * @return the ApiError object
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(
        String.format(
            "The parameter '%s' of value '%s' could not be converted to type '%s'",
            ex.getName(), ex.getValue(), ex.getRequiredType()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(OAuth2AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationCredentialsNotFound(
      OAuth2AuthenticationException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(CandidateNotFoundException.class)
  protected ResponseEntity<Object> handleCandidateNotFoundException(
      CandidateNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(CandidateStatusNotFoundException.class)
  protected ResponseEntity<Object> handleStatusCandidateNotFoundException(
      CandidateStatusNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ActivityNotFoundException.class)
  protected ResponseEntity<Object> handleActivityNotFoundException(
      ActivityNotFoundException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(InterviewNotFoundException.class)
  protected ResponseEntity<Object> handleInterviewNotFoundException(
      InterviewNotFoundException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(SystemMailConfigurationNotFoundException.class)
  public ResponseEntity<Object> handleMailPropertiesNotFoundException(
      SystemMailConfigurationNotFoundException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setDebugMessage(e.getMessage());
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(MailConfigurationNotFoundException.class)
  public ResponseEntity<Object> handleMailConfigurationNotFoundException(
      MailConfigurationNotFoundException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(MailException.class)
  public ResponseEntity<Object> handleMailException(MailException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setDebugMessage(e.getLocalizedMessage());
    apiError.setMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ResultNotFoundException.class)
  protected ResponseEntity<Object> handleResultNotFoundException(
      ResultNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  // this exception i use it when delete status that has id in candidate
  // plz help to check this http status, if it wrong help to tell me because i'm not sure
  @ResponseStatus(FORBIDDEN)
  @ExceptionHandler(RelationDatabaseException.class)
  protected ResponseEntity<Object> handleCandidateStatusBadRequestException(
      RelationDatabaseException ex, WebRequest request) {
    ApiError apiError = new ApiError(FORBIDDEN);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UniversityNotFoundException.class)
  protected ResponseEntity<Object> handleUniversityNotFoundException(
      UniversityNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
  protected ResponseEntity<Object> handleInvalidDataAccessResourceUsageException(
      InvalidDataAccessResourceUsageException ex, WebRequest request) {
    log.error("{}", ex);
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage("Internal Server Error");
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(MappingException.class)
  protected ResponseEntity<Object> handleMappingException(MappingException ex, WebRequest request) {
    log.error("{}", ex);
    ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
    apiError.setMessage("Internal Server Error");
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UniversityNameConflictException.class)
  protected ResponseEntity<Object> handleUniversityNameConflictException(
      UniversityNameConflictException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EmailNotFoundException.class)
  protected ResponseEntity<Object> handleEmailNotFoundException(
      EmailNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UsernameNotFoundException.class)
  protected ResponseEntity<Object> handleUsernameNotFoundException(
      UsernameNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ModuleNotFoundException.class)
  protected ResponseEntity<Object> handleModuleNotFoundException(
      ModuleNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserRoleNotFoundException.class)
  protected ResponseEntity<Object> handleUserRoleNotFoundException(
      UserRoleNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(GroupNotFoundException.class)
  protected ResponseEntity<Object> handleGroupNotFoundException(
      GroupNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserKeycloakNotFoundException.class)
  protected ResponseEntity<Object> handleUserKeycloakNotFoundException(
      UserKeycloakNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(AdvancedSearchBadRequestException.class)
  protected ResponseEntity<Object> handleAdvancedSearchBadRequestException(
      AdvancedSearchBadRequestException ex, WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(ex.getMessage());

    return buildResponseEntity(apiError);
  }

  @ResponseStatus(CONFLICT)
  @ExceptionHandler(EmailConflictException.class)
  protected ResponseEntity<Object> handleEmailConflictException(
      EmailConflictException ex, WebRequest request) {
    ApiError apiError = new ApiError(CONFLICT);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(ResponseStatusException.class)
  protected ResponseEntity<Object> responseStatusException(
      ResponseStatusException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(FileNotFoundException.class)
  protected ResponseEntity<Object> handleFileNotFoundException(
      FileNotFoundException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(ex.getMessage());

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(MailTemplateNotFoundException.class)
  public ResponseEntity<Object> handleMailTemplateNotFoundException(
      MailTemplateNotFoundException e, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(JobDescriptionNotFoundException.class)
  protected ResponseEntity<Object> handleJobDescriptionNotFound(JobDescriptionNotFoundException e) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UserRoleConflictException.class)
  protected ResponseEntity<Object> handleUserRoleConflictException(UserRoleConflictException e) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(AddressException.class)
  protected ResponseEntity<Object> handleAddressException(AddressException e) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage("Invalid Email");
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ReminderNotFoundException.class)
  protected ResponseEntity<Object> handleReminderNotFoundException(ReminderNotFoundException e) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ReminderTypeNotFoundException.class)
  protected ResponseEntity<Object> handleReminderTypeNotFoundException(
      ReminderTypeNotFoundException e) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException e) {
    ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(InterviewStatusInactiveException.class)
  protected ResponseEntity<Object> handleInterviewStatusInactiveException(
      InterviewStatusInactiveException e) {
    ApiError apiError = new ApiError(HttpStatus.OK);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ElasticsearchException.class)
  protected ResponseEntity<Object> handleElasticsearchIndexNotFoundException(
      ElasticsearchException e) {
    String error = "Full text search error.";
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, e.getCause()));
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(CandidateStatusTitleConflictException.class)
  protected ResponseEntity<Object> handleCandidateStatusTitleConflictException(
      CandidateStatusTitleConflictException e) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(e.getMessage());
    apiError.setDebugMessage(e.getMessage());
    return buildResponseEntity(apiError);
  }
}
