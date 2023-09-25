package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CandidateNotFoundException extends RuntimeException {

  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public CandidateNotFoundException(int id) {
    super("Could not find Candidate " + id);
  }
}
