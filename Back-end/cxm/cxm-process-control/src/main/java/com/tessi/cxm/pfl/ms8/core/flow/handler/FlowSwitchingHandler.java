package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.BatchSwitchingRequestDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PortalSwitchRequestDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.tessi.cxm.pfl.ms8.model.SwitchFlowResponse;
import com.tessi.cxm.pfl.ms8.service.restclient.SwitchFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.FlowFileControl;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * To handle process of switching to cxm-switch microservice.
 *
 * @author Vichet CHANN
 */
@Log4j2
@Component
@AllArgsConstructor
public class FlowSwitchingHandler extends AbstractExecutionHandler {

  private final SwitchFeignClient switchFeignClient;
  private final KeycloakService keycloakService;

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
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.SWITCH);
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      return this.switchBatchDeposit(context);
    }
    return this.switchPortalDeposit(context);
  }

  private ExecutionState switchBatchDeposit(ExecutionContext context) {
    var fileControl = context.get(FlowTreatmentConstants.BATCH_JSON_FILE_CONTROL,
        FlowFileControl.class);
    context.put(FlowTreatmentConstants.FLOW_TYPE, fileControl.getFlow().getType());
    var request =
        BatchSwitchingRequestDto.builder()
            .flowType(fileControl.getFlow().getType())
            .composedFileId(context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class))
            .fileControl(fileControl)
            .created(fileControl.getUserName())
            .build();
    var token = ProcessControlExecutionContextUtils.getBearerToken(context);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    var response = this.switchFeignClient.switchBatchProcessing(request, funcKey, privKey, token);
    if (Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
      return ExecutionState.NEXT;
    }
    return ExecutionState.END;
  }
  @SuppressWarnings("unchecked")
  private ExecutionState switchPortalDeposit(ExecutionContext context) {

    PortalFlowFileControl fileControl = context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL,
        PortalFlowFileControl.class);
    context.put(FlowTreatmentConstants.FLOW_TYPE, fileControl.getFlow().getType());
    fileControl.setUserName(this.getUsername(context));
    var request =
        PortalSwitchRequestDto.builder()
            .flowType(fileControl.getFlow().getType())
            .composedFileId(context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class))
            .fileControl(fileControl)
            .created(context.get(FlowTreatmentConstants.CREATED_BY, String.class))
            .build();
    var token = ProcessControlExecutionContextUtils.getBearerToken(context);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    FlowProcessingResponse<Object> response;

    try {
      if (!PortalDepositType.isPortalDepositCampaignType(fileControl.getFlow().getType())) {
        response = this.switchFeignClient.switchPortalProcessing(request, funcKey, privKey, token);
      } else {
        if (fileControl
            .getFlow()
            .getType()
            .contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_EMAIL)) {
          request.setSenderEmail(context.get(FlowTreatmentConstants.SENDER_EMAIL, String.class));
          request.setSenderName(context.get(FlowTreatmentConstants.SENDER_NAME, String.class));
          request.setAttachments(context.get(FlowTreatmentConstants.ATTACHMENTS, Map.class));

        }
        response = this.switchFeignClient.portalSwitchingCampaign(request, funcKey, privKey, token);
      }
    } catch (Exception e) {
      log.error("Unable to process flow because: ", e);
      throw new ExecutionException(e.getMessage());
    }

    if (Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
      context.put(FlowTreatmentConstants.SWITCH_FLOW_RESPONSE, new SwitchFlowResponse(fileControl));
      context.put(FlowTreatmentConstants.USERNAME, fileControl.getUserName());
      var flowStatusModel =
          new UpdateFlowStatusModel(
              fileControl,
              FlowTraceabilityStatus.IN_PROCESS, "");
      context.put(FlowTreatmentConstants.SWITCH_FLOW_DOCS_KAFKA, flowStatusModel);
      return ExecutionState.NEXT;
    }
    return ExecutionState.END;
  }

  private String getUsername(ExecutionContext context) {
    if (StringUtils.hasText(context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class))
        && BooleanUtils.isFalse(
            context.get(FlowTreatmentConstants.PROCESS_BY_SCHEDULER, Boolean.class))) {
      return this.keycloakService.getUserInfo().getUsername();
    } else {
      return context.get(FlowTreatmentConstants.USERNAME, String.class);
    }
  }
}
