package com.innovationandtrust.utils.encryption.exception;

public class EncryptionException extends RuntimeException {
    public EncryptionException(Throwable cause) {
        super("Failed to encrypt/decrypt a value.", cause);
    }

    public EncryptionException(String message) {
        super(message);
    }
}
