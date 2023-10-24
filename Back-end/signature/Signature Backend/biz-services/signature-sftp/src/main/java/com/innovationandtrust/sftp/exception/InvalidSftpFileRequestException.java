package com.innovationandtrust.sftp.exception;

public class InvalidSftpFileRequestException extends RuntimeException {

  public InvalidSftpFileRequestException() {
    super("Invalid sftp file processing request");
  }
}
