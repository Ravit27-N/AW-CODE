package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class FirebaseTokenRequest implements Serializable {
  /** */
  private static final long serialVersionUID = 1L;

  @NotEmpty
  @Schema(name = "deviceId", type = "string", minimum = "1")
  private String deviceId;

  @NotEmpty
  @Schema(name = "token", type = "string", minimum = "1")
  private String token;
}
