package com.tessi.cxm.pfl.ms5.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignedServiceDTO {
  @Schema(type = "string", example = "1")
  private long id;

  @Schema(type = "string", example = "IT")
  private String name;
  // add new
  @Schema(type = "string", example = "IT")
  private String divisionName;

  @Schema(type = "string", example = "IT")
  private long divisionId;

  @Schema(type = "string", example = "IT")
  private String clientName;

  @Schema(type = "string", example = "IT")
  private Long clientId;
}
