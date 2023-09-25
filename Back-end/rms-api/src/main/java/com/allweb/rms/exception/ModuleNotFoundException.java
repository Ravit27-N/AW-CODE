package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ModuleNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ModuleNotFoundException(int id) {
    super("Can't find Module id " + id);
  }
}
