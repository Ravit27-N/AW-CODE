package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GroupNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 61577549232301820L;

  public GroupNotFoundException(String id) {
    super("Can't find group id " + id);
  }
}
