package com.tessi.cxm.pfl.ms32.dto;

import java.time.ZoneOffset;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticExportingRequestFilter extends StatisticRequestFilter {

  @NotBlank private String exportingType;
  private String timeZone;

  public String getTimeZone() {
    if (!StringUtils.hasText(timeZone)) {
      return ZoneOffset.UTC.getId();
    }
    return timeZone;
  }
}
