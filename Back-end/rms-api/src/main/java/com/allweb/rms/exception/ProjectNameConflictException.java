package com.allweb.rms.exception;

public class ProjectNameConflictException extends RuntimeException {
  private static final long serialVersionUID = -61577549232301850L;

  public ProjectNameConflictException() {
    super("Project Name is already exists");
  }
}
