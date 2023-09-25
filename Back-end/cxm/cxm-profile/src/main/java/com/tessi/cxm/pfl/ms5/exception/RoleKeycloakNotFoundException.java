package com.tessi.cxm.pfl.ms5.exception;

public class RoleKeycloakNotFoundException extends RuntimeException{
  public RoleKeycloakNotFoundException() {
    super("Profile not found.");
  }

  public RoleKeycloakNotFoundException(String message) {
    super(message);
  }
}
