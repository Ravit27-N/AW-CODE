package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.ms5.constant.DayOfWeek;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A DTO for the {@link com.tessi.cxm.pfl.ms5.entity.ClientUnloading} entity */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientUnloadingDto implements Serializable {
  private Long id;

  @NotEmpty(message = "Field dayOfWeek should be not null and empty.")
  private DayOfWeek dayOfWeek;

  private boolean enabled;

  @NotEmpty(message = "Field time should be not null and empty.")
  private String time;

  @JsonProperty(access = Access.READ_ONLY)
  private int hour;

  @JsonProperty(access = Access.READ_ONLY)
  private int minute;

  @NotEmpty(message = "Field zoneId should be not null and empty.")
  private String zoneId;

  public int getHour() {
    return Integer.parseInt(time.split(":")[0]);
  }

  public int getMinute() {
    return Integer.parseInt(time.split(":")[1]);
  }
}
