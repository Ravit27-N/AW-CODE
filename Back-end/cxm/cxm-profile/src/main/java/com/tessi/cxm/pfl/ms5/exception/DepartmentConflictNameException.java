package com.tessi.cxm.pfl.ms5.exception;

public class DepartmentConflictNameException extends RuntimeException {
  public DepartmentConflictNameException() {
    super("Service name is already exist!");
  }

  public DepartmentConflictNameException(String name) {
    super("Service name is already exist: " + name + ".");
  }
}
