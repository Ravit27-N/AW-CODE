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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class MailTemplateDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @Schema(name = "id", type = "int", description = "Not required when create new")
  private int id;

  @Schema(
      name = "subject",
      type = "string",
      description = "Required when create new and update",
      required = true,
      nullable = true)
  @NotEmpty
  private String subject;

  @Schema(
      name = "body",
      type = "text",
      description = "Required when create new and update",
      required = true,
      nullable = true,
      maxLength = 50)
  @NotEmpty
  private String body;

  @Schema(
      hidden = true,
      name = "active",
      type = "boolean",
      description = "Not required when create new",
      defaultValue = "true",
      allowableValues = {"false", "true"})
  private Boolean active;

  @Schema(
      hidden = true,
      name = "deleted",
      type = "boolean",
      description = "Not required when create new",
      defaultValue = "false",
      allowableValues = {"false", "true"})
  private boolean deleted;

  @Schema(
      hidden = true,
      name = "isDeletable",
      type = "boolean",
      description = "Not required when create new",
      defaultValue = "true",
      allowableValues = {"false", "true"})
  private boolean isDeletable;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;
}
