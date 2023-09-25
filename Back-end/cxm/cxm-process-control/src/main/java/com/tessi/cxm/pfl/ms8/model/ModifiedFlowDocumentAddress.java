package com.tessi.cxm.pfl.ms8.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifiedFlowDocumentAddress {
  private boolean isModified;
  private String docId;
  private String address;
}
