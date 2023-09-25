package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;

@Slf4j
public abstract class AbstractRequestValidationHandler extends AbstractExecutionHandler {

  @SuppressWarnings("unchecked")
  protected <T extends List<?>> T cast(Object obj) {
    return (T) obj;
  }

  private final StreamBridge streamBridge;

  protected AbstractRequestValidationHandler(StreamBridge streamBridge) {
    this.streamBridge = streamBridge;
  }
  /** Produce message to update flow and flow document to status schedule. */
  protected void updateFlowValidation(UpdateFlowStatusModel updateFlowStatus) {
    final boolean sent =
        this.streamBridge.send(KafkaUtils.UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC, updateFlowStatus);
    if (sent) {
      log.info(
          "Flow validation is updated to the topic {}",
          KafkaUtils.UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC);
    }
  }
}
