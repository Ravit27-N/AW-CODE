package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateDepositFlowStep;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@code UpdateFlowTraceabilityStepListener} - Perform consume event messages from the producer of
 * deposit flow to update step when the user leave from deposit flow.
 */
@Slf4j
@Component("updateFlowTraceabilityDetailByDepositFlowStep")
public class UpdateFlowTraceabilityStepListener
    extends AbstractFlowTraceabilityConsumer<UpdateDepositFlowStep> {

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(value = RuntimeException.class, maxAttempts = 10, backoff = @Backoff(delay = 300))
  @Override
  public void accept(UpdateDepositFlowStep payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_STEP_WHEN_LEAVE_DEPOSIT >>");

      if (payload.getUuid().isBlank() || payload.getUuid().equals("undefined")) {
        return;
      }
      FlowTraceability flowTraceability = this.getFlowTraceabilityByFileId(payload.getUuid());
      // handle flow traceability when status error. nothing to do below step.
      if (flowTraceability.getStatus().equals(FlowTraceabilityStatus.IN_ERROR.getValue())) {
        return;
      }

      if (!payload.getStatus()
          .equalsIgnoreCase(FlowTraceabilityStatus.TO_FINALIZE.getFlowHistoryStatus())) {
        flowTraceability.setStatus(payload.getStatus());
        // create or update flow history.
        this.createOrUpdateFlowHistory(flowTraceability, payload.getStatus());
      }

      // Flow traceability detail.
      var flowDetails = flowTraceability.getFlowTraceabilityDetails();
      flowDetails.setComposedId(payload.getComposedId());
      flowDetails.setStep(payload.getStep());
      flowDetails.setValidation(payload.isValidation());

      // Add flow traceability detail to flow traceability.
      flowTraceability.addFlowTraceabilityDetails(flowDetails);

      // Update flow traceability, create flow history and update flow traceability details.
      this.saveFlowTraceability(flowTraceability);
      if (flowDetails
          .getPortalDepositType()
          .equalsIgnoreCase(FlowTreatmentConstants.PORTAL_PDF)) {
        // update flowDeposit
        this.findFlowDeposit(flowTraceability.getId())
            .ifPresent(
                flowDeposit -> {
                  flowDeposit.setComposedFileId(payload.getComposedId());
                  flowDeposit.setStep(payload.getStep());
                  flowDeposit.setLastModified(new Date());
                  this.saveFlowDeposit(flowDeposit);
                });
      }
    } catch (RuntimeException exception) {
      log.error("Failed to update Flow.", exception);
      throw exception;
    }
  }

  /**
   * Create or update flow history by validate id and status of flow traceability.
   *
   * @param flowTraceability refer to object reference of {@link FlowTraceability}.
   * @param status           refer to flow status.
   */
  private void createOrUpdateFlowHistory(FlowTraceability flowTraceability, String status) {
    var isExist =
        this.getFlowTraceabilityRepository()
            .existsByHistoryStatus(flowTraceability.getId(), flowTraceability.getStatus());

    var flowTraceabilityStatus = FlowTraceabilityStatus.valueOfLabel(status);

    if (!isExist) {
      flowTraceability.setDateStatus(new Date());
      // Update flow history.
      FlowHistory flowHistory =
          new FlowHistory(
              ComputerSystemProduct.getDeviceId(),
              flowTraceabilityStatus.getFlowHistoryStatus(),
              flowTraceability.getCreatedBy(),
              flowTraceability.getDateStatus());

      flowTraceability.addFlowHistory(flowHistory);
    } else {
      flowTraceability.getFlowHistories().stream()
          .filter(
              history -> history.getEvent().equals(flowTraceabilityStatus.getFlowHistoryStatus()))
          .forEach(
              flowHistory -> {
                var datetime = new Date();
                flowHistory.setDateTime(datetime);
                flowTraceability.setDateStatus(datetime);
              });
    }
  }
}
