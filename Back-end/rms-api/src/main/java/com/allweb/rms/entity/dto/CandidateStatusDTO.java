package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CandidateStatusDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;

  @NotEmpty
  @Schema(maximum = "50")
  private String title;

  private boolean isDeleted;
  @NotEmpty private String description;
  private boolean active;
  private boolean isDeletable;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;
}
