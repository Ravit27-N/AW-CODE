package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ComposedCampaignResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ComposedFlowResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.service.restclient.CompositionFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class FlowCompositionHandler extends AbstractExecutionHandler {
  private final ObjectMapper objectMapper;
  private final CompositionFeignClient compositionFeignClient;
  private final FileCtrlMngtFeignClient fileCtrlMngtFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.COMPOSITION);
    String flowtype = context.get(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
            DepositedFlowLaunchRequest.class).getDepositType();
    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
            flowtype) && flowtype.contains("pdf")) {
      return ExecutionState.NEXT;
    }
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    log.info("Composing files of flow {}.", depositedFlowLaunchRequest.getUuid());

    var response = this.getComposedFile(context);

    if (response != null && Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
      if (depositedFlowLaunchRequest
          .getDepositType()
          .equals(FlowTreatmentConstants.BATCH_DEPOSIT)) {
        var composedFlow =
            this.objectMapper.convertValue(response.getData(), ComposedFlowResponse.class);
        log.info("Composited file id {} .", composedFlow.getComposedFileId());

        context.put(FlowTreatmentConstants.COMPOSED_FILE_ID, composedFlow.getComposedFileId());
        context.put(
            FlowTreatmentConstants.FLOW_FILE_DOCUMENT_PROCESSING, composedFlow.getProcessing());
      } else {
        var composedFlow =
            this.objectMapper.convertValue(response.getData(), ComposedCampaignResponse.class);

        context.put(FlowTreatmentConstants.COMPOSED_FILE_ID, composedFlow.getComposedFileId());
        context.put(
            FlowTreatmentConstants.FLOW_FILE_DOCUMENT_PROCESSING, composedFlow.getProcessing());
      }
      log.info("Files of flow {} are composed.", depositedFlowLaunchRequest.getUuid());
    } else {
      throw new ExecutionException(
          "Failed to compose files of flow " + depositedFlowLaunchRequest.getUuid() + ".");
    }

    return ExecutionState.NEXT;
  }

  private FlowProcessingResponse<ComposedFlowResponse> getComposedFile(ExecutionContext context) {

    var request =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var token = ProcessControlExecutionContextUtils.getBearerToken(context);
    var standardizedFileId = context.get(FlowTreatmentConstants.STANDARDIZED_FILE_ID, String.class);
    if (request.getDepositType().equals(FlowTreatmentConstants.BATCH_DEPOSIT)) {
      // Request to get json FileControl
      log.info("Requesting FileControl of flow {}.", request.getUuid());
      var processingJsonFileControl =
          this.fileCtrlMngtFeignClient.getJsonFileControl(request.getUuid(), token);
      String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
      String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
      return this.compositionFeignClient.getComposedFile(
          request.getFlowType(),
          standardizedFileId,
          processingJsonFileControl,
          funcKey,
          privKey,
          token);
    }

    if (PortalDepositType.isPortalDepositCampaignType(request.getFlowType())) {
      // Request to get json FileControl
      log.info("Requesting Portal FileControl of flow {}.", request.getUuid());
      var portalJson =
          this.fileCtrlMngtFeignClient.getPortalJsonFileControl(request.getUuid(), token);
      context.put(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, portalJson);

      // Getting unsubscribe link for Campaign Email, null for SMS
      var campaignFlowLaunchRequest =
          context.get(
              FlowTreatmentConstants.CAMPAIGN_DEPOSIT_FLOW_LAUNCH_REQUEST,
              CampaignDepositFlowLaunchRequest.class);
      var unsubscribeLink = campaignFlowLaunchRequest.getUnsubscribeLink();

      String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
      String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
      return this.compositionFeignClient.getCampaignComposedFile(
          request.getFlowType(),
          standardizedFileId,
          unsubscribeLink,
          portalJson,
          funcKey,
          privKey,
          token);
    }
    throw new ExecutionException("Invalid deposit flow!");
  }
}
