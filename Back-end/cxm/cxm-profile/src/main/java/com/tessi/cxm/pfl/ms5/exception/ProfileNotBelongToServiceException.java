package com.tessi.cxm.pfl.ms5.exception;

public class ProfileNotBelongToServiceException extends RuntimeException{
  public ProfileNotBelongToServiceException(String username){
    super("The user:" + username + " is not belong to any services. Please assign user to a service before creating a profile.");
  }
}
