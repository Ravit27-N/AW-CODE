package com.tessi.cxm.pfl.ms5.exception;

public class TokenExpiredDateException extends RuntimeException{
  public TokenExpiredDateException(String message){
    super(message);
  }
}
