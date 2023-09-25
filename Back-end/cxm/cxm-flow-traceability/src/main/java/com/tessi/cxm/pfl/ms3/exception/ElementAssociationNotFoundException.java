package com.tessi.cxm.pfl.ms3.exception;

public class ElementAssociationNotFoundException extends RuntimeException {
  public ElementAssociationNotFoundException(String message) {
    super(message);
  }

  public ElementAssociationNotFoundException(long id) {
    super(String.format("Element Association not found: %d.", id));
  }
}
