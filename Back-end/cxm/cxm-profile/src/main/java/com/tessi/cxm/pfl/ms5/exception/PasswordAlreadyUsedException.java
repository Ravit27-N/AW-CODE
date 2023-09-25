package com.tessi.cxm.pfl.ms5.exception;

public class PasswordAlreadyUsedException extends RuntimeException{
    public PasswordAlreadyUsedException(String message){
        super(message);
    }
}