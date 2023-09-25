package com.tessi.cxm.pfl.ms32.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailSummary {

  private long fillerGroupingApplied = 0;
  private List<FlowProductionDetailMetaData> metaData;
  private List<DocumentStatistics> data;
  private DocumentStatistics total;

  public DocumentDetailSummary(long fillerGroupingApplied, List<DocumentStatistics> data) {
    this.fillerGroupingApplied = fillerGroupingApplied;
    this.data = data;
  }
}
