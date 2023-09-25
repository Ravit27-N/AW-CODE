package com.tessi.cxm.pfl.ms32.exception;

public class FlowTraceabilityReportNotFoundException extends RuntimeException {

  public FlowTraceabilityReportNotFoundException(Long id) {
    super("FlowTraceabilityReport is not found: " + id + ".");
  }
}
