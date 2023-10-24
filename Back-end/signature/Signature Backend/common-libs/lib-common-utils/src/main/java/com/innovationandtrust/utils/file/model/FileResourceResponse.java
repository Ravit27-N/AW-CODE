package com.innovationandtrust.utils.file.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileResourceResponse {
  private String fileName;
  private String fullPath;
}
