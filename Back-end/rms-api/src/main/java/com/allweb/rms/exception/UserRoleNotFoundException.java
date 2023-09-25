package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRoleNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 61577549232301820L;

  public UserRoleNotFoundException(String id) {
    super("Can't find user role " + id);
  }
}
