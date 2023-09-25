package com.allweb.rms.entity.dto;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.InterviewStatus;
import com.allweb.rms.security.AuthenticatedUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRequest {
  @Schema(name = "id", type = "int", description = "Not required when create new", hidden = true)
  private int id;

  @NotNull
  @Schema(name = "candidateId", type = "int", description = "required when create new")
  private int candidateId;

  @NotNull
  @Column(length = 11)
  @Schema(name = "statusId", type = "int", description = "required when create new")
  private int statusId;

  @NotNull
  @NotEmpty
  @Column(length = 100)
  @Schema(type = "string", required = true, maximum = "255", name = "title")
  private String title;

  @Column(columnDefinition = "text")
  @Schema(type = "string", name = "description")
  private String description;

  private boolean sendInvite;
  private boolean setReminder;
  private int reminderTime;

  @NotNull
  @Schema(
      name = "dateTime",
      type = "Date",
      description = "required when create new",
      pattern = "dd-MM-yyyy HH:mm",
      example = "14-01-2021 02:00",
      required = true)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
  private Date dateTime;

  @Schema(hidden = true)
  private Candidate candidate;

  @Schema(hidden = true)
  private InterviewStatus interviewStatus;

  @Schema(hidden = true)
  private String userId;

  @Schema(hidden = true)
  private AuthenticatedUser authenticatedUser;

  public InterviewRequest(
      @NotNull int candidateId,
      @NotNull @NotEmpty String title,
      String description,
      @NotNull Date dateTime,
      String userId) {
    this.candidateId = candidateId;
    this.title = title;
    this.description = description;
    this.dateTime = dateTime;
    this.userId = userId;
  }
}
