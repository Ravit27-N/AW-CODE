package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Logs;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.model.kafka.FlowFileControlCreateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowHistoryStatus;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CreateFlowTraceabilityHandler extends FlowTraceabilityHandler {

  private final StreamBridge streamBridge;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    log.info("--- Start create flow traceability ---");
    var flowTraceability = this.getFlowTraceability(context);
    log.info(Logs.PRODUCING_FLOW_TRACEABILITY_CREATION_MESSAGE, flowTraceability.getFlowName());

    log.info("Send to kafka");
    this.streamBridge.send(KafkaUtils.INITIAL_FLOW_TRACEABILITY_TOPIC, flowTraceability);

    log.info(Logs.PRODUCED_FLOW_TRACEABILITY_CREATION_MESSAGE, flowTraceability.getFlowName());
    log.info("--- End create flow traceability ---");
    return ExecutionState.NEXT;
  }

  private FlowFileControlCreateFlowTraceabilityModel getFlowTraceability(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var userCreator = context.get(FlowTreatmentConstants.USER_CREATOR, String.class);
    if (StringUtils.isBlank(userCreator)) {
      userCreator = depositedFlowLaunchRequest.getUserName();
    }

    log.info("Deposit request: {}", depositedFlowLaunchRequest);

    var flowTraceability = new FlowFileControlCreateFlowTraceabilityModel();

    flowTraceability.setDepositDate(depositedFlowLaunchRequest.getDepositDate());
    flowTraceability.setDepositMode(depositedFlowLaunchRequest.getDepositType());
    flowTraceability.setFlowName(
        this.buildFlowName(
            depositedFlowLaunchRequest.getFileName(), depositedFlowLaunchRequest.getFlowType()));
    flowTraceability.setCreatedBy(userCreator);
    flowTraceability.setService(depositedFlowLaunchRequest.getServiceId());
    flowTraceability.setChannel("");
    flowTraceability.setSubChannel("");
    flowTraceability.setFileId(depositedFlowLaunchRequest.getFileId());
    flowTraceability.setFullName(depositedFlowLaunchRequest.getFullName());
    flowTraceability.setCampaignName(depositedFlowLaunchRequest.getCampaignName());

    flowTraceability.setOwnerId(depositedFlowLaunchRequest.getIdCreator());

    var modelName = context.get(FlowTreatmentConstants.MODEL_NAME, String.class);
    flowTraceability.setModelName(modelName);

    var flowType = context.get(FlowTreatmentConstants.FLOW_TYPE, String.class);

    var portalPdfType =
        FlowTreatmentConstants.PORTAL_DEPOSIT.concat("/").concat(FlowTreatmentConstants.PORTAL_PDF);
    log.info("portalPdfType = '" + portalPdfType + "'");
    if (flowType.contains(portalPdfType)) {
      // Set status for portal PDF.
      log.info("modelName = '" + modelName + "'");
      if (StringUtils.isBlank(modelName)) {
        log.info("Set status to error");
        flowTraceability.setStatus(FlowHistoryStatus.IN_ERROR.getValue());
      } else {
        log.info("Set status to deposited");
        flowTraceability.setStatus(FlowHistoryStatus.DEPOSITED.getValue());
      }
    } else {
      // Set status for other flow types.
      log.info("Set status to deposited");
      flowTraceability.setStatus(FlowHistoryStatus.DEPOSITED.getValue());
    }
    log.info("Set server to = '" + ComputerSystemProduct.getDeviceId() + "'");
    flowTraceability.setServer(ComputerSystemProduct.getDeviceId());
    flowTraceability.setDateStatus(new Date());
    if (PortalDepositType.isPortalDepositCampaignType(flowType)) {
      flowTraceability.setStatus("");
    }
    if (PortalDepositType.isBatchDepositType(flowType)) {
      var htmlContent =
          context.get(
              FlowTreatmentConstants.BASE64_NAME.concat(
                  "_".concat(context.get(FlowTreatmentConstants.FILE_ID, String.class))),
              String.class);
      log.info("htmlContent = '" + htmlContent + "'");
      flowTraceability.setHtmlContent(htmlContent);
    }
    log.info("PortalDepositType = '" + PortalDepositType.getPortalDepositType(
            depositedFlowLaunchRequest.getFlowType()).name() + "'");
    flowTraceability.setPortalDepositType(
            PortalDepositType.getPortalDepositType(depositedFlowLaunchRequest.getFlowType()).name());
    return flowTraceability;
  }
}
