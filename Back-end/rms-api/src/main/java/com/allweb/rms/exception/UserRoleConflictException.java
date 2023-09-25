package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRoleConflictException extends RuntimeException {

  private static final long serialVersionUID = -65577549232301820L;

  public UserRoleConflictException(String message) {
    super(message);
  }
}
