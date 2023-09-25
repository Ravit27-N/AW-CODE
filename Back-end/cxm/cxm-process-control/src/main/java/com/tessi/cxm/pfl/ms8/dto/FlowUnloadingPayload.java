package com.tessi.cxm.pfl.ms8.dto;

import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDTO;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowUnloadingPayload {

  private long clientId;
  SharedClientUnloadDTO clientUnloads;
  private List<SharedPublicHolidayDTO> publicHolidays = new ArrayList<>();
}
