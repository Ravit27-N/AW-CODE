package com.tessi.cxm.pfl.ms3.exception;

public class FlowDocumentStatusNotFoundException extends RuntimeException {
  public FlowDocumentStatusNotFoundException(String message) {
    super(message);
  }

  public FlowDocumentStatusNotFoundException() {
    super("Flow document status not found.");
  }
}
