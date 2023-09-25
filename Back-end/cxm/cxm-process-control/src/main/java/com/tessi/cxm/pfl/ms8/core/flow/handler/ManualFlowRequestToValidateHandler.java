package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ManualFlowRequestToValidateHandler extends AbstractRequestValidationHandler {

  protected ManualFlowRequestToValidateHandler(StreamBridge streamBridge) {
    super(streamBridge);
  }

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
    PortalFlowFileControl fileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);

    var flowStatusModel =
        new UpdateFlowStatusModel(
            fileControl,
            FlowTraceabilityStatus.TO_VALIDATE,
            context.get(FlowTreatmentConstants.CREATED_BY, String.class));
    flowStatusModel.setValidateDocument(
        context.get(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, Boolean.class));
    this.updateFlowValidation(flowStatusModel);
    return ExecutionState.NEXT;
  }
}
