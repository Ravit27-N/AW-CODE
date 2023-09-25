package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PortalSwitchRequestDto;
import com.tessi.cxm.pfl.ms8.service.restclient.SwitchFeignClient;
import com.tessi.cxm.pfl.ms8.util.FlowValidationUtils;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Handling business logic of flow's documents validation.
 *
 * @author Piseth KHON
 * @author Vichet CHANN
 * @version 1.10.0
 * @since 13 September 2022
 */
@Component
public class FlowDocumentValidationProcessingHandler extends ValidationProcessingHandler {

  public FlowDocumentValidationProcessingHandler(SwitchFeignClient switchFeignClient) {
    super(switchFeignClient);
  }

  @SuppressWarnings("unchecked")
  public static <T extends List<?>> T cast(Object obj) {
    return (T) obj;
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
    if (checkFlowNullable(context)) {
      return ExecutionState.END;
    }
    FlowValidationUtils.normalizeValidationDocument(context);
    final var flowFileControl = this.getValidationFlow(context);
    context.put(
        FlowTreatmentConstants.DOCUMENT_VALIDATION,
        PortalSwitchRequestDto.builder()
            .flowType(flowFileControl.getFlow().getType())
            .composedFileId(context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class))
            .fileControl(flowFileControl)
            .created(context.get(FlowTreatmentConstants.CREATED_BY, String.class))
            .build());
    context.put(PROCESS_ONLY_SCHEDULE_DOCUMENT, true);
    return super.execute(context);
  }
}
