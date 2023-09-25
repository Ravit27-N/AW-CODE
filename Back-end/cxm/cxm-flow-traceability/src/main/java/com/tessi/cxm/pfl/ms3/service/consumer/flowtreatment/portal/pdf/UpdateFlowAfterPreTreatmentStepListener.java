package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.PreProcessingUpdateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * {@code UpdateFlowAfterPreTreatmentStepListener} - Perform consume event messages from the
 * producer of deposit flow after finished {@code PreTreatmentStep} step to update flow
 * traceability.
 */
@Slf4j
@Component("updateFlowTraceabilityAfterPreTreatmentStep")
public class UpdateFlowAfterPreTreatmentStepListener
    extends AbstractFlowTraceabilityConsumer<PreProcessingUpdateFlowTraceabilityModel> {

  @Transactional(rollbackFor = StatusNotInOrderException.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(value = RuntimeException.class, maxAttempts = 10, backoff = @Backoff(delay = 300))
  @Override
  public void accept(PreProcessingUpdateFlowTraceabilityModel payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_AFTER_PRE_TREATMENT_STEP >>");

      // Process update flow traceability and flow history.
      var flowTraceability = this.getFlowTraceabilityByFileId(payload.getFileId());
      int currentStep = 3;
      this.validateBatchStatusOrder(flowTraceability, List.of(currentStep - 1));

      flowTraceability.setStatus(payload.getStatus());
      flowTraceability.setLastModifiedBy(payload.getCreatedBy());
      flowTraceability.setDateStatus(new Date());
      var flowDetails =
          getFlowTraceabilityDetailsById(flowTraceability.getId())
              .orElse(new FlowTraceabilityDetails());
      flowDetails.setPageCount(Integer.parseInt(payload.getNbPages()));
      flowDetails.setPageError(Integer.parseInt(payload.getNbDocumentsKo()));
      flowDetails.setPageProcessed(Integer.parseInt(payload.getNbDocuments()));
      flowDetails.setFlowTraceability(flowTraceability);
      flowDetails.setStep(currentStep);
      flowTraceability.addFlowTraceabilityDetails(flowDetails);

      // Set flow history.
      var baseUpdate = mapping(payload, BaseUpdateFlowFromProcessCtrl.class);
      // Update flow traceability
      this.updateFlowTraceability(flowTraceability, baseUpdate, true);

      // Process list of document.
      processFlowDocuments(payload, flowTraceability);
    } catch (RuntimeException e) {
      log.error("", e);
      throw e;
    }
  }

  /**
   * To process flow documents create or update (flow documents, flow history, flow traceability)
   *
   * @param payload                  refer to object of
   *                                 {@link PreProcessingUpdateFlowTraceabilityModel}
   * @param flowTraceabilityResponse refer to object of {@link FlowTraceability}
   */
  private void processFlowDocuments(
      PreProcessingUpdateFlowTraceabilityModel payload, FlowTraceability flowTraceabilityResponse) {
    payload.getFlowDocuments().stream()
        .collect(
            HashMap<Integer, FileFlowDocument>::new,
            (hashMap, document) -> hashMap.put(hashMap.size(), document),
            (nonHashMap, nonHashMap2) -> {
            })
        .forEach(
            (idx, doc) -> {
              // Flow document.
              FlowDocument flowDocument = new FlowDocument();

              String status = FlowDocumentStatus.IN_PROGRESS.getValue();
              if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
                  payload.getFlowType())) {
                status = null;
              }
              this.setFlowDocument(
                  flowDocument, doc, payload.getCreatedBy(), status, idx, doc.getEmailRecipient());

              // Set flow traceability to flow document.
              flowDocument.setFlowTraceability(flowTraceabilityResponse);

              // Process create flow document history.
              if (StringUtils.hasText(payload.getFlowType())
                  && !FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
                  payload.getFlowType())) {
                FlowDocumentHistory documentHistory = new FlowDocumentHistory();
                documentHistory.setServer(payload.getServer());
                documentHistory.setCreatedBy(payload.getCreatedBy());
                documentHistory.setLastModifiedBy(payload.getCreatedBy());
                documentHistory.setEvent(flowDocument.getStatus());
                flowDocument.addFlowDocumentHistory(documentHistory);
              }

              // Create flow document.
              var flowDocumentResponse = this.saveFlowDocument(flowDocument);
              // Process create flow document detail.
              FlowDocumentDetails flowDocumentDetail = flowDocumentResponse.getDetail();
              setFlowDocumentDetails(doc, flowDocumentDetail);
              flowDocumentDetail.setFlowDocument(flowDocumentResponse);

              // Create flow document detail
              this.saveFlowDocumentDetails(flowDocumentDetail);
            });
  }

  /**
   * Set values to field of {@link FlowDocumentDetails}
   *
   * @param document           refer to object of {@link FileFlowDocument}
   * @param flowDocumentDetail refer to object of {@link FlowDocumentDetails}
   */
  private void setFlowDocumentDetails(
      FileFlowDocument document, FlowDocumentDetails flowDocumentDetail) {
    var address = document.getAddress().values();
    flowDocumentDetail.setAddress(String.join(", ", address));
    flowDocumentDetail.setEmail(document.getEmailRecipient());
    flowDocumentDetail.setTelephone("");
    flowDocumentDetail.setReference("");
    flowDocumentDetail.setFillers(new String[]{});
    flowDocumentDetail.setArchiving(document.getProduction().getArchiving());
    flowDocumentDetail.setAddition(document.getProduction().getValidation());
    flowDocumentDetail.setColor(document.getProduction().getColor());
    flowDocumentDetail.setEnvelope("");
    flowDocumentDetail.setImpression("");
    flowDocumentDetail.setPostage("");
    flowDocumentDetail.setWatermark(document.getProduction().getWatermark());
    flowDocumentDetail.setPostalPickup("");
  }
}
