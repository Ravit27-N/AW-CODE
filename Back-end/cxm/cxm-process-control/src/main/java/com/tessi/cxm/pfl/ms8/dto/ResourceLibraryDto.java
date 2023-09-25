package com.tessi.cxm.pfl.ms8.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceLibraryDto implements Serializable {

  private Long id;
  private String fileName;
  private String fileId;
  private String label;
  private String type;
  private Long fileSize;

  private int pageNumber;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long ownerId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date lastModified;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String lastModifiedBy;
}
