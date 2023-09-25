package com.tessi.cxm.pfl.ms32.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowProductionDetailMetaData {
  private String col;
  private String label;
  private String type;
}
