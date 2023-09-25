package com.tessi.cxm.pfl.ms11.exception;

public class CustomerNotFoundException extends RuntimeException {

  public CustomerNotFoundException(String customer) {
    super("Customer not found : " + customer);
  }
}
