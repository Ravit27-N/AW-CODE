package com.tessi.cxm.pfl.ms3.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowDocumentValidationRequest implements Serializable {

  private long flowId;
  private String action;
  private List<String> documentIds;

}
