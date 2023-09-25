package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.utils.UnloadingUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NearestClientUnloadingHandler extends AbstractExecutionHandler {

  protected NearestClientUnloadingHandler(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  private final SimpleDateFormat portalDepositDateFormatter =
      new SimpleDateFormat(FlowTreatmentConstants.PORTAL_DEPOSIT_DATE_FORMAT);
  private final ModelMapper modelMapper;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    PortalFlowFileControl fileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);

    SharedClientUnloadDetailsDTO sharedClientUnloadDetailsDTO =
        context.get(
            ProcessControlConstants.CLIENT_UNLOADING_DETAIL, SharedClientUnloadDetailsDTO.class);

    SharedClientUnloadDetailDTO nearestClientUnload;
    try {
      Date depositDate = portalDepositDateFormatter.parse(fileControl.getDepositDate());
      UnloadingUtils.setModelMapper(modelMapper);
      nearestClientUnload =
          UnloadingUtils.findNearestUnloading(sharedClientUnloadDetailsDTO, depositDate);
    } catch (ParseException parseException) {
      throw new ExecutionException("Failed to parse a Portal deposit date.", parseException);
    }

    if (nearestClientUnload == null) {
      throw new ExecutionException("Failed to the nearest unloading date.");
    }

    context.put(ProcessControlConstants.NEAREST_CLIENT_UNLOADING, nearestClientUnload);
    return ExecutionState.NEXT;
  }
}
