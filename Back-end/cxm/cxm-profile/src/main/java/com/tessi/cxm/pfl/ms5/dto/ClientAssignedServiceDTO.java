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
public class ClientAssignedServiceDTO {

  @Schema(type = "string", example = "1")
  private long id;

  @Schema(type = "string", example = "IT")
  private String name;
}
