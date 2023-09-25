package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handling process of preparing required data before launch a campaign process.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 30 May 2022
 */
@Component
@RequiredArgsConstructor
public class FlowCampaignBeforeLaunchHandler extends AbstractExecutionHandler {

  private final FileCtrlMngtFeignClient fileCtrlMngtFeignClient;

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {

    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.IDENTIFICATION);

    var flowId = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
    var portalFlowFileControl =
        this.fileCtrlMngtFeignClient.getPortalJsonFileControl(
            flowId, this.getToken(context, FlowTreatmentConstants.BEARER_TOKEN));
    var depositedFlowLaunchRequest =
        DepositedFlowLaunchRequest.builder()
            .uuid(flowId)
            .flowType(portalFlowFileControl.getFlow().getType())
            .modelName(portalFlowFileControl.getFlow().getModelName())
            .depositType(portalFlowFileControl.getDepositType())
            .idCreator(Long.valueOf(portalFlowFileControl.getUserId()))
            .fileId(flowId)
            .fileName(portalFlowFileControl.getFileName())
            .build();
    context.put(FlowTreatmentConstants.FLOW_TYPE, portalFlowFileControl.getFlow().getType());
    context.put(FlowTreatmentConstants.USERNAME, portalFlowFileControl.getUserName());
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, depositedFlowLaunchRequest);
    context.put(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, portalFlowFileControl);
    return ExecutionState.NEXT;
  }
}
