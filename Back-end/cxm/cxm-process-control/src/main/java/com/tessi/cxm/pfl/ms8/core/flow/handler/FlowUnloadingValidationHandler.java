package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.dto.FlowUnloadingPayload;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDTO;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FlowUnloadingValidationHandler extends AbstractExecutionHandler {

  private final KeycloakService keycloakService;
  private final ProfileFeignClient profileFeignClient;
  private final ModelMapper modelMapper;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    // force unloading scheduled
    boolean isAllowToForceUnloading =
        BooleanUtils.toBoolean(
            context.get(ProcessControlConstants.ALLOW_FORCE_UNLOADING, Boolean.class));
    if (isAllowToForceUnloading) {
      return ExecutionState.NEXT;
    }

    FlowUnloadingPayload flowUnloadingPayload = context.get(
        ProcessControlConstants.FLOW_UNLOADING_PAYLOAD, FlowUnloadingPayload.class);
    var currentDateTime = new Date();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDateTime);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    // Get public holidays
    final var adminAuthToken = BearerAuthentication.PREFIX_TOKEN.concat(
        this.keycloakService.getToken());
    List<Object> publicHolidayRes = profileFeignClient.getPublicHolidays(adminAuthToken);
    List<SharedPublicHolidayDTO> publicHolidays = publicHolidayRes.stream()
        .map(publicHoliday -> modelMapper.map(publicHoliday, SharedPublicHolidayDTO.class))
        .collect(
            Collectors.toList());
    // Check in current day is a public holiday
    var isPublicHoliday = publicHolidays.stream().anyMatch(
        sharedPublicHolidayDTO -> sharedPublicHolidayDTO.getMonth() == month
            && sharedPublicHolidayDTO.getDay() == day);
//    Validate if client allow to unload flow on holiday
    boolean isAllowToUnload = true;
    if (isPublicHoliday) {
      log.info("Current execution date is a public holiday.");
      isAllowToUnload = flowUnloadingPayload.getPublicHolidays().stream().anyMatch(
          sharedPublicHolidayDTO -> sharedPublicHolidayDTO.getMonth() == month
              && sharedPublicHolidayDTO.getDay() == day);

      if (isAllowToUnload) {
        log.info("Client is allow unload the Flow in this holiday.");
        return ExecutionState.NEXT;
      } else {
        log.info("Client is not allow to unload the Flow in this holiday.");
        return ExecutionState.END;
      }
    }
    return ExecutionState.NEXT;
  }
}
