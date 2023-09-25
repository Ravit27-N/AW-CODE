package com.tessi.cxm.pfl.ms5.exception;

public class PrivilegeKeyNotFoundException extends RuntimeException {
  public PrivilegeKeyNotFoundException(String privilegeKey) {
    super("Privilege key is not found: " + privilegeKey);
  }
}
