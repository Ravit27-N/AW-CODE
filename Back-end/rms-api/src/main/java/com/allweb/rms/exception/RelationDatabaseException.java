package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RelationDatabaseException extends RuntimeException {

  private static final long serialVersionUID = -61677549232301820L;

  public RelationDatabaseException(String message) {
    super(message);
  }
}
