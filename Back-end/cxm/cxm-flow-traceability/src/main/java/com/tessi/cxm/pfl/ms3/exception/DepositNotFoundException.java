package com.tessi.cxm.pfl.ms3.exception;

public class DepositNotFoundException extends RuntimeException {
  public DepositNotFoundException(String message) {
    super(message);
  }

  public DepositNotFoundException() {
    super("Deposit mode not found.");
  }
}
