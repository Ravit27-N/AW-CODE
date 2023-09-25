package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
  @Schema(name = "id", type = "int", description = "Not required when create new", hidden = true)
  private int id;

  @NotNull
  @NotEmpty
  @Schema(type = "string", requiredMode = RequiredMode.REQUIRED, maximum = "255", name = "title")
  private String title;

  @Schema(type = "string", maximum = "255", name = "description")
  private String description;

  @Schema(
      type = "integer",
      requiredMode = RequiredMode.REQUIRED,
      maximum = "11",
      name = "candidateId")
  @NotNull
  private int candidateId;

  @Schema(type = "integer", requiredMode = RequiredMode.REQUIRED, maximum = "11", name = "statusId")
  @NotNull
  private int statusId;
}
