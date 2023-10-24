package com.innovationandtrust.utils.encryption.exception;

public class InvalidUserTokenException extends RuntimeException {
    public InvalidUserTokenException() {
        super("You don't have required privileges to perform this action.");
    }
}
