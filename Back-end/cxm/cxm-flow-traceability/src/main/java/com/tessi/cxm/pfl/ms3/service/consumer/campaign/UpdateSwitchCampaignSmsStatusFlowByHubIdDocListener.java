package com.tessi.cxm.pfl.ms3.service.consumer.campaign;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Consume message from <strong>Cxm-Campaign</strong> to update <strong>flow traceability</strong>
 * by {@code HubIdDoc}.
 *
 * @author Sokhor LACH
 * @author Piseth KHON
 * @see KafkaUtils#UPDATE_FLOW_DOCUMENT_STATUS_BY_HUB_ID_DOC_TOPIC
 * @see UpdateSwitchCampaignSmsStatusFlowByHubIdDocListener
 * @see AbstractFlowTraceabilityConsumer#findFlowDocumentByHubIdDoc(String)
 * @see AbstractFlowTraceabilityConsumer#updateFlowTraceabilityStatus(FlowTraceability, String)
 * @see AbstractFlowTraceabilityConsumer#findFlowTraceabilityById(long)
 * @see AbstractFlowTraceabilityConsumer#updateFlowDocumentHistory(UpdateFlowStatusModel,
 * FlowDocument, String)
 * @see AbstractFlowTraceabilityConsumer#updateProgressDocument(String, long)
 * @see AbstractFlowTraceabilityConsumer#saveFlowDocument(FlowDocument)
 */
@Component("updateCampaignSmsStatusByHubIdDoc")
@Slf4j
public class UpdateSwitchCampaignSmsStatusFlowByHubIdDocListener
    extends AbstractFlowTraceabilityConsumer<UpdateFlowStatusModel> {

  @Override
  public void accept(UpdateFlowStatusModel updateFlowStatusModel) {
    log.info("<<UPDATE_CAMPAIGN_SMS_STATUS_BY_HUB_ID_DOC : {}>>", updateFlowStatusModel);
    updateFlowStatusModel.getDocuments().stream()
        .findFirst()
        .ifPresent(
            document -> {
              log.info("Consume flow from switch of campaign to update status: {}", document);
              this.findFlowDocumentByHubIdDoc(document.getHubIdDoc())
                  .ifPresent(
                      flowDocument -> {
                        if (FlowDocumentStatus.isAbleMapStatusToCompleted(document.getStatus())) {
                          flowDocument.setStatus(FlowDocumentStatus.COMPLETED.getValue());
                        } else {
                          flowDocument.setStatus(document.getStatus());
                        }
                        flowDocument.setDateStatus(new Date());
                        this.updateFlowDocumentHistory(
                            updateFlowStatusModel, flowDocument, document.getStatus());
                        this.updateProgressDocument(
                            document.getStatus(), flowDocument.getFlowTraceability().getId());
                        this.saveFlowDocument(flowDocument);
                        // update flow traceability when all the document is completed.
                        var flowTraceability =
                            this.findFlowTraceabilityById(
                                flowDocument.getFlowTraceability().getId());
                        this.updateFlowTraceabilityStatus(
                            flowTraceability, updateFlowStatusModel.getServer());

                        // Update flow document status report.
                        TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronization() {
                              @Override
                              public void afterCompletion(int status) {
                                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                                  updateFlowDocumentStatusReport(flowDocument);
                                }
                              }
                            });
                      });
            });
  }
}
