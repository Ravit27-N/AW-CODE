package com.innovationandtrust.utils.keycloak.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KeycloakException extends RuntimeException {

  private final HttpStatus httpStatus;

  public KeycloakException(String message) {
    super(message);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public KeycloakException(String message, HttpStatus status) {
    super(message);
    this.httpStatus = status;
  }
}
