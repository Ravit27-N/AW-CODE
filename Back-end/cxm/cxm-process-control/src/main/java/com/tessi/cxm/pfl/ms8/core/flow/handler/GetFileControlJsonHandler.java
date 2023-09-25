package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetFileControlJsonHandler extends AbstractExecutionHandler {

  private final FileCtrlMngtFeignClient fileCtrlMngtFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var bearerToken = ProcessControlExecutionContextUtils.getBearerToken(context);

    if (FlowTreatmentConstants.PORTAL_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {

      var portalJsonFileControl =
          this.fileCtrlMngtFeignClient.getPortalJsonFileControl(
              depositedFlowLaunchRequest.getUuid(), bearerToken);

      if (portalJsonFileControl != null) {
        context.put(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, portalJsonFileControl);

        return ExecutionState.NEXT;
      }
    } else if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      var batchJsonFileControl =
          this.fileCtrlMngtFeignClient.getJsonFileControl(depositedFlowLaunchRequest.getUuid(),
              bearerToken);
      if (batchJsonFileControl != null) {
        context.put(FlowTreatmentConstants.BATCH_JSON_FILE_CONTROL, batchJsonFileControl);
        return ExecutionState.NEXT;
      }
    }

    throw new ExecutionException(
        "Failed to get Json File control by UUID: {" + depositedFlowLaunchRequest.getUuid() + "}.");
  }
}
