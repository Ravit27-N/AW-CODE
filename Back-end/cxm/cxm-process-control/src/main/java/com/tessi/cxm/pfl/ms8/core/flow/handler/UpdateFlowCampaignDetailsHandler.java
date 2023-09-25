package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.model.kafka.FlowCampaignDetailsModel;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * To handle process to update flow campaign details (htmlTemplate) after
 * <strong>Composition</strong>.
 *
 * @author Sokhour LACH
 * @since 10 Jan 2023
 * @version 1.17.0
 * @see KafkaUtils#UPDATE_FLOW_CAMPAIGN_HTML_TEMPLATE_AFTER_COMPOSITION_TOPIC
 */
@Component
@Slf4j
@AllArgsConstructor
public class UpdateFlowCampaignDetailsHandler extends AbstractExecutionHandler {

  private final StreamBridge streamBridge;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var flowProcessingStep =
        context.get(
            FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.class);
    var depositRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    if (flowProcessingStep == ProcessControlStep.COMPOSITION
        && PortalDepositType.isPortalDepositCampaignType(depositRequest.getFlowType())) {
      updateFlowCampaignDetails(context);
    }
    return ExecutionState.NEXT;
  }

  private void updateFlowCampaignDetails(ExecutionContext context) {
    var flowCampaignDetails =
        FlowCampaignDetailsModel.builder()
            .fileId(context.get(FlowTreatmentConstants.FLOW_UUID, String.class));
    var htmlContent =
        context.get(ProcessControlConstants.HTML_CONTENT_PORTAL_DEPOSIT_CAMPAIGN, String.class);
    flowCampaignDetails.htmlTemplate(htmlContent);

    // produce message to update htmlTemplate for the flow campaign details (produce to cxm-flow-traceability)
    streamBridge.send(
        KafkaUtils.UPDATE_FLOW_CAMPAIGN_HTML_TEMPLATE_AFTER_COMPOSITION_TOPIC,
        flowCampaignDetails.build());
  }
}
