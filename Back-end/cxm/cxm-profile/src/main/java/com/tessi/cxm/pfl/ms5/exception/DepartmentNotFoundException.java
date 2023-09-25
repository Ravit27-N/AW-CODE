package com.tessi.cxm.pfl.ms5.exception;

import java.util.List;

public class DepartmentNotFoundException extends RuntimeException {

  public DepartmentNotFoundException(long id) {
    super("Service not found " + id);
  }

  public DepartmentNotFoundException(List<Long> ids) {
    super("Service not found " + ids);
  }
}
