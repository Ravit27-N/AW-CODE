package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHolidayDetailsDto implements Serializable {

  private Long id;
  private Date date;

  @JsonProperty(access = Access.READ_ONLY)
  private int day;

  @JsonProperty(access = Access.READ_ONLY)
  private int month;

  @JsonProperty(access = Access.READ_ONLY)
  private int year;

  public int getDay() {
    if (date != null) {
      return this.getCalendar().get(Calendar.DAY_OF_MONTH);
    }
    return day;
  }

  public int getMonth() {
    if (date != null) {
      return this.getCalendar().get(Calendar.MONTH) + 1;
    }
    return month;
  }

  public int getYear() {
    if (date != null) {
      return this.getCalendar().get(Calendar.YEAR);
    }
    return year;
  }

  private Calendar getCalendar() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }
}
