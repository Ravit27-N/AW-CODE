package com.tessi.cxm.pfl.ms3.exception;

public class FlowDocumentHistoryNotFoundException extends RuntimeException {
  public FlowDocumentHistoryNotFoundException(long id) {
    super(String.format("Flow document history not found: %d.", id));
  }

  public FlowDocumentHistoryNotFoundException(String message) {
    super(message);
  }
}
