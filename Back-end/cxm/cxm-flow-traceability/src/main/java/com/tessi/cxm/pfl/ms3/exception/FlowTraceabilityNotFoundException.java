package com.tessi.cxm.pfl.ms3.exception;

import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;

public class FlowTraceabilityNotFoundException extends RuntimeException {
  public FlowTraceabilityNotFoundException(String message) {
    super(message);
  }

  public FlowTraceabilityNotFoundException(long id) {
    super(String.format("%s: %d.", FlowTraceabilityConstant.MESSAGE_NOT_FOUND, id));
  }
}
