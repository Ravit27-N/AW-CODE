package com.tessi.cxm.pfl.ms32.service.consumer;

import com.tessi.cxm.pfl.ms32.service.FlowTraceabilityReportService;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowTraceabilityReportModel;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * A consumer that consume a message to create a flow.
 *
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#UPDATE_FLOW_TRACEABILITY_REPORT_TOPIC
 */
@RequiredArgsConstructor
@Component("updateFlowTraceabilityReport")
@Slf4j
public class UpdateFlowTraceabilityReportConsumer
    implements Consumer<UpdateFlowTraceabilityReportModel> {
  private final FlowTraceabilityReportService flowTraceability;

  @Override
  public void accept(UpdateFlowTraceabilityReportModel payload) {
    log.info("<< UPDATE_FLOW_TRACEABILITY_REPORT >>");
    log.debug("Payload: {}", payload);
    try {
      this.flowTraceability.update(payload);
    } catch (Exception exception) {
      log.error(
          "Failed to update Flow traceability report: " + payload.getFlowId() + ".", exception);
    }
  }
}
