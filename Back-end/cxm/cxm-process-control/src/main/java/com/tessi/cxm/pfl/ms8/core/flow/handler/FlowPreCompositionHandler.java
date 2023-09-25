package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProCompositionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.service.restclient.CompositionFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class FlowPreCompositionHandler extends AbstractExecutionHandler {
  private final ObjectMapper objectMapper;
  private final CompositionFeignClient compositionFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    log.info("--- Start FlowPreCompositionHandler ---");
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.PRE_COMPOSITION);
    String flowtype = context.get(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
            DepositedFlowLaunchRequest.class).getDepositType();
    log.info("flowtype = '" + flowtype + "'");
    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
            flowtype) && flowtype.contains("pdf")) {
      return ExecutionState.NEXT;
    }

    var bearerToken = ProcessControlExecutionContextUtils.getBearerToken(context);
    var flowId = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);

    log.info("Normalizing files of flow {}.", flowId);

    var response = preComposedFlowResponse(context, bearerToken);

    if (Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
      var preComposedFlowResponse =
          this.objectMapper.convertValue(response.getData(), ProCompositionResponse.class);

      if (StringUtils.isNotBlank(preComposedFlowResponse.getStandardizedFileId())) {
        context.put(
            FlowTreatmentConstants.STANDARDIZED_FILE_ID,
            preComposedFlowResponse.getStandardizedFileId());

        log.info("Files of flow {} has bean normalized.", flowId);

        return ExecutionState.NEXT;
      }
    }

    throw new ExecutionException("Failed to normalize flow " + flowId + ".");
  }

  private FlowProcessingResponse<ProCompositionResponse> preComposedFlowResponse(
      ExecutionContext context, String token) {
    var request =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    if (request.getDepositType().equalsIgnoreCase(FlowTreatmentConstants.BATCH_DEPOSIT)) {
      String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
      String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
      return this.compositionFeignClient.preComposeFlow(
          request.getFileId(),
          request.getIdCreator(),
          request.getFlowType(),
          funcKey,
          privKey,
          token);
    }

    if (PortalDepositType.isPortalDepositCampaignType(request.getFlowType())) {
      var campaignFlowLaunchRequest =
          context.get(
              FlowTreatmentConstants.CAMPAIGN_DEPOSIT_FLOW_LAUNCH_REQUEST,
              CampaignDepositFlowLaunchRequest.class);
      String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
      String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
      return this.compositionFeignClient.campaignPreComposeFlow(
          request.getUuid(),
          List.of(campaignFlowLaunchRequest.getVariables()),
          funcKey,
          privKey,
          token);
    }

    throw new ExecutionException("Invalid deposit type or flow type!");
  }
}
