package com.allweb.rms.core.scheduler.batch.exception;

public class TaskExecutionException extends Exception {
  private static final long serialVersionUID = 8016678354045541645L;

  public TaskExecutionException(String message) {
    super(message);
  }

  public TaskExecutionException(Throwable cause) {
    super(cause);
  }

  public TaskExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
