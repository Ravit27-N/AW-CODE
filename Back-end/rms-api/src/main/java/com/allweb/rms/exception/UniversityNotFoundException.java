package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UniversityNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UniversityNotFoundException(int id) {
    super("Cannot find University " + id);
  }
}
