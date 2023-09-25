package com.tessi.cxm.pfl.ms8.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

  @Schema(type = "string", description = "A hash reference to a flow deposit")
  private String hash;

}
