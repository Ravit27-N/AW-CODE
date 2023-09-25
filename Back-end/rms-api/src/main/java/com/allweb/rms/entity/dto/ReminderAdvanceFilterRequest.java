package com.allweb.rms.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReminderAdvanceFilterRequest {
  private Date from;
  private Date to;
  private String filter;
  private String[] reminderTypes;
  private boolean deleted;
  private Boolean active;
}
