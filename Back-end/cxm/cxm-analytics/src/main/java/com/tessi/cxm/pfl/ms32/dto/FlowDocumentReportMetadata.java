package com.tessi.cxm.pfl.ms32.dto;

import java.io.Serializable;
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
public class FlowDocumentReportMetadata implements Serializable {
  private String col; // column name.
  private String label; // key translate.
  private String type; // dataType for frontend: (text, number, percent).
}
