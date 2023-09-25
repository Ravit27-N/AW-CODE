package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class ClientUnloadDetails implements Serializable {

  private Long clientId;
  private List<ClientUnloadingDto> clientUnloads = new ArrayList<>();
  private List<PublicHolidayDto> publicHolidays = new ArrayList<>();
}
