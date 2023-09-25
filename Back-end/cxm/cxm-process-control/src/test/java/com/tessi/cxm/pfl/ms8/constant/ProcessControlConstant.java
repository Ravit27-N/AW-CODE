package com.tessi.cxm.pfl.ms8.constant;

import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessControlConstant {
  public static final SharedClientUnloadDetailsDTO MOCK_CLIENT_UNLOAD_DETAILS;
  public static final List<SharedClientUnloadDTO> MOCK_CLIENT_UNLOADS;
  public static final List<SharedPublicHolidayDTO> MOCK_PUBLIC_HOLIDAYS;

  static {
    MOCK_CLIENT_UNLOADS =
        new ArrayList<>(
            Arrays.asList(
                SharedClientUnloadDTO.builder().id(1L).dayOfWeek("MON").hour(11).minute(59).build(),
                SharedClientUnloadDTO.builder()
                    .id(2L)
                    .dayOfWeek("SUN")
                    .hour(11)
                    .minute(59)
                    .build()));
    MOCK_PUBLIC_HOLIDAYS =
        new ArrayList<>(
            Arrays.asList(
                SharedPublicHolidayDTO.builder()
                    .id(1L)
                    .day(1)
                    .isFixedDate(true)
                    .month(11)
                    .build()));
    MOCK_CLIENT_UNLOAD_DETAILS =
        SharedClientUnloadDetailsDTO.builder()
            .clientId(1L)
            .clientUnloads(MOCK_CLIENT_UNLOADS)
            .publicHolidays(MOCK_PUBLIC_HOLIDAYS)
            .build();
  }
}
