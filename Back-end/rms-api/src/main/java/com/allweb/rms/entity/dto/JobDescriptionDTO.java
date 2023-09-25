package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class JobDescriptionDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  /*
   *
   * Data transfer object Class
   *
   * */
  @Schema(type = "boolean", name = "isActive")
  @NotNull
  boolean isActive;

  @Schema(name = "id", type = "int", description = "Not required when create new")
  private int id;

  @Schema(name = "title", type = "string", required = true)
  @NotEmpty
  @NotNull
  private String title;

  @Schema(name = "description", type = "text")
  private String description;

  @Schema(name = "filename", type = "string")
  private String filename;

  private Date createdAt;
  private Date updatedAt;
}
