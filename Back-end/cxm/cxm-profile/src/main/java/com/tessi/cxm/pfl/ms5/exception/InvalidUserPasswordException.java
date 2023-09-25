package com.tessi.cxm.pfl.ms5.exception;

public class InvalidUserPasswordException extends RuntimeException{
  public InvalidUserPasswordException(String message){
    super(message);
  }
}
