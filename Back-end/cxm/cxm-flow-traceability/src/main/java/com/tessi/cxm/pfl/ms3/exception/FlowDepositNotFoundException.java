package com.tessi.cxm.pfl.ms3.exception;

public class FlowDepositNotFoundException extends RuntimeException {

  public FlowDepositNotFoundException(Long id) {
    super(String.format("FlowDeposit id %s not found", id));
  }

  public FlowDepositNotFoundException(String fileId) {
    super(String.format("FlowDeposit id %s not found", fileId));
  }
}
