package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProjectNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -81577545342301820L;

  public ProjectNotFoundException(int id) {
    super("Could not find Project " + id);
  }
}
