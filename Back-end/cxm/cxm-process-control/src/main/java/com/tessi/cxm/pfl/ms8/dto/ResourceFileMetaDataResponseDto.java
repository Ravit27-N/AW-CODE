package com.tessi.cxm.pfl.ms8.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceFileMetaDataResponseDto {

  Long id;
  private String fileId;
  private String originalName;
  private long fileSize;
  private String extension;
  private int numberOfPages;
  private String base64;
  private String flowId;
  private String type;
  private String position;
  private boolean isMissing;
  private long ownerId;
  private String source;
}
