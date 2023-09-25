package com.tessi.cxm.pfl.ms8.exception;

public class WatermarkNotFoundException extends RuntimeException {
  public WatermarkNotFoundException(String flowId) {
    super(String.format("Watermark not found :%s", flowId));
  }
}
