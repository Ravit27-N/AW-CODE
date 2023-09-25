package com.tessi.cxm.pfl.ms5.exception;

public class UserPasswordNotMatchException extends RuntimeException{
  public UserPasswordNotMatchException(String message){
    super(message);
  }
}
