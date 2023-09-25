package com.tessi.cxm.pfl.ms5.exception;

import java.util.List;

public class FunctionalityKeyNotAllowedException extends RuntimeException {
  public FunctionalityKeyNotAllowedException(List<String> functionalityKeys) {
    super(
        "Functionality keys are not allowed: "
            + functionalityKeys.toString()
            + " for your permission.");
  }
}
