package com.tessi.cxm.pfl.ms8.exception;

public class FileNotFoundException extends RuntimeException{
  public FileNotFoundException(String message, Throwable throwable){
    super(message, throwable);
  }
}
