package com.tessi.cxm.pfl.ms11.exception;

public class PortalPdfBadRequestException extends RuntimeException {

  public PortalPdfBadRequestException(String customer) {
    super("Deposit mode \"Portal\" is not active for customer name: " + customer);
  }
}
