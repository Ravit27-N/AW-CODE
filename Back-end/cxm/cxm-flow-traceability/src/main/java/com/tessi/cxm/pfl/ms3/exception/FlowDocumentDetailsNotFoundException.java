package com.tessi.cxm.pfl.ms3.exception;

public class FlowDocumentDetailsNotFoundException extends RuntimeException {
  public FlowDocumentDetailsNotFoundException(long id) {
    super(String.format("Flow document details not found: %d.", id));
  }

  public FlowDocumentDetailsNotFoundException(String message) {
    super(message);
  }
}
