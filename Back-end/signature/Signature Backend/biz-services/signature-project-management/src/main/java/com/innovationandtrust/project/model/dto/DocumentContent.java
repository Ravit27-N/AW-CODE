package com.innovationandtrust.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;

/** Document content is used to response with field resource, fileName... */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContent {
  private Resource resource;
  private String fileName;
  private String contentType;
  private long contentLength;

  public DocumentContent(Resource resource, String contentType) {
    this.contentType = contentType;
    this.resource = resource;
  }
}
