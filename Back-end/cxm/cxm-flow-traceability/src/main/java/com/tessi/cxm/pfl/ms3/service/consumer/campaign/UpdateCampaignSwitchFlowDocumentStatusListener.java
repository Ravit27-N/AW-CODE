package com.tessi.cxm.pfl.ms3.service.consumer.campaign;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatusConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

/**
 * To consume message the topic below.
 *
 * @see com.tessi.cxm.pfl.shared.utils.KafkaUtils#CAMPAIGN_FLOW_DOCUMENT_STATUS_CHANGE_TOPIC
 */
@Component("updateCampaignFlowDocumentStatus")
@Slf4j
public class UpdateCampaignSwitchFlowDocumentStatusListener
    extends AbstractFlowTraceabilityConsumer<UpdateFlowStatusModel> {

  /**
   * Update switch campaignSms status.
   *
   * @param updateFlowStatusPayload payload of flow
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  @Retryable(
      value = StatusNotInOrderException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 300))
  @Override
  public void accept(UpdateFlowStatusModel updateFlowStatusPayload) {
    try {
      log.info("<<UPDATE_CAMPAIGN_FLOW_DOCUMENT_STATUS : {}>>", updateFlowStatusPayload);
      updateFlowDocument(updateFlowStatusPayload);
    } catch (StatusNotInOrderException e) {
      log.error("Failed to update Flow document as status is not in order.", e);
      throw e;
    } catch (Exception ex){
      log.error("Failed to update Flow document.", ex);
    }
  }

  private void updateFlowDocument(@Payload UpdateFlowStatusModel flowStatusModel) {
    Optional<FlowTraceability> flowTraceabilityByFileId =
        this.getFlowTraceabilityRepository().findByFileId(flowStatusModel.getFileId());
    if (flowTraceabilityByFileId.isPresent()) {
      var flow = flowTraceabilityByFileId.get();
      int currentStep = 6;
      this.validateBatchStatusOrder(flow, List.of(currentStep - 1, currentStep));

      var flowDocuments =
          this.getFlowDocumentRepository().getFlowDocuments(flow.getId()).stream()
              .map(doc -> this.mapDocument(doc, flowStatusModel))
              .collect(Collectors.toList());
      final Map<String, List<FlowDocument>> statusMap =
          flowDocuments.stream()
              .filter(
                  flowDocument ->
                      flowStatusModel.getDocuments().stream()
                          .anyMatch(
                              switchDocumentModel ->
                                  switchDocumentModel
                                      .getHubIdDoc()
                                      .equals(flowDocument.getHubIdDoc())
                                      || switchDocumentModel
                                      .getDocUuid()
                                      .equals(flowDocument.getFileId())))
              .collect(Collectors.groupingBy(doc -> doc.getStatus().toLowerCase()));

      final int docErrorsCount =
          statusMap
              .getOrDefault(FlowDocumentStatus.IN_ERROR.getValue().toLowerCase(), List.of())
              .size();
      this.getFlowDocumentRepository().saveAll(flowDocuments);
      if (flowDocuments.size() == docErrorsCount) {
        flow.setStatus(FlowTraceabilityStatus.IN_ERROR.getValue());
      }
      this.saveFlowTraceability(flow);
      if (flowStatusModel.getDocuments().stream()
          .anyMatch(doc -> doc.getStatus().equalsIgnoreCase(FlowDocumentStatusConstant.IN_ERROR))) {
        this.updateFlowTraceabilityStatus(flow, flowStatusModel.getServer());
      }
      // Update flow documents status report.
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
              if (status == TransactionSynchronization.STATUS_COMMITTED) {
                updateFlowDocumentStatusReports(flowDocuments);
              }
            }
          });
    } else {
      throw new FlowTraceabilityNotFoundException(
          "FlowTraceability with fileId is not found: " + flowStatusModel.getFileId() + ".");
    }
  }

  private FlowDocument mapDocument(
      FlowDocument flowDocument, UpdateFlowStatusModel flowStatusModel) {
    log.info("Consume flow from switch of campaign: {}", flowStatusModel.getDocuments());
    flowStatusModel.getDocuments().stream()
        .filter(
            doc ->
                doc.getHubIdDoc().equals(flowDocument.getHubIdDoc())
                    || doc.getDocUuid().equals(flowDocument.getFileId()))
        .findFirst()
        .ifPresent(
            value -> {
              log.info("Consume flow from switch of campaign: {}", value.getDocUuid());
              flowDocument.setStatus(FlowDocumentStatus.valueOfLabel(value.getStatus()).getValue());
              if (flowDocument.getDateStatus() == null) {
                flowDocument.setDateStatus(new Date());
              }
              var detail = flowDocument.getDetail();
              if (!StringUtils.hasText(detail.getDocName())) {
                detail.setDocName(value.getDocName());
              }
              flowDocument.setDetail(detail);
              if (StringUtils.hasText(value.getHubIdDoc())) {
                flowDocument.setHubIdDoc(value.getHubIdDoc());
              }
              this.updateProgressDocument(flowDocument.getStatus(),
                  flowDocument.getFlowTraceability().getId());
              this.updateFlowDocumentHistory(flowStatusModel, flowDocument, value.getStatus());
            });
    return flowDocument;
  }
}
