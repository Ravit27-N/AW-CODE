package com.tessi.cxm.pfl.ms3.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementAssociationDocumentContext implements Serializable {

  private String elementName;
  private String fileId;
  private long documentId;
  private String extension;
}
