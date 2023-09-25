package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.ms3.util.FlowDigitalType;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.CompositionUpdateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.model.kafka.CompositionUpdateFlowTraceabilityModel.Document;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@code UpdateFlowAfterCompositionStepListener} - Perform consume event messages from the producer
 * of deposit flow after finished {@code Composition} step to update flow traceability and create
 * history, and flow document.
 *
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#UPDATE_FLOW_AFTER_COMPOSITION_STEP_TOPIC
 */
@Slf4j
@Component("updateFlowTraceabilityAfterCompositionStep")
public class UpdateFlowAfterCompositionStepListener
    extends AbstractFlowTraceabilityConsumer<CompositionUpdateFlowTraceabilityModel> {

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(value = StatusNotInOrderException.class, maxAttempts = 10, backoff = @Backoff(delay = 300))
  @Override
  public void accept(CompositionUpdateFlowTraceabilityModel payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_AFTER_COMPOSITION_STEP: {} >>", payload);

      var flowTraceability = this.getFlowTraceabilityByFileId(payload.getFileId());
      int currentStep = 4;
      this.validateBatchStatusOrder(flowTraceability, List.of(currentStep - 1));

      flowTraceability.setStatus(payload.getStatus());
      flowTraceability.setLastModifiedBy(payload.getCreatedBy());

      if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
          flowTraceability.getDepositMode())) {
        var flowDetails = flowTraceability.getFlowTraceabilityDetails();
        flowDetails.setStep(currentStep);
      }

      if (List.of(FlowDigitalType.EMAIL, FlowDigitalType.SMS)
          .contains(flowTraceability.getSubChannel().toUpperCase())) {
        flowTraceability.setDateStatus(payload.getDateSchedule());
        this.updateCreatedAtForEmailAndSms(flowTraceability);
      }
      // Flow History.
      var baseUpdate = mapping(payload, BaseUpdateFlowFromProcessCtrl.class);

      this.saveFlowTraceability(flowTraceability);

      List<FlowDocument> flowDocuments =
          this.getFlowDocumentsByFlowId(flowTraceability.getId()).stream()
              .map(doc -> this.mapCompositionDocument(doc, payload.getDocuments(),
                  flowTraceability.getDepositMode(), payload.getServer()))
              .collect(Collectors.toList());
      this.saveAllFlowDocuments(flowDocuments);
      this.saveFlowTraceability(flowTraceability);
      log.info("Status of flow : {}", flowTraceability.getStatus());
      this.updateFlowTraceability(flowTraceability, baseUpdate, true, payload.getHtmlContent());
      // Create flow document reports.
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
              if (status == TransactionSynchronization.STATUS_COMMITTED) {
                createFlowDocumentReport(flowDocuments);
              }
            }
          });
    } catch (RuntimeException e) {
      log.error("", e);
      throw e;
    }
  }

  /**
   * Update createdAt field for flow traceability of sms and email.
   *
   * @param flowTraceability object of {@link FlowTraceability}
   */

  private void updateCreatedAtForEmailAndSms(FlowTraceability flowTraceability) {
    if (flowTraceability.getCreatedAt() == null) {
      flowTraceability.setCreatedAt(new Date());
    } else {
      if (flowTraceability.getStatus()
          .equalsIgnoreCase(FlowTraceabilityStatusConstant.SCHEDULED)
          || flowTraceability.getStatus()
          .equalsIgnoreCase(FlowTraceabilityStatusConstant.IN_PROCESS)) {
        flowTraceability.setCreatedAt(new Date());
      }
    }
  }

  /**
   * Map object of {@code CompositionDocument}s {@link Document} to {@link FlowDocument}
   *
   * @param document  refer to object of {@link FlowDocument}
   * @param documents refer to collection of {@link Document}
   * @return object of {@link FlowDocument}
   */
  private FlowDocument mapCompositionDocument(FlowDocument document, List<Document> documents,
      String depositMode, String server) {
    if (log.isDebugEnabled()) {
      log.debug("Flow document data: {}", document);
      log.debug("Consume document: {}", documents);
    }

    if (document == null) {
      document = new FlowDocument();
    }
    final FlowDocument flowDoc = document;
    documents.stream()
        .filter(doc -> doc != null && doc.getDocId().equalsIgnoreCase(flowDoc.getFileId()))
        .findFirst()
        .ifPresent(
            value -> {
              flowDoc.setFileSize(value.getSize());

              var detail = flowDoc.getDetail();
              if (!FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(depositMode)) {
                if (StringUtils.isEmpty(detail.getDocName())) {
                  detail.setDocName(value.getDocName());
                }
                flowDoc.setStatus(value.getStatus());
                var history = flowDoc.getFlowDocumentHistories().stream()
                    .filter(his -> his.getEvent().equals(
                        FlowDocumentStatus.valueOfLabel(value.getStatus()).getValue())).findFirst()
                    .orElse(new FlowDocumentHistory(flowDoc, server));
                flowDoc.addFlowDocumentHistory(history);
              } else {
                detail.setDocName(
                    value.getDocId().concat(".").concat(FlowTreatmentConstants.EMAIL_EXTENSION));
                flowDoc.setStatus(value.getStatus());
              }
              flowDoc.setDetail(detail);
              flowDoc.setLastModifiedBy(value.getModifiedBy());
            });
    return flowDoc;
  }
}
