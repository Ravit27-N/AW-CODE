package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class FlowRequestValidationHandler extends AbstractRequestValidationHandler {

  protected FlowRequestValidationHandler(StreamBridge streamBridge) {
    super(streamBridge);
  }

  /**
   * @param context Current execution context which hold all the state from previous execution and
   *                for storing all the current state changed.
   * @return
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {

    var isToValidate = context.get(FlowTreatmentConstants.IS_TO_VALIDATED, Boolean.class);
    if (Boolean.TRUE.equals(isToValidate)) {
      var fileControl = context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL,
          PortalFlowFileControl.class);
      var flowStatusModel = new UpdateFlowStatusModel(fileControl, FlowTraceabilityStatus.VALIDATED,
          context.get(FlowTreatmentConstants.CREATED_BY, String.class));
      flowStatusModel.setValidateDocument(false);
      this.updateFlowValidation(flowStatusModel);
    }
    return ExecutionState.NEXT;
  }
}
