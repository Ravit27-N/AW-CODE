package com.tessi.cxm.pfl.ms8.service.manual;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessControlServiceManualImp implements ProcessControlServiceManual {

  private static final String CURRENT_SERVER_NAME;

  static {
    CURRENT_SERVER_NAME = ComputerSystemProduct.getDeviceId();
  }

  private final FlowProcessingManagerManual flowProcessingManagerManual;
  private final StreamBridge streamBridge;
  private ExecutionContext executionContext;

  public ProcessControlServiceManualImp(FlowProcessingManagerManual flowProcessingManagerManual,
      StreamBridge streamBridge) {
    this.flowProcessingManagerManual = flowProcessingManagerManual;
    this.streamBridge = streamBridge;
  }

  @Override
  public ProcessCtrlIdentificationResponse identityLauncher(
      DepositedFlowLaunchRequest launchRequest, String bearerToken) {
    this.flowProcessingManagerManual.setNumberOfBean(List.of(1, 2));
    this.lunchProcess(launchRequest, bearerToken);
    return ProcessCtrlIdentificationResponse.builder()
        .channel(this.executionContext.get(FlowTreatmentConstants.MODEL_NAME, String.class))
        .modelName(this.executionContext.get(FlowTreatmentConstants.CHANNEL, String.class))
        .subChannel(this.executionContext.get(FlowTreatmentConstants.SUB_CHANNEL, String.class))
        .build();
  }

  @Override
  public ProcessCtrlIdentificationResponse preProcessingLauncher(
      DepositedFlowLaunchRequest launchRequest, String bearerToken) {
    this.flowProcessingManagerManual.setNumberOfBean(List.of(2));
    this.lunchProcess(launchRequest, bearerToken);
    return new ProcessCtrlIdentificationResponse();
  }

  private ExecutionContext getExecutionContext(
      DepositedFlowLaunchRequest launchRequest, String bearerToken) {
    var context = new ExecutionContext();
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, launchRequest);
    context.put(FlowTreatmentConstants.FLOW_TYPE, launchRequest.getFlowType());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, bearerToken);
    return context;
  }

  @Override
  public void updateFlowTraceabilityStatus(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var message = new BaseUpdateFlowFromProcessCtrl();
    message.setStatus(context.get(FlowTreatmentConstants.FLOW_PROCESSING_STATUS, String.class));
    message.setFileId(depositedFlowLaunchRequest.getFileId());
    message.setCreatedBy(depositedFlowLaunchRequest.getUserName());
    message.setServer(CURRENT_SERVER_NAME);

    this.streamBridge.send(KafkaUtils.UPDATE_FLOW_STATUS_BY_FILE_ID_TOPIC, message);
  }

  private void lunchProcess(DepositedFlowLaunchRequest launchRequest, String bearerToken) {
    var context = getExecutionContext(launchRequest, bearerToken);
    try {
      this.flowProcessingManagerManual.execute(context);
    } catch (RuntimeException exception) {
      log.error("Flow processing encounter an error.", exception);

      context.put(
          FlowTreatmentConstants.FLOW_PROCESSING_STATUS,
          FlowTraceabilityStatus.IN_ERROR.getValue());

      var processControlExecutionStep =
          context.get(
              FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
              ProcessControlStep.class);

      if (processControlExecutionStep != null && processControlExecutionStep.getStepOrder() >= 1) {
        this.updateFlowTraceabilityStatus(context);
      }
    }
    this.executionContext = context;
  }
}
