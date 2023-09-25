package com.tessi.cxm.pfl.ms11.exception;

public class ResourceLibraryNotFoundException extends RuntimeException {
  public ResourceLibraryNotFoundException(String message) {
    super(message);
  }

  public ResourceLibraryNotFoundException(Long id) {
    this("Resource library is not found: " + id);
  }
}
