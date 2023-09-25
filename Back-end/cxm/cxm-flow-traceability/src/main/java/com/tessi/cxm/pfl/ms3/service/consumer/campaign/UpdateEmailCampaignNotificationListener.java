package com.tessi.cxm.pfl.ms3.service.consumer.campaign;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.EmailCampaignNotification;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.GenericStatusUtils;
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
 * Consume message to update <strong>EmailCampaignNotification</strong> from
 * <strong>Campaign</strong> after consume payload from <strong>HubDigitalFlow</strong>.
 *
 * @author Sokhour LACH
 * @author Piseth KHON
 * @author Sakal TUM
 * @see KafkaUtils#FLOW_EMAIL_CAMPAIGN_NOTIFICATION_TOPIC
 * @see #updateEmailCampaignNotification(EmailCampaignNotification)
 * @see #updateFlowCampaignStatusDone(FlowDocument)
 * @see #isEqualsIgnoreCase(FlowDocumentStatus, String)
 */
@Slf4j
@Component("updateEmailCampaignNotification")
public class UpdateEmailCampaignNotificationListener
    extends AbstractFlowTraceabilityConsumer<EmailCampaignNotification> {

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  @Retryable(value = StatusNotInOrderException.class, maxAttempts = 10, backoff = @Backoff(delay = 300))
  @Override
  public void accept(EmailCampaignNotification payload) {
    log.info("<< UPDATE_FLOW_EMAIL_CAMPAIGN_NOTIFICATION: {} >>", payload);
    try {
      this.updateEmailCampaignNotification(payload);
    } catch (RuntimeException e) {
      log.error(e.getMessage(), e);
      if (log.isDebugEnabled()) {
        log.debug(e.getMessage(), e);
      }
      throw e;
    }
  }

  /**
   * To update email campaign of flow document when retrieve notification campaign.
   *
   * @param payload refer to object of {@link EmailCampaignNotification}
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateEmailCampaignNotification(EmailCampaignNotification payload) {
    var flowDocumentByHubIdDoc = this.findFlowDocumentByHubIdDoc(payload.getUuid());
    if (flowDocumentByHubIdDoc.isPresent()) {
      var document = flowDocumentByHubIdDoc.get();
      var flowTraceability = this.findFlowTraceabilityById(document.getFlowTraceability().getId());
      var event = payload.getEventHistory();
      var statusNoneUpdateDocument =
          List.of(FlowDocumentStatus.IN_ERROR.getValue(), FlowDocumentStatus.COMPLETED.getValue());
      var eventNoneContinue =
          List.of(
              GenericStatusUtils.HARD_BOUNCE,
              GenericStatusUtils.SOFT_BOUNCE,
              GenericStatusUtils.BLOCKED);
      var postDeliverStatusList = List.of(GenericStatusUtils.OPENED, GenericStatusUtils.CLICKED);

      // nothing to do the process bellow when the document history have the status blocked and
      // bounce (Soft bounce, Hard bounce)
      if (this.existsByFlowDocumentIdAndEventIn(document.getId(), eventNoneContinue)) {
        // to stop the process to create document history.
        return;
      }

      // update date status of flow document.
      if (!postDeliverStatusList.contains(event)) {
        document.setDateStatus(new Date());
      }

      if (this.isEqualsIgnoreCase(FlowDocumentStatus.SENT, event)) {
        // update flow document.
        this.updateFlowCampaignStatusDone(document);
      } else if (!statusNoneUpdateDocument.contains(document.getStatus())) {
        // update the status of flow document.
        document.setStatus(payload.getStatus());
      }
      // create flow document history.
      this.createFlowDocumentHistory(payload, document);

      this.updateFlowTraceabilityStatus(flowTraceability, payload.getServer());

      // Update flow document status report.
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
              if (status == TransactionSynchronization.STATUS_COMMITTED) {
                updateFlowDocumentStatusReport(document);
              }
            }
          });
    } else {
      throw new FlowDocumentNotFoundException(
          "Flow document with hubIdDoc is not found: " + payload.getUuid() + ".");
    }
  }

  /**
   * Update flow document when the status done or complete.
   *
   * @param flowDocument refer to object of {@link FlowDocument}
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateFlowCampaignStatusDone(FlowDocument flowDocument) {
    if (!this.isEqualsIgnoreCase(FlowDocumentStatus.COMPLETED, flowDocument.getStatus())) {
      flowDocument.setStatus(FlowDocumentStatus.COMPLETED.getValue());
      // update total document delivered into flow campaign details.
      this.updateTotalDeliveredById(flowDocument.getFlowTraceability().getId(), 1);
    }
  }

  /**
   * To update event status of flow document.
   *
   * @param payload      refer to object of {@link EmailCampaignNotification}
   * @param flowDocument refer to object of {@link FlowDocument}
   */
  private void createFlowDocumentHistory(
      EmailCampaignNotification payload, FlowDocument flowDocument) {
    var event = payload.getEventHistory();
    var eventBounces = List.of(GenericStatusUtils.HARD_BOUNCE, GenericStatusUtils.SOFT_BOUNCE);

    if (!this.existsDocumentHistoryByDocumentIdAndEvent(flowDocument.getId(), event)) {
      var flowId = flowDocument.getFlowTraceability().getId();
      // update flow email campaign summary
      if (this.isEqualsIgnoreCase(FlowDocumentStatus.IN_ERROR, event)) {
        // update summary for status error
        this.updateTotalErrorById(flowId, 1);
      } else if (this.isEqualsIgnoreCase(FlowDocumentStatus.CLICKED, event)) {
        // update summary for status clicked
        this.updateTotalClickedById(flowId, 1);
      } else if (this.isEqualsIgnoreCase(FlowDocumentStatus.OPENED, event)) {
        // update summary for status opened
        this.updateTotalOpenedById(flowId, 1);
      } else if (this.isEqualsIgnoreCase(FlowDocumentStatus.RESENT, event)) {
        // update summary for status resent
        this.updateTotalResentById(flowId, 1);
      } else if (eventBounces.contains(event) && !this.existsByFlowDocumentIdAndEventIn(
          flowDocument.getId(), eventBounces)) {
        // update summary of flow campaign details for status bounce (hard bounce and soft bounce)
        if (isEqualsIgnoreCase(FlowDocumentStatus.HARD_BOUNCE, event)) {
          this.updateTotalPermanentErrorById(flowId, 1);
        } else {
          this.updateTotalTemporaryErrorById(flowId, 1);
        }
        // update total bounce
        this.updateTotalBounceById(flowId, 1);
      } else if (this.isEqualsIgnoreCase(FlowDocumentStatus.BLOCKED, event)) {
        this.updateTotalBlockedById(flowId, 1);
      }

      // create a new document history.
      var documentHistory = new FlowDocumentHistory(flowDocument, payload.getServer());
      if (FlowDocumentStatus.isAbleMapStatusToCompleted(payload.getStatus())) {
        flowDocument.setStatus(FlowDocumentStatus.COMPLETED.getValue());
        if (flowDocument.getDateStatus() == null) {
          flowDocument.setDateStatus(new Date());
        }
      }
      documentHistory.setFlowDocument(flowDocument);
      documentHistory.setEvent(event);
      documentHistory.setDateTime(payload.getDateStatus());
      this.saveFlowDocumentHistory(documentHistory);
    }
  }

  /**
   * To filter status of document with equals ignore case.
   *
   * @param documentStatus refer to Enum of {@link FlowDocumentStatus}
   * @param value          refer to event status of flow document.
   * @return true if existing.
   */
  private boolean isEqualsIgnoreCase(FlowDocumentStatus documentStatus, String value) {
    return documentStatus.getValue().equalsIgnoreCase(value);
  }
}
