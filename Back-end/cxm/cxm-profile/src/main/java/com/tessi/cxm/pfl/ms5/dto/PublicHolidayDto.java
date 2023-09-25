package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHolidayDto implements Serializable {

  private Long id;
  private String label;
  private boolean isFixedDate;
  private String eventDate;
  private List<PublicHolidayDetailsDto> publicHolidayDetails;

  @JsonProperty(access = Access.READ_ONLY)
  private int day;

  @JsonProperty(access = Access.READ_ONLY)
  private int month;

  public int getDay() {
    if (StringUtils.hasText(eventDate)) {
      return Integer.parseInt(eventDate.split("/")[1]);
    }
    return day;
  }

  public int getMonth() {
    if (StringUtils.hasText(eventDate)) {
      return Integer.parseInt(eventDate.split("/")[0]);
    }
    return month;
  }

  public static PublicHolidayDto from(LocalDate date, String label, long id) {
    return PublicHolidayDto.builder()
        .id(id)
        .month(date.getMonthValue())
        .day(date.getDayOfMonth())
        .label(label)
        .isFixedDate(true)
        .eventDate(String.format("%02d/%02d", date.getMonthValue(), date.getDayOfMonth()))
        .build();
  }
}
