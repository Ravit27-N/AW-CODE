package com.tessi.cxm.pfl.ms32.exception;

public class FlowDocumentReportNotFoundException extends RuntimeException {

  public FlowDocumentReportNotFoundException(Long id) {
    super("Flow document report not found :" + id + ".");
  }
}
