package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.exception.CSVNotAcceptableException;
import com.tessi.cxm.pfl.ms5.exception.ClientEmailConflictException;
import com.tessi.cxm.pfl.ms5.exception.ClientFillerKeysNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.ClientNameConflictException;
import com.tessi.cxm.pfl.ms5.exception.ClientNameNotModifiableException;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.ClientSettingJDBCException;
import com.tessi.cxm.pfl.ms5.exception.DataReaderHeaderNotAcceptable;
import com.tessi.cxm.pfl.ms5.exception.DataReaderNotSupported;
import com.tessi.cxm.pfl.ms5.exception.DataReaderRowNotAcceptable;
import com.tessi.cxm.pfl.ms5.exception.DepartmentConflictNameException;
import com.tessi.cxm.pfl.ms5.exception.DepartmentNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNameConflictException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.EmailInvalidPatternException;
import com.tessi.cxm.pfl.ms5.exception.FormatTimeNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalitiesNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalitiesNotFound;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityKeyNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityNotModifiableException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityRequiredException;
import com.tessi.cxm.pfl.ms5.exception.INIConfigurationFileNotAcceptable;
import com.tessi.cxm.pfl.ms5.exception.InvalidUserAssignedProfileException;
import com.tessi.cxm.pfl.ms5.exception.InvalidUserException;
import com.tessi.cxm.pfl.ms5.exception.InvalidUserPasswordException;
import com.tessi.cxm.pfl.ms5.exception.KeycloakUserNotFound;
import com.tessi.cxm.pfl.ms5.exception.NotRegisteredServiceUserException;
import com.tessi.cxm.pfl.ms5.exception.PasswordAlreadyUsedException;
import com.tessi.cxm.pfl.ms5.exception.PortalConfigurationFailureException;
import com.tessi.cxm.pfl.ms5.exception.PortalSettingConfigFailureException;
import com.tessi.cxm.pfl.ms5.exception.PortalSettingModelFailureException;
import com.tessi.cxm.pfl.ms5.exception.PrivilegeKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.ProfileDetailsFunctionalityKeyDuplicateException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNameDuplicateException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotBelongToServiceException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.PublicHolidayNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.ReturnAddressNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.RoleKeycloakNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.TokenExpiredDateException;
import com.tessi.cxm.pfl.ms5.exception.UserAPIFailureException;
import com.tessi.cxm.pfl.ms5.exception.UserKeycloakServiceExceptionHandler;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserPasswordNotMatchException;
import com.tessi.cxm.pfl.ms5.exception.UserRepresentationNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserResetPasswordException;
import com.tessi.cxm.pfl.shared.exception.AbstractGlobalExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.ApiErrorHandler;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.exception.JsonProcessingExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.KeycloakServiceException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle Profile exception.
 *
 * @see AbstractGlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class ProfileGlobalExceptionHandler extends AbstractGlobalExceptionHandler {

  @ExceptionHandler(ClientErrorException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  protected ResponseEntity<Object> handleKeycloakConflictException(ClientErrorException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, ex.getMessage(), ex));
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleKeycloakBadRequestException(BadRequestException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleKeycloakNotFoundException(NotFoundException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(RoleKeycloakNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleRoleKeycloakNotFoundException(
      RoleKeycloakNotFoundException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(UserKeycloakServiceExceptionHandler.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> existedGroupForUser(
      UserKeycloakServiceExceptionHandler exceptionHandler) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exceptionHandler.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(ClientNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleClientExceptionHandler(ClientNotFoundException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(JsonProcessingExceptionHandler.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleJsonProcessingExceptionHandler(
      JsonProcessingExceptionHandler ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(DivisionNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleDivisionNotFoundExceptionHandler(
      DivisionNotFoundException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(ProfileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleDepartmentNotFoundExceptionHandler(
      ProfileNotFoundException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(ProfileNameDuplicateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleProfileNameDuplicateHandler(ProfileNameDuplicateException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, e.getMessage(), e));
  }

  @ExceptionHandler(ProfileNotBelongToServiceException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleProfileNotBelongToServiceHandler(
      ProfileNotBelongToServiceException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(ProfileDetailsFunctionalityKeyDuplicateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleProfileDetailsFunctionalityKeyDuplicateHandler(
      ProfileDetailsFunctionalityKeyDuplicateException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, e.getMessage(), e));
  }

  @ExceptionHandler(FunctionalityKeyNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleProfileDetailsFunctionalityKeyNotFoundHandler(
      FunctionalityKeyNotFoundException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(PrivilegeKeyNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handlePrivilegeKeyNotFoundHandler(PrivilegeKeyNotFoundException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(InvalidUserException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handlePrivilegeKeyNotFoundHandler(InvalidUserException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, e.getMessage(), e));
  }

  @ExceptionHandler(InvalidUserAssignedProfileException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handlePrivilegeKeyNotFoundHandler(
      InvalidUserAssignedProfileException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, e.getMessage(), e));
  }

  @ExceptionHandler(KeycloakServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> keycloakServiceExceptionHandler(KeycloakServiceException e) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e));
  }

  @ExceptionHandler(NotRegisteredServiceUserException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> notRegisteredServiceUserExceptionHandler(
      NotRegisteredServiceUserException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, e.getMessage(), e));
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> userNotFoundExceptionHandler(UserNotFoundException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, e.getMessage(), e));
  }

  @ExceptionHandler(DepartmentConflictNameException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleDepartmentConflictNameExceptionHandler(
      DepartmentConflictNameException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, e.getMessage(), e));
  }

  @ExceptionHandler(EmailInvalidPatternException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleEmailInvalidExceptionHandler(EmailInvalidPatternException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, e.getMessage(), e));
  }

  @ExceptionHandler(UserRepresentationNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleUserRepresentationNotFoundExceptionHandler(
      UserRepresentationNotFoundException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(KeycloakUserNotFound.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleKeycloakUserNotFoundExceptionHandler(KeycloakUserNotFound e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, e.getMessage(), e));
  }

  @ExceptionHandler(TokenExpiredDateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleTokenExpiredDateExceptionHandler(
      TokenExpiredDateException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, e.getMessage(), e));
  }

  @ExceptionHandler(UserResetPasswordException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleUserResetPasswordExceptionHandler(
      UserResetPasswordException e) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e));
  }

  @ExceptionHandler(ClientNameConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleClientNameConflictHandler(ClientNameConflictException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, e.getMessage(), e));
  }

  @ExceptionHandler(DivisionNameConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleDivisionNameConflictHandler(DivisionNameConflictException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, e.getMessage(), e));
  }

  @ExceptionHandler(ClientEmailConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Object> handleClientEmailConflictExceptionHandler(
      ClientEmailConflictException e) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.CONFLICT, e.getMessage(), e));
  }

  /**
   * Handle exception to {@link FileErrorException}.
   *
   * @return the apiError object
   */
  @ExceptionHandler(FileErrorException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleFileErrorException(FileErrorException ex) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DepartmentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleDepartmentNotFoundException(
      DepartmentNotFoundException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(ClientNameNotModifiableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleClientNameNotModifiableException(
      ClientNameNotModifiableException ex) {
    var apiError = new ApiErrorHandler(HttpStatus.BAD_REQUEST);
    apiError.setMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FunctionalitiesNotFound.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleFunctionalitiesNotFoundException(
      FunctionalitiesNotFound ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(FunctionalityNotModifiableException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  protected ResponseEntity<Object> handleFunctionalityNotModifiableException(
      FunctionalityNotModifiableException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.FORBIDDEN, ex.getMessage(), ex));
  }

  @ExceptionHandler(FunctionalityRequiredException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleFunctionalityRequiredException(
      FunctionalityRequiredException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(FunctionalitiesNotAllowedException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  protected ResponseEntity<Object> handleFunctionalitiesNotAllowedException(
      FunctionalitiesNotAllowedException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.EXPECTATION_FAILED, ex.getMessage(), ex));
  }

  @ExceptionHandler(PublicHolidayNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handlePublicHolidayNotFoundException(
      PublicHolidayNotFoundException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(FormatTimeNotAllowedException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  protected ResponseEntity<Object> handleFormatTimeNotAllowedException(
      FormatTimeNotAllowedException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.EXPECTATION_FAILED, ex.getMessage(), ex));
  }

  @ExceptionHandler(InvalidUserPasswordException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleInvalidUserPasswordException(
      InvalidUserPasswordException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }

  @ExceptionHandler(UserPasswordNotMatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleUserPasswordNotMatchException(
      UserPasswordNotMatchException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(ClientFillerKeysNotAllowedException.class)
  @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
  protected ResponseEntity<Object> handleClientFillerKeysNotAllowedException(
      ClientFillerKeysNotAllowedException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.EXPECTATION_FAILED, ex.getMessage(), ex));
  }

  @ExceptionHandler(FunctionalityKeyNotAllowedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  protected ResponseEntity<Object> handleFunctionalityKeyNotAllowedException(
      FunctionalityKeyNotAllowedException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.FORBIDDEN, ex.getMessage(), ex));
  }

  @ExceptionHandler(ClientSettingJDBCException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleCreateOrModifiedClientSettingException(
      ClientSettingJDBCException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(PortalSettingConfigFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleModifiedPortalSettingConfigFailureException(
      PortalSettingConfigFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(PortalSettingModelFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleGetPortalSettingModelsFailureException(
      PortalSettingModelFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(PortalConfigurationFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleGetPortalConfigurationFailureException(
      PortalConfigurationFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(INIConfigurationFileNotAcceptable.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  protected ResponseEntity<Object> handleINIConfigFileNotAcceptableException(
      INIConfigurationFileNotAcceptable ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), ex));
  }

  @ExceptionHandler(UserAPIFailureException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ResponseEntity<Object> handleCreateUserAPIFailureException(UserAPIFailureException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex));
  }

  @ExceptionHandler(DataReaderNotSupported.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  protected ResponseEntity<Object> handleDataReaderNotSupportedException(
      DataReaderNotSupported ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), ex));
  }

  @ExceptionHandler(DataReaderHeaderNotAcceptable.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  protected ResponseEntity<Object> handleDataReaderHeaderNotAcceptableException(
      DataReaderHeaderNotAcceptable ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), ex));
  }

  @ExceptionHandler(CSVNotAcceptableException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  protected ResponseEntity<Object> handleCSVNotAcceptableException(CSVNotAcceptableException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), ex));
  }

  @ExceptionHandler(DataReaderRowNotAcceptable.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  protected ResponseEntity<Object> handleDataReaderRowNotAcceptableException(
      DataReaderRowNotAcceptable ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), ex));
  }

  @ExceptionHandler(BatchUserCreationFailureException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleBatchUserCreationFailureException(
      BatchUserCreationFailureException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
  }

  @ExceptionHandler(PasswordAlreadyUsedException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
  protected ResponseEntity<Object> handlePasswordAlreadyUsedException(
      PasswordAlreadyUsedException ex) {
    return buildResponseEntity(
        new ApiErrorHandler(HttpStatus.PRECONDITION_REQUIRED, ex.getMessage(), ex));
  }

  @ExceptionHandler(ReturnAddressNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleReturnAddressNotFoundException(
          ReturnAddressNotFoundException ex) {
    return buildResponseEntity(new ApiErrorHandler(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
  }
}
