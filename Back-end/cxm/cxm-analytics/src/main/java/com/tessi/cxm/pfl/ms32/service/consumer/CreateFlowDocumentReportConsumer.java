package com.tessi.cxm.pfl.ms32.service.consumer;

import com.tessi.cxm.pfl.ms32.service.FlowDocumentReportService;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentReportModel;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * A consumer that consume a message to create a document.
 *
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#CREATE_FLOW_DOCUMENT_REPORT_TOPIC
 */
@RequiredArgsConstructor
@Component("createFlowDocumentReport")
@Slf4j
public class CreateFlowDocumentReportConsumer implements Consumer<CreateFlowDocumentReportModel> {

  private final FlowDocumentReportService flowDocumentReportService;

  @Override
  public void accept(CreateFlowDocumentReportModel payload) {
    log.info("<<CREATE_FLOW_DOCUMENT_REPORT: {}>>", payload);
    try {
      this.flowDocumentReportService.createDocumentReport(payload);
    } catch (Exception exception) {
      log.error(
          "Failed to create Flow document report ["
              + payload.getDocumentId()
              + "] of Flow traceability ["
              + payload.getFlowId()
              + "].",
          exception);
    }
  }
}
