package com.innovationandtrust.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DocumentDTO used for response back to client when they request for it. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO implements Serializable {
  private Long id;
  private String fileName;
  private String signedDocUrl;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String editedFileName;

  private String originalFileName;
  private String contentType;
  private String fullPath;
  private Long size;
  private String extension;
  private Long projectId;
  private int totalPages;
  private List<DocumentDetailDTO> documentDetails;
}
