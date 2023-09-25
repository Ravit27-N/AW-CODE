package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PortalSwitchRequestDto;
import com.tessi.cxm.pfl.ms8.service.restclient.SwitchFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import org.springframework.stereotype.Component;

@Component
public class FlowValidationProcessingHandler extends ValidationProcessingHandler {

  public FlowValidationProcessingHandler(SwitchFeignClient switchFeignClient) {
    super(switchFeignClient);
  }

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *                for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    if (checkFlowNullable(context)) {
      return ExecutionState.END;
    }
    final PortalFlowFileControl portalFlowFileControl = this.getValidationFlow(context);
    PortalSwitchRequestDto portalSwitchRequestDto =
        PortalSwitchRequestDto.builder()
            .flowType(portalFlowFileControl.getFlow().getType())
            .composedFileId(context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class))
            .fileControl(portalFlowFileControl)
            .created(context.get(FlowTreatmentConstants.CREATED_BY, String.class))
            .build();
    context.put(FlowTreatmentConstants.DOCUMENT_VALIDATION, portalSwitchRequestDto);
    return super.execute(context);
  }
}
