package com.tessi.cxm.pfl.ms15.exception;

public class FailToAnalyseDocumentExceptionHandler extends RuntimeException {
  public FailToAnalyseDocumentExceptionHandler() {
    super("Fail to analyse document from go2pdf server.");
  }
}
