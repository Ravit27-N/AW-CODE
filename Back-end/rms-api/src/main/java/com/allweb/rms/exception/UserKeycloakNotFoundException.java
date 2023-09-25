package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserKeycloakNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 61577549232301820L;

  public UserKeycloakNotFoundException(String id) {
    super("Can't find user id " + id);
  }
}
