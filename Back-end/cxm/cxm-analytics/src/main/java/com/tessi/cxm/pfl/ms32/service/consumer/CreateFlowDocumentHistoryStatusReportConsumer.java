package com.tessi.cxm.pfl.ms32.service.consumer;

import com.tessi.cxm.pfl.ms32.service.FlowDocumentReportService;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentHistoryStatusReportModel;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("createFlowDocumentHistoryStatusReport")
@RequiredArgsConstructor
public class CreateFlowDocumentHistoryStatusReportConsumer
    implements Consumer<CreateFlowDocumentHistoryStatusReportModel> {
  private final FlowDocumentReportService flowDocumentReportService;

  @Override
  public void accept(CreateFlowDocumentHistoryStatusReportModel payload) {
    log.info("<<UPDATE_FLOW_DOCUMENT_REPORT_EVENT_HISTORY>>");
    log.debug("payload {}", payload);
    try {
      this.flowDocumentReportService.createFlowDocumentEventHistory(payload);
    } catch (Exception exception) {
      log.error(
          "Failed to create Flow document history report ["
              + payload.getId()
              + "] of Flow document ["
              + payload.getFlowDocumentId()
              + "].",
          exception);
    }
  }
}
