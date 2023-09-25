package com.tessi.cxm.pfl.ms3.exception;

public class FlowHistoryNotFoundException extends RuntimeException {
  public FlowHistoryNotFoundException(long id) {
    super(String.format("Flow Traceability history not found: %d.", id));
  }

  public FlowHistoryNotFoundException(String message) {
    super(message);
  }
}
