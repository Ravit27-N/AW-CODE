package com.tessi.cxm.pfl.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListFlowTraceabilityValidationDto extends ListFlowTraceabilityDto {
  private long totalDocument;
  private long totalRemainingValidationDocument;
}
