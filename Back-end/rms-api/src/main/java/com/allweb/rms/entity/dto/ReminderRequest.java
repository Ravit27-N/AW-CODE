package com.allweb.rms.entity.dto;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.security.AuthenticatedUser;
import com.allweb.rms.utils.ReminderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReminderRequest implements Serializable {
  private static final long serialVersionUID = -5116222547530893167L;

  @Schema(implementation = ReminderType.class, name = "reminderType", required = true)
  private String reminderType;

  @Schema(name = "candidateId", type = "int")
  private int candidateId;

  @Schema(name = "interviewId", type = "int")
  private int interviewId;

  @Schema(name = "title", type = "string", required = true, maximum = "50")
  private String title;

  @Schema(
      name = "dateReminder",
      type = "string",
      format = "date-time",
      pattern = "dd-MM-yyyy HH:mm",
      example = "14-01-2021 02:00",
      required = true)
  @JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
  private Date dateReminder;

  @Schema(
      name = "remindBefore",
      type = "integer",
      description = "Time in seconds before the actual reminder start.")
  private int remindBefore;

  @Schema(name = "description", type = "string")
  private String description;

  @Schema(name = "active", type = "boolean")
  @Builder.Default
  private boolean active = true;

  @Schema(hidden = true)
  private Interview interview;

  @Schema(hidden = true)
  private transient AuthenticatedUser authenticatedUser;
}
