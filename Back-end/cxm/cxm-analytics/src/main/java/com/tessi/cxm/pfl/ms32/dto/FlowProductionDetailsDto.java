package com.tessi.cxm.pfl.ms32.dto;

import java.util.ArrayList;
import java.util.List;
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
public class FlowProductionDetailsDto {

  private List<FlowProductionDetailMetaData> metaData = new ArrayList<>();
  private List<FlowDocumentProductionDetail> result = new ArrayList<>();
}
