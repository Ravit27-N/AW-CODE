package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@code UpdateFlowTraceabilityStatusListener} - Perform consume event messages from the producer
 * of deposit flow to update flow traceability status.
 *
 * @see KafkaUtils#UPDATE_FLOW_STATUS_BY_FILE_ID_TOPIC
 */
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
@Retryable(
    value = StatusNotInOrderException.class,
    maxAttempts = 10,
    backoff = @Backoff(delay = 300)
)
@Slf4j
@Component("updateFlowTraceabilityStatusByFileId")
public class UpdateFlowTraceabilityStatusListener
    extends AbstractFlowTraceabilityConsumer<BaseUpdateFlowFromProcessCtrl> {

  @Override
  public void accept(BaseUpdateFlowFromProcessCtrl payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_STATUS_BY_FILE_ID >>");
      log.info("BaseUpdateFlowFromProcessCtrl: {}", payload);

      this.getFlowByFileId(payload.getFileId()).ifPresent(flow -> {

        int currentStep = 5;
        this.validateBatchStatusOrder(flow, List.of(currentStep - 1, currentStep));
        if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
            flow.getDepositMode())) {
          var flowDetails = flow.getFlowTraceabilityDetails();
          flowDetails.setStep(currentStep);
        }

        if (!payload.getStatus().equals(FlowTraceabilityStatus.VALIDATED.getValue())) {
          if (payload.getStatus().equalsIgnoreCase(FlowTraceabilityStatusConstant.SCHEDULED)) {
            flow.setDateStatus(payload.getDateSchedule());
          } else {
            if (flow.getDateStatus() == null) {
              flow.setDateStatus(new Date());
            }
          }
          flow.setStatus(
              FlowTraceabilityStatus.valueOfLabel(payload.getStatus()).getValue());
        }

        // update flow documents to error when flow status is errored
        if (FlowTraceabilityStatusConstant.IN_ERROR.equalsIgnoreCase(payload.getStatus())) {
          List<String> unableToUpdateStatus =
              List.of(
                  FlowTraceabilityStatusConstant.IN_ERROR,
                  FlowTraceabilityStatusConstant.REFUSE_DOC,
                  FlowTraceabilityStatusConstant.CANCELED,
                  FlowTraceabilityStatusConstant.IN_PROGRESS,
                  FlowTraceabilityStatusConstant.COMPLETED);

          final List<FlowDocument> flowDocuments =
              this.getFlowDocumentRepository()
                  .findAllByFlowTraceabilityIdAndStatusNotIn(
                      flow.getId(), unableToUpdateStatus);
          UpdateFlowStatusModel updateFlowStatusModel = new UpdateFlowStatusModel();
          updateFlowStatusModel.setCreatedBy(payload.getCreatedBy());
          updateFlowStatusModel.setServer(payload.getServer());
          flowDocuments.forEach(
              flowDocument -> {
                flowDocument.setStatus(FlowDocumentStatus.IN_ERROR.getValue());
                flowDocument.setLastModifiedBy(payload.getCreatedBy());
                flowDocument.setLastModified(new Date());
                flowDocument.setDateStatus(new Date());
                updateFlowDocumentHistory(updateFlowStatusModel, flowDocument, payload.getStatus());
              });
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

        flow.setLastModifiedBy(payload.getCreatedBy());

        // update flow traceability
        this.updateFlowTraceability(flow, payload, true);
      });
    } catch (StatusNotInOrderException e) {
      // to allow retryable when meet this exception type
      throw e;
    } catch (RuntimeException e) {
      log.error("Failed to update Flow by fileId", e);
    }
  }
}
