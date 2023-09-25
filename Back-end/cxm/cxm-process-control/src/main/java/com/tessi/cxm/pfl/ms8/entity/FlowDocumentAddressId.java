package com.tessi.cxm.pfl.ms8.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class FlowDocumentAddressId implements Serializable {
  private String flowId;
  private String docId;
  private Integer addressLineNumber;
}
