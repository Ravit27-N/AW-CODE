package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.util.FlowValidationUtils;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class FlowDocumentRequestValidationHandler extends AbstractRequestValidationHandler {

  protected FlowDocumentRequestValidationHandler(StreamBridge streamBridge) {
    super(streamBridge);
  }

  /**
   * @param context Current execution context which hold all the state from previous execution and
   *                for storing all the current state changed.
   * @return
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    FlowValidationUtils.normalizeValidationDocument(context);
    var fileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    var flowStatusModel =
        new UpdateFlowStatusModel(
            fileControl,
            FlowTraceabilityStatus.VALIDATED,
            context.get(FlowTreatmentConstants.CREATED_BY, String.class));

    flowStatusModel.setValidateDocument(true);
    this.updateFlowValidation(flowStatusModel);
    return ExecutionState.NEXT;
  }
}
