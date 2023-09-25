package com.tessi.cxm.pfl.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowDocumentOwnerProjection {
  private long flowId;
  private long documentId;
  private long ownerId;
}
