package com.tessi.cxm.pfl.ms5.exception;

public class NotRegisteredServiceUserException extends RuntimeException {
  public NotRegisteredServiceUserException(String userId) {
    super("User is not registered with any service: " + userId + ".");
  }
}
