package com.tessi.cxm.pfl.ms32.service.consumer;

import com.tessi.cxm.pfl.ms32.service.FlowTraceabilityReportService;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowTraceabilityReportModel;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * A consumer that consume a message to create a flow.
 *
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#CREATE_FLOW_TRACEABILITY_REPORT_TOPIC
 */
@RequiredArgsConstructor
@Component("createFlowTraceabilityReport")
@Slf4j
public class CreateFlowTraceabilityReportConsumer
    implements Consumer<CreateFlowTraceabilityReportModel> {

  private final FlowTraceabilityReportService flowTraceability;

  @Override
  public void accept(CreateFlowTraceabilityReportModel createFlowTraceabilityReportModel) {
    log.info("<< CREATE_FLOW_TRACEABILITY_REPORT >>");
    log.debug("Payload: {}.", createFlowTraceabilityReportModel);
    try {
      this.flowTraceability.create(createFlowTraceabilityReportModel);
    } catch (Exception exception) {
      log.error(
          "Failed to create Flow traceability report: "
              + createFlowTraceabilityReportModel.getFlowId()
              + ".",
          exception);
    }
  }
}
