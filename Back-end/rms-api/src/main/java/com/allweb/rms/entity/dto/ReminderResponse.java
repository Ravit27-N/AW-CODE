package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class ReminderResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  @Schema(name = "id", type = "int", description = "Not required when create new")
  private int id;

  @Schema(type = "string", required = true, maximum = "50", name = "title")
  private String reminderType;

  @Schema(type = "string", required = true, maximum = "50", name = "title")
  private String title;

  @Schema(
      type = "string",
      name = "dateReminder",
      format = "date",
      pattern = "dd-MM-yyyy HH:mm",
      example = "14-01-2021 02:00",
      required = true)
  private Date dateReminder;

  private Date createdAt;

  private String description;

  private boolean active;

  private transient Map<String, Object> candidate;

  private transient Map<String, Object> interview;
}
