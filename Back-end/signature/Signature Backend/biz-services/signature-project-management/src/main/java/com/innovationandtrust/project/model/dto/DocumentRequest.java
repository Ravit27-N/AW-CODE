package com.innovationandtrust.project.model.dto;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DocumentRequest used for requesting to create the document when the client upload files. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest implements Serializable {
  private Long id;

  @NotEmpty(message = "File name cannot be empty.")
  private String fileName;

  @NotEmpty(message = "Original file name cannot be empty.")
  private String originalFileName;

  @NotEmpty(message = "Content type cannot be empty.")
  private String contentType;

  private Long size;
  private String extension;

  @NotEmpty(message = "Project id cannot be empty.")
  private Long projectId;
}
