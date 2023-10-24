package com.innovationandtrust.configuration.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import jakarta.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Getter
@Setter
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.CUSTOM, property = "error", visible = true)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class ResponseErrorHandler {
  private HttpStatus status;
  private int statusCode;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss a")
  private Date timestamp;

  private String message;
  private String debugMessage;
  private List<AbstractSubError> subErrors;

  private ResponseErrorHandler() {
    timestamp = new Date();
  }

  public ResponseErrorHandler(HttpStatus status) {
    this();
    this.status = status;
    this.statusCode = status.value();
  }

  public ResponseErrorHandler(HttpStatusCode statusCode) {
    this();
    this.status = HttpStatus.resolve(statusCode.value());
    this.statusCode = statusCode.value();
  }

  public ResponseErrorHandler(HttpStatus status, String message) {
    this();
    this.status = status;
    this.message = message;
    this.statusCode = status.value();
  }

  public ResponseErrorHandler(HttpStatus status, Throwable ex) {
    this();
    this.status = status;
    this.statusCode = status.value();
    this.message = "Unexpected error";
    this.debugMessage = ex.getLocalizedMessage();
  }

  public ResponseErrorHandler(HttpStatus status, String message, Throwable ex) {
    this();
    this.status = status;
    this.statusCode = status.value();
    this.message = message;
    this.debugMessage = ex.getLocalizedMessage();
  }

  private void addSubError(AbstractSubError subError) {
    if (subErrors == null) {
      subErrors = new ArrayList<>();
    }
    subErrors.add(subError);
  }

  private void addValidationError(
      String object, String field, Object rejectedValue, String message) {
    addSubError(new ValidationError(object, field, rejectedValue, message));
  }

  private void addValidationError(String object, String message) {
    addSubError(new ValidationError(object, message));
  }

  private void addValidationError(FieldError fieldError) {
    this.addValidationError(
        fieldError.getObjectName(),
        fieldError.getField(),
        fieldError.getRejectedValue(),
        fieldError.getDefaultMessage());
  }

  public void addValidationErrors(List<FieldError> fieldErrors) {
    fieldErrors.forEach(this::addValidationError);
  }

  private void addValidationError(ObjectError objectError) {
    this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
  }

  public void addValidationError(List<ObjectError> globalErrors) {
    globalErrors.forEach(this::addValidationError);
  }

  /**
   * Utility method for adding error of ConstraintViolation. Usually when a @Validated validation
   * fails.
   *
   * @param cv the ConstraintViolation
   */
  private <T> void addValidationError(ConstraintViolation<T> cv) {
    this.addValidationError(
        cv.getRootBeanClass().getSimpleName(),
        ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(),
        cv.getInvalidValue(),
        cv.getMessage());
  }

  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
    constraintViolations.forEach(this::addValidationError);
  }
}
