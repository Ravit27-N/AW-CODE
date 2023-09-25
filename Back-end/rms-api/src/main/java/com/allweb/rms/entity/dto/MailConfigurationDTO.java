package com.allweb.rms.entity.dto;

import com.allweb.rms.entity.jpa.CandidateStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
import java.util.List;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MailConfigurationDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final String REQUIRED_DESCRIPTION = "Required when create new and update";

  @Schema(name = "id", type = "integer", description = "Not required when create new")
  private int id;

  @Schema(
      name = "title",
      type = "string",
      description = REQUIRED_DESCRIPTION,
      required = true,
      maxLength = 50)
  @NotEmpty
  @NotNull
  private String title;

  @Email
  @Schema(
      name = "from",
      type = "string",
      description = REQUIRED_DESCRIPTION,
      required = true,
      maxLength = 128)
  private String from;

  @NotEmpty
  @NotNull
  @Schema(
      name = "to",
      type = "string[]",
      description = REQUIRED_DESCRIPTION,
      required = true,
      maxLength = 255)
  private List<String> to;

  @Schema(
      name = "cc",
      type = "string[]",
      description = "Not required when create new",
      maxLength = 255)
  private List<String> cc;

  @Schema(
      name = "candidateStatusId",
      type = "integer",
      description = REQUIRED_DESCRIPTION,
      required = true,
      maxLength = 11)
  @NotNull
  private int candidateStatusId;

  @Schema(
      name = "mailTemplateId",
      type = "integer",
      description = REQUIRED_DESCRIPTION,
      required = true,
      maxLength = 11)
  @NotNull
  private int mailTemplateId;

  @Schema(
      name = "active",
      type = "boolean",
      description = "Not required when create new",
      allowableValues = {"false", "true"})
  private boolean active;

  @Schema(hidden = true)
  private boolean deleted;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm a")
  private Date createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm a")
  private Date updatedAt;

  @Schema(hidden = true)
  private MailTemplateDTO mailTemplate;

  @Schema(hidden = true)
  private CandidateStatus candidateStatus;
}
