package com.tessi.cxm.pfl.ms5.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String userId) {
    super("User is not found: " + userId);
  }
  public UserNotFoundException(long userId) {
   this(String.valueOf(userId));
  }
}
