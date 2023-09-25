package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileNotFoundException extends RuntimeException {

  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public FileNotFoundException(String message) {
    super(message);
  }
}
