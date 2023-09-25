package com.allweb.rms.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
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
public class InterviewDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;
  @NotNull private int candidateId;

  @NotNull
  @Column(length = 11)
  private int statusId;

  @NotNull
  @NotEmpty
  @Column(length = 100)
  private String title;

  @NotEmpty
  @NotNull
  @Column(columnDefinition = "text")
  private String description;

  private boolean sendInvite;
  private boolean setReminder;
  private int reminderTime;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm a")
  private Date dateTime;
}
