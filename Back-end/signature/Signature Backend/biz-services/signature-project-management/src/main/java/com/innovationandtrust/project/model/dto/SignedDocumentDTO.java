package com.innovationandtrust.project.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignedDocumentDTO implements Serializable {
  private Long id;
  private String fileName;
  private String fullPath;
  private int contentLength;
  private Long signatoryId;
  private Long documentId;
}
