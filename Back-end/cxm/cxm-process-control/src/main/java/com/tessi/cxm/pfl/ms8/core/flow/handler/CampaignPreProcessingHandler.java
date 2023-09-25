package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.CampaignPreProcessingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.service.restclient.ProcessingFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import java.util.Locale;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

/**
 * Handle process of passing data to cxm-pre-processing for campaign flow.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 01 Jun 2022
 */
@Log4j2
@AllArgsConstructor
@Component
public class CampaignPreProcessingHandler extends AbstractExecutionHandler {

  private final ProcessingFeignClient processingFeignClient;
  private final FileCtrlMngtFeignClient fileCtrlMngtFeignClient;

  private final ObjectMapper mapper;

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
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.PRE_PROCESSING);
    var campaignFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.CAMPAIGN_DEPOSIT_FLOW_LAUNCH_REQUEST,
            CampaignDepositFlowLaunchRequest.class);
    var portalFlowFileControl =
        this.fileCtrlMngtFeignClient.getPortalJsonFileControl(
            campaignFlowLaunchRequest.getFlowId(),
            this.getToken(context, FlowTreatmentConstants.BEARER_TOKEN));

    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var campaignPreProcessingRequest =
        CampaignPreProcessingRequest.builder()
            .variables(campaignFlowLaunchRequest.getVariables())
            .fileId(campaignFlowLaunchRequest.getFlowId())
            .csvHeader(campaignFlowLaunchRequest.isCsvHeader())
            .removeDuplicate(campaignFlowLaunchRequest.isRemoveDuplicate())
            .channel(portalFlowFileControl.getFlow().getChannel())
            .subChannel(portalFlowFileControl.getFlow().getSubChannel())
            .flowType(portalFlowFileControl.getFlow().getType())
            .flowName(portalFlowFileControl.getFileName())
            .locale(Locale.getDefault())
            .extension(portalFlowFileControl.getExtension())
            .subjectMail(campaignFlowLaunchRequest.getSubjectMail())
            .attachments(campaignFlowLaunchRequest.getAttachments())
            .dateSchedule(campaignFlowLaunchRequest.getDateSchedule())
            .createdBy(campaignFlowLaunchRequest.getCreatedBy())
            .idCreator(depositedFlowLaunchRequest.getIdCreator())
            .build();

    this.setCreatedAt(campaignPreProcessingRequest, portalFlowFileControl.getDepositDate());

    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    var response =
        this.processingFeignClient.getPortalCampaignDocument(
            campaignPreProcessingRequest,
            funcKey,
            privKey,
            this.getToken(context, FlowTreatmentConstants.BEARER_TOKEN));
    if (response.getMessage().equals(Message.FINISHED) && Objects.nonNull(response.getData())) {
      log.info("Success return from cxm-pre-processing");
      var result =
          this.mapper.convertValue(response.getData(), CampaignPreProcessingResponse.class);
      context.put(FlowTreatmentConstants.PORTAL_DOCUMENT, result);
      context.put(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, portalFlowFileControl);
      context.put(FlowTreatmentConstants.ATTACHMENTS, result.getAttachments());
    }

    return ExecutionState.NEXT;
  }

  private void setCreatedAt(CampaignPreProcessingRequest campaignPreProcessingRequest,
      String depositDate) {
    try {
      campaignPreProcessingRequest.setCreatedAt(
          DateUtils.parseDate(depositDate,
              new String[]{"dd/MM/yyyy HH:mm:ss"}));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
