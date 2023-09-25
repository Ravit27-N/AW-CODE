package com.tessi.cxm.pfl.ms32.service.consumer;

import com.tessi.cxm.pfl.ms32.service.FlowDocumentReportService;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowDocumentStatusReportModel;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * A consumer that consume a message for update status of a flow document.
 *
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#UPDATE_FLOW_DOCUMENT_STATUS_REPORT_TOPIC
 */
@RequiredArgsConstructor
@Component("createFlowDocumentStatusReport")
@Slf4j
public class UpdateDocumentStatusReportConsumer
    implements Consumer<UpdateFlowDocumentStatusReportModel> {

  private final FlowDocumentReportService flowDocumentReportService;

  @Override
  public void accept(UpdateFlowDocumentStatusReportModel updateFlowDocumentStatusReportModel) {
    log.info("<< UPDATE_FLOW_DOCUMENT_STATUS_REPORT >>");
    log.debug("Payload: {}.", updateFlowDocumentStatusReportModel);
    this.flowDocumentReportService.updateDocumentStatusReport(updateFlowDocumentStatusReportModel);
  }
}
