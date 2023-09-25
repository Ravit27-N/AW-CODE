package com.tessi.cxm.pfl.ms8.exception;

public class WatermarkDuplicatedException extends RuntimeException {
  public WatermarkDuplicatedException(String flowId) {
    super(String.format("Watermark already exists for this flow :%s", flowId));
  }
}
