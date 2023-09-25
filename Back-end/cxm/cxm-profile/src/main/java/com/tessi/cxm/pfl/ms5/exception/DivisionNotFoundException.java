package com.tessi.cxm.pfl.ms5.exception;

import java.util.List;

public class DivisionNotFoundException extends RuntimeException {

  public DivisionNotFoundException(long id) {
    super("Division not found " + id);
  }

  public DivisionNotFoundException(List<Long> ids) {
    super("Division not found " + ids.toString());
  }

  public DivisionNotFoundException(String message) {
    super(message);
  }
}
