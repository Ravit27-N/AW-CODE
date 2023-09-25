package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.service.ProcessControlService;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
@Slf4j
public class UpdateFlowDepositHandler extends AbstractExecutionHandler {

  private ProcessControlService processControlService;

  @Autowired
  @Lazy
  public void setProcessControlService(
      ProcessControlService processControlService) {
    this.processControlService = processControlService;
  }

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var uuid = depositedFlowLaunchRequest.getUuid();
    var step = 2;
    var composedFileId = Optional.<String>empty();
    var validation = false;

    if (FlowTreatmentConstants.IV_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {

      this.processControlService.updateDepositPortalFlowStep(
          uuid, step, composedFileId, validation);
    }
    return ExecutionState.NEXT;
  }
}
