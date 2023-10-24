package com.innovationandtrust.utils.file.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse implements Serializable {
  private String fileName;
  private String originalFileName;
  private String contentType;
  private String fullPath;
  private Long size;
}
