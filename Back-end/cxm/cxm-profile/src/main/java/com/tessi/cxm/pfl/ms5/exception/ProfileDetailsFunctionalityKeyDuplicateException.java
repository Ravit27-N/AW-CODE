package com.tessi.cxm.pfl.ms5.exception;

public class ProfileDetailsFunctionalityKeyDuplicateException extends RuntimeException{
  public ProfileDetailsFunctionalityKeyDuplicateException(){
    super("Function key is already exist!");
  }
}
