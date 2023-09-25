package com.tessi.cxm.pfl.ms3.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FlowValidationResponse {
  private long totalError;
  private long totalSuccess;
  private List<String> errors;
  private List<String> successes;
}
