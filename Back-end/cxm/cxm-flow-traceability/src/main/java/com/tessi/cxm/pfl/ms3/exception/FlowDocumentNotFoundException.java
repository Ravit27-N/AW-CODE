package com.tessi.cxm.pfl.ms3.exception;

public class FlowDocumentNotFoundException extends RuntimeException {
  public FlowDocumentNotFoundException(long id) {
    super(String.format("Flow document not found: %d.", id));
  }

  public FlowDocumentNotFoundException(int lineNumber) {
    super(String.format("Flow document not found: %d.", lineNumber));
  }

  public FlowDocumentNotFoundException(String message) {
    super(message);
  }
}
