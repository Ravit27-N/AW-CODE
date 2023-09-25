package com.tessi.cxm.pfl.ms32.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tessi.cxm.pfl.ms32.constant.DateType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterPreferenceDto implements Serializable {
  @Schema(
      type = "string",
      allowableValues = {"0", "1", "2", "3", "4", "5", "6", "7"})
  private String selectDateType;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date customStartDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private Date customEndDate;

  public String getSelectDateType() {
    return selectDateType == null ? DateType.LAST_7_DAYS.getKey() : selectDateType;
  }
}
