package com.tessi.cxm.pfl.ms5.exception;

public class ProfileNameDuplicateException extends RuntimeException{
  public ProfileNameDuplicateException(String name){
    super("Name field is already exist!");
  }
}
