package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.service.ReportingService;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.ms3.util.Channel;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowTraceabilityReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.IdentificationUpdateFlowModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

/**
 * {@code UpdateFlowAfterIdentificationStepListener} - Perform consume event messages from the
 * producer of deposit flow after finished {@code Identification} step to update flow traceability.
 */
@Slf4j
@RequiredArgsConstructor
@Component("updateFlowAfterIdentificationStep")
public class UpdateFlowAfterIdentificationStepListener
    extends AbstractFlowTraceabilityConsumer<IdentificationUpdateFlowModel> {

  private final ReportingService reportingService;

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(value = RuntimeException.class, maxAttempts = 10, backoff = @Backoff(delay = 300))
  @Override
  public void accept(IdentificationUpdateFlowModel payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_AFTER_IDENTIFICATION_STEP >>");
      var flowTraceability = this.getFlowTraceabilityByFileId(payload.getFileId());
      final String portalDepositType = payload.getDepositType();
      if (!StringUtils.isEmpty(portalDepositType)
          && !FlowTreatmentConstants.PORTAL_DEPOSIT.equals(portalDepositType)) {
        flowTraceability.setStatus(payload.getStatus());
      }
      FlowDocumentSubChannel flowDocumentSubChannel =
          FlowDocumentSubChannel.valueOfLabel(payload.getSubChannel());
      if (Objects.nonNull(flowDocumentSubChannel)) {
        flowTraceability.setSubChannel(flowDocumentSubChannel.getValue());
      } else {
        flowTraceability.setSubChannel(payload.getSubChannel());
      }
      flowTraceability.setLastModifiedBy(payload.getCreatedBy());
      flowTraceability.setChannel(payload.getChannel());
      var baseUpdate = mapping(payload, BaseUpdateFlowFromProcessCtrl.class);
      this.updateFlowTraceability(flowTraceability, baseUpdate, false);

      // add campaign batch details from version 4.0 (BatchTreatment_ENI_V4.0.drawio)
      // from this version deposit batch details use campaign details instead of create new table
      if (StringUtils.isNotEmpty(portalDepositType)
          && portalDepositType.equalsIgnoreCase(FlowTreatmentConstants.BATCH_DEPOSIT)) {
        updateCampaignBatchDetails(flowTraceability);

        var flowDetails = flowTraceability.getFlowTraceabilityDetails();
        flowDetails.setStep(2);
      }

      // Create flow traceability report (after flow have channel & sub-channel).
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
              if (status == TransactionSynchronization.STATUS_COMMITTED) {
                CreateFlowTraceabilityReportModel flowTraceabilityReportModel = CreateFlowTraceabilityReportModel.builder()
                    .flowId(flowTraceability.getId())
                    .depositDate(flowTraceability.getDepositDate())
                    .depositMode(flowTraceability.getDepositMode())
                    .channel(flowTraceability.getChannel())
                    .subChannel(flowTraceability.getSubChannel())
                    .ownerId(flowTraceability.getOwnerId())
                    .build();
                reportingService.createFlowTraceabilityReport(flowTraceabilityReportModel);
              }
            }
          });
    } catch (RuntimeException e) {
      log.error("", e);
      throw e;
    }
  }

  /**
   * Update flowCampaignBatchDetails.
   */
  private void updateCampaignBatchDetails(FlowTraceability flowTraceability) {
    final FlowCampaignDetail flowCampaignDetail =
        this.getFlowCampaignDetailByFlowId(flowTraceability.getId())
            .orElse(initializeCampaignBatchDetails(flowTraceability));
    flowCampaignDetail.setCampaignType(flowTraceability.getDepositMode().toUpperCase());
    flowCampaignDetail.setLastModifiedBy(flowTraceability.getLastModifiedBy());
    this.saveCampaignBatchDetails(flowCampaignDetail);
  }


}
