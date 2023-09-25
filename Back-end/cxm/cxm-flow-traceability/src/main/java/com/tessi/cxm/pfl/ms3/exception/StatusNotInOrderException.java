package com.tessi.cxm.pfl.ms3.exception;

import java.util.List;
import java.util.stream.Collectors;

public class StatusNotInOrderException extends RuntimeException {

  public StatusNotInOrderException(List<Integer> requiredSteps, int presentedStep) {
    super("Status is not in order. Required steps: [" + requiredSteps.stream().map(String::valueOf)
        .collect(
            Collectors.joining(",")) + "]. Presented step: [" + presentedStep + "]");
  }
}
