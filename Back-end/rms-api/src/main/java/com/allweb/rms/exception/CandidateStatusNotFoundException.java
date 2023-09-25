package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CandidateStatusNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -81577545342301820L;

  public CandidateStatusNotFoundException(int id) {
    super("Could not find Candidate Status " + id);
  }
}
