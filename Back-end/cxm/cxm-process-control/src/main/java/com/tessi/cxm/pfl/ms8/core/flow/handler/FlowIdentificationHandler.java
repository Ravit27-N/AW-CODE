package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.service.restclient.ProcessingFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class FlowIdentificationHandler extends AbstractExecutionHandler {
  private final ProcessingFeignClient processingFeignClient;
  private final ObjectMapper objectMapper;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    log.info("--- Start Flow Identification ---");
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.IDENTIFICATION);

    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var bearerToken = context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class);
    if (StringUtils.isNotBlank(bearerToken)) {
      bearerToken = BearerAuthentication.PREFIX_TOKEN.concat(bearerToken);
    }

    log.info("Identifying model of flow {}.", depositedFlowLaunchRequest.getUuid());

    var flowName = "";
    if (FlowTreatmentConstants.PORTAL_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      flowName = depositedFlowLaunchRequest.getFileName();
    }

    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);

    var response =
        this.processingFeignClient.getChannelAndSubChannel(
            depositedFlowLaunchRequest.getFileId(),
            depositedFlowLaunchRequest.getFlowType(),
            depositedFlowLaunchRequest.getIdCreator(),
            flowName,
            funcKey,
            privKey,
            bearerToken);
    log.info("response = '" + response + "'");

    if (response.getMessage().equalsIgnoreCase(Message.FINISHED)) {
      var content =
          this.objectMapper.convertValue(
              response.getData(), ProcessCtrlIdentificationResponse.class);
      log.info("content = '" + content + "'");
      if (content != null) {
        log.info("Flow type = '" + depositedFlowLaunchRequest.getFlowType() + "', " +
                "DepositType = '" + depositedFlowLaunchRequest.getDepositType() + "', " +
                "Extension = '" + depositedFlowLaunchRequest.getExtension() + "'");
        if (!PortalDepositType.isPortalDepositCampaignType(
            depositedFlowLaunchRequest.getFlowType())) {
          if (!org.springframework.util.StringUtils.hasText(content.getModelName())) {
            throw new ExecutionException("Unable to identify the model of a PDF file");
          }
          context.put(ProcessControlConstants.ATTACHMENT_DTO, content.getAttachments());
          context.put(ProcessControlConstants.BACKGROUND_DTO, content.getBackground());
          context.put(FlowTreatmentConstants.MODEL_NAME, content.getModelName());
          context.put(ProcessControlConstants.DEFAULT_SIGNATURE, content.getSignature());
        }

        context.put(FlowTreatmentConstants.MODEL_TYPE, content.getModelType());
        context.put(FlowTreatmentConstants.CHANNEL, content.getChannel());
        context.put(FlowTreatmentConstants.SUB_CHANNEL, content.getSubChannel());
        log.info("Model of flow {} is identified.", depositedFlowLaunchRequest.getUuid());
        log.info("--- End Flow Identification ---");
        return ExecutionState.NEXT;
      }
    }
    var errorMessage =
        String.format("%s %s.", "Unable to identify flow with id {}.", depositedFlowLaunchRequest);
    throw new ExecutionException(errorMessage);
  }
}
