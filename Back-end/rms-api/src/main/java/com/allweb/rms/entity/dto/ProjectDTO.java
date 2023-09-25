package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private int id;

  @NotEmpty
  @Schema(maximum = "50")
  private String name;

  private String description;
  private boolean active;
  private boolean isDeleted;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;
}
