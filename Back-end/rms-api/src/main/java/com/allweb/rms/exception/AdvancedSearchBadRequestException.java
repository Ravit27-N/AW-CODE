package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdvancedSearchBadRequestException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AdvancedSearchBadRequestException(String message) {
    super(message);
  }
}
