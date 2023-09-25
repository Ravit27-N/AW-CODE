package com.tessi.cxm.pfl.ms5.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignedProfileDTO implements Serializable {
  @Schema(type = "string", example = "1")
  private long id;

  @Schema(type = "string", example = "Concepteur")
  private String name;
}
