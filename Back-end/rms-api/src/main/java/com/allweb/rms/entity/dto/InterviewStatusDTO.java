package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class InterviewStatusDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Schema(name = "id", type = "int", description = "Not required when create new")
  private int id;

  @Schema(
      name = "name",
      type = "string",
      maximum = "50",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(type = "boolean", name = "isActive")
  private boolean isActive;

  @Schema(type = "string", name = "createdAt", format = "date")
  private Date createdAt;

  @Schema(type = "string", name = "updatedAt ", format = "date")
  private Date updatedAt;
}
