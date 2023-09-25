package com.tessi.cxm.pfl.ms8.exception;

public class ConfigSignatureAttributeNotFoundException extends RuntimeException {

  public ConfigSignatureAttributeNotFoundException(String message) {
    super(message);
  }

  public ConfigSignatureAttributeNotFoundException() {
    this("Coordinate and IdBalise of signature in config file is empty");
  }
}
