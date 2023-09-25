package com.tessi.cxm.pfl.ms8.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotBlank;
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
public class ResourceFileMetaDataRequestDto {

  private Long id;

  @NotBlank(message = "The fileName field is required")
  private String fileId;

  @NotBlank(message = "The flowId field is required")
  private String flowId;

  @NotBlank(message = "The type field is required")
  private String type;

  @NotBlank(message = "The position field is required")
  private String position;

  @NotBlank(message = "The source field is required")
  private String source;

}
