package com.tessi.cxm.pfl.ms32.exception;

public class StatusNotInOrderException extends RuntimeException {

  public StatusNotInOrderException(long id, String status) {
    super("Status \"" + status + "\"required for document with id " + id);
  }
}
