package com.tessi.cxm.pfl.ms32.dto;

import java.io.Serializable;
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
public class FlowDocumentReportDto implements Serializable {

  private List<FlowDocumentReportMetadata> metaData = new ArrayList<>();
  private List<FlowDocumentReportData> result = new ArrayList<>();
}
