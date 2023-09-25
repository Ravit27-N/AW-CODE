package com.tessi.cxm.pfl.ms8.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignAttachment;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.core.flow.portal.campaign.CampaignProcessingLauncher;
import com.tessi.cxm.pfl.ms8.core.flow.portal.campaign.ManualFlowInitializer;
import com.tessi.cxm.pfl.ms8.core.flow.portal.campaign.ManualFlowModifier;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.model.PrivilegeHelper;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.DateUtils;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handling process campaign sms flow processing.
 *
 * @author Vichet CHANN
 * @since 24 May 2022
 */
@Service
@Log4j2
public class ProcessControlCampaignService {

  private static final String CURRENT_SERVER_NAME;

  static {
    CURRENT_SERVER_NAME = ComputerSystemProduct.getDeviceId();
  }

  private static final String ERROR_MESSAGE = "Flow processing encounter an error.";
  private final StreamBridge streamBridge;
  private final ManualFlowInitializer manualFlowInitializer;
  private final ManualFlowModifier manualFlowModifier;
  private final CampaignProcessingLauncher campaignProcessingLauncher;

  public ProcessControlCampaignService(
      StreamBridge streamBridge,
      ManualFlowInitializer manualFlowInitializer,
      ManualFlowModifier manualFlowModifier,
      CampaignProcessingLauncher campaignProcessingLauncher) {
    this.streamBridge = streamBridge;
    this.manualFlowInitializer = manualFlowInitializer;
    this.manualFlowModifier = manualFlowModifier;
    this.campaignProcessingLauncher = campaignProcessingLauncher;
  }

  /**
   * To initialize process of campaign sms flow to create json file to cxm-file-control-management
   * and produce message to create a new flow in cxm-flow-traceability.
   *
   * @param request refers to object of {@link DepositedFlowLaunchRequest} require in flow
   * @param headers refers to require headers in the flow
   */
  public void initializeCampaign(final DepositedFlowLaunchRequest request, String funcKey, String privKey, HttpHeaders headers) {
    var context = new ExecutionContext();
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, request);
    context.put(FlowTreatmentConstants.FLOW_TYPE, request.getFlowType());
    context.put(FlowTreatmentConstants.FLOW_UUID, request.getUuid());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, BearerAuthentication.getToken(headers));
    context.put(FlowTreatmentConstants.CAMPAIGN_NAME, request.getCampaignName());
    context.put(FlowTreatmentConstants.IS_SET_SCHEDULE, false);
    context.put(FlowTreatmentConstants.OWNER_ID, request.getIdCreator());
    context.put(FlowTreatmentConstants.FUNC_KEY, funcKey);
    context.put(FlowTreatmentConstants.PRIV_KEY, privKey);
    try {
      log.info("Campaign sms request object: {}", request);
      if (request.isNew()) {
        this.manualFlowInitializer.execute(context);
      } else {
        context.put(
            FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
            ProcessControlStep.ACQUISITION);
        this.manualFlowModifier.execute(context);
      }
    } catch (RuntimeException exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(context);
    }
  }

  /**
   * To launch a campaign SMS after the schedule of campaign start.
   *
   * @param request the object of {@link CampaignDepositFlowLaunchRequest} is required to launch
   *     campaign
   * @param headers refer to the authorization headers contain in {@link HttpHeaders}
   */
  public void launchCampaign(CampaignDepositFlowLaunchRequest request, PrivilegeHelper privilegeHelper, HttpHeaders headers) {
    ExecutionContext context = getExecutionContext(request, headers);
    try {
      context.put(
          FlowTreatmentConstants.IS_SET_SCHEDULE,
          !DateUtils.isSmallerThanOrEqualCurrentDate(request.getDateSchedule()));
      context.put(FlowTreatmentConstants.SENDER_EMAIL, request.getSenderMail());
      context.put(FlowTreatmentConstants.SENDER_NAME, request.getSenderName());
      context.put(ProcessControlConstants.DATE_SCHEDULE, request.getDateSchedule());
      if (!CollectionUtils.isEmpty(request.getAttachments())) {
        final Map<String, String> attachments =
            request.getAttachments().stream()
                .collect(
                    Collectors.toMap(CampaignAttachment::getFileId, CampaignAttachment::getHash));
        context.put(FlowTreatmentConstants.ATTACHMENTS, attachments);
      }
      context.put(FlowTreatmentConstants.FUNC_KEY, privilegeHelper.getFuncKey());
      context.put(FlowTreatmentConstants.PRIV_KEY, privilegeHelper.getPrivKey());
      this.campaignProcessingLauncher.execute(context);
    } catch (RuntimeException exception) {
      log.error(ERROR_MESSAGE, exception);
      this.reportErrorToKafka(context);
    }
  }

  private ExecutionContext getExecutionContext(
      CampaignDepositFlowLaunchRequest request, HttpHeaders headers) {
    var context = new ExecutionContext();
    context.put(FlowTreatmentConstants.FLOW_UUID, request.getFlowId());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, BearerAuthentication.getToken(headers));
    context.put(FlowTreatmentConstants.CAMPAIGN_DEPOSIT_FLOW_LAUNCH_REQUEST, request);
    context.put(FlowTreatmentConstants.CREATED_BY, request.getCreatedBy());
    
    return context;
  }

  private void reportErrorToKafka(ExecutionContext executionContext) {
    executionContext.put(
        FlowTreatmentConstants.FLOW_PROCESSING_STATUS, FlowTraceabilityStatus.IN_ERROR.getValue());

    var processControlExecutionStep =
        executionContext.get(
            FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.class);

    if (processControlExecutionStep != null && processControlExecutionStep.getStepOrder() >= 1) {
      this.updateFlowTraceabilityStatus(executionContext);
    }
  }

  /**
   * Produce message to update FLowTraceability's status identified by fileId field.
   *
   * @param context Current execution context. Use for required data to create message.
   */
  private void updateFlowTraceabilityStatus(ExecutionContext context) {
    var launchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var message = new BaseUpdateFlowFromProcessCtrl();
    message.setStatus(context.get(FlowTreatmentConstants.FLOW_PROCESSING_STATUS, String.class));
    message.setFileId(launchRequest.getFileId());
    message.setCreatedBy(launchRequest.getUserName());
    message.setServer(CURRENT_SERVER_NAME);

    this.streamBridge.send(KafkaUtils.UPDATE_FLOW_STATUS_BY_FILE_ID_TOPIC, message);
  }
}
