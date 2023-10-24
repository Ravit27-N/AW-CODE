package com.innovationandtrust.sftp.exception;

public class InvalidProjectDocumentException extends RuntimeException {

  public InvalidProjectDocumentException(String message) {
    super("Invalid project document: " + message);
  }
}
