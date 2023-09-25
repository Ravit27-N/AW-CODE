package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Handling process of consuming a topic to cancel a flow.
 *
 * @author Vichet CHANN
 * @version 1.6.0
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#CANCEL_FLOW_TOPIC
 * @since 01 Jun 2022
 */
@Log4j2
@Component("cancelFlowTraceability")
public class CancelFlowTraceabilityListener
    extends AbstractFlowTraceabilityConsumer<BaseUpdateFlowFromProcessCtrl> {


  @Override
  public void accept(BaseUpdateFlowFromProcessCtrl payload) {
    log.info("<<CANCEL_FLOW_AND_FLOW_DOCUMENT: {}>>", payload);
    try {
      this.getFlowByFileId(payload.getFileId())
          .ifPresent(
              flow -> {
                if (unableToCancel(flow.getStatus())) {
                  flow.setStatus(FlowTraceabilityStatus.CANCELED.getValue());
                  flow.setDateStatus(new Date());
                  flow.setLastModifiedBy(payload.getCreatedBy());
                  AtomicBoolean stillInProgress = new AtomicBoolean(false);
                  final List<FlowDocument> flowDocuments =
                      this.getFlowDocumentsByFlowId(flow.getId()).stream()
                          .map(
                              doc -> {
                                if (FlowTraceabilityStatusConstant.IN_PROGRESS.equalsIgnoreCase(
                                    doc.getStatus())) {
                                  stillInProgress.set(true);
                                }
                                return mapDocument(doc, payload);
                              })
                          .collect(Collectors.toUnmodifiableList());
                  if (stillInProgress.get()) {
                    flow.setStatus(FlowTraceabilityStatus.IN_PROCESS.getValue());
                    payload.setStatus(FlowTraceabilityStatus.IN_PROCESS.getValue());
                  }
                  // update flow traceability
                  this.updateFlowTraceability(flow, payload, true);

                  // update flow  documents
                  this.saveAllFlowDocuments(flowDocuments);

                  // Update flow document status report.
                  TransactionSynchronizationManager.registerSynchronization(
                      new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                          if (status == TransactionSynchronization.STATUS_COMMITTED) {
                            updateFlowDocumentStatusReports(flowDocuments);
                          }
                        }
                      });
                }
              });

    } catch (RuntimeException e) {
      log.warn(e);
    }
  }

  private boolean unableToCancel(String status) {
    return Stream.of(
            FlowTraceabilityStatusConstant.IN_ERROR,
            FlowTraceabilityStatusConstant.IN_PROGRESS,
            FlowTraceabilityStatusConstant.CANCELED,
            FlowTraceabilityStatusConstant.COMPLETED,
            FlowTraceabilityStatusConstant.REFUSE_DOC)
        .noneMatch(status::equalsIgnoreCase);
  }

  private FlowDocument mapDocument(FlowDocument document, BaseUpdateFlowFromProcessCtrl payload) {

    if (unableToCancel(document.getStatus())) {
      document.setStatus(FlowDocumentStatus.CANCELED.getValue());
      document.setDateStatus(new Date());
      document.setLastModifiedBy(payload.getCreatedBy());
      document.setLastModified(new Date());
      var history =
          document.getFlowDocumentHistories().stream()
              .filter(
                  his -> his.getEvent().equalsIgnoreCase(FlowDocumentStatus.CANCELED.getValue()))
              .findFirst()
              .orElse(
                  new FlowDocumentHistory(document, payload.getServer(), payload.getCreatedBy()));
      history.setDateTime(document.getDateStatus());
      history.setLastModifiedBy(payload.getCreatedBy());
      history.setLastModified(new Date());
      document.addFlowDocumentHistory(history);
    }
    return document;
  }
}
