package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PortalSwitchRequestDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.tessi.cxm.pfl.ms8.model.SwitchFlowResponse;
import com.tessi.cxm.pfl.ms8.service.restclient.SwitchFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.util.ObjectUtils;

/**
 * Handling process of validate flow and documents of the flow.
 *
 * @author Piseth KHON
 * @author Vichet CHANN
 * @version 1.10.0
 * @since 13 September 2022
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ValidationProcessingHandler extends AbstractExecutionHandler {

  protected static final String PROCESS_ONLY_SCHEDULE_DOCUMENT = "ProcessOnlyScheduleDocument";
  private final SwitchFeignClient switchFeignClient;

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
    final PortalSwitchRequestDto portalSwitchRequestDto =
        context.get(FlowTreatmentConstants.DOCUMENT_VALIDATION, PortalSwitchRequestDto.class);
    final PortalFlowFileControl portalFlowFileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    FlowProcessingResponse<Object> flowProcessingResponse;
    try {
      flowProcessingResponse =
          switchFeignClient.switchPortalProcessing(
              portalSwitchRequestDto, funcKey, privKey, this.getToken(context, FlowTreatmentConstants.BEARER_TOKEN));
    } catch (Exception e) {
      log.error("Unable to process validation flow because: ", e);
      throw new ExecutionException(e.getMessage(), e);
    }

    if (Message.FINISHED.equalsIgnoreCase(flowProcessingResponse.getMessage())) {
      var isValidateAction = context.get(FlowTreatmentConstants.IS_VALIDATE_ACTION, Boolean.class);
      if (isValidateAction != null && !isValidateAction) {
        return ExecutionState.END;
      }
      context.put(FlowTreatmentConstants.FLOW_TYPE, portalSwitchRequestDto.getFlowType());
      context.put(
          FlowTreatmentConstants.SWITCH_FLOW_RESPONSE,
          new SwitchFlowResponse(portalSwitchRequestDto.getFileControl()));
      context.put(
          FlowTreatmentConstants.USERNAME, portalSwitchRequestDto.getFileControl().getUserName());
      var flowStatusModel =
          new UpdateFlowStatusModel(
              portalFlowFileControl,
              FlowTraceabilityStatus.IN_PROCESS,
              context.get(FlowTreatmentConstants.CREATED_BY, String.class));
      flowStatusModel.setValidateDocument(
          context.get(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, Boolean.class));
      flowStatusModel.setOnlyScheduleDocument(
          BooleanUtils.toBoolean(context.get(PROCESS_ONLY_SCHEDULE_DOCUMENT, Boolean.class)));
      var unloadingDate = context.get(FlowTreatmentConstants.FORCE_UNLOADING_DATE, Date.class);
      if (Objects.nonNull(unloadingDate)) {
        flowStatusModel.setUnloadingDate(unloadingDate);
      }
      context.put(FlowTreatmentConstants.SWITCH_FLOW_DOCS_KAFKA, flowStatusModel);
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.SWITCH);
      return ExecutionState.NEXT;
    }
    return ExecutionState.END;
  }

  protected PortalFlowFileControl getValidationFlow(ExecutionContext context) {
    return context.get(
        FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
  }

  protected boolean checkFlowNullable(ExecutionContext context) {
    return ObjectUtils.isEmpty(this.getValidationFlow(context));
  }
}
