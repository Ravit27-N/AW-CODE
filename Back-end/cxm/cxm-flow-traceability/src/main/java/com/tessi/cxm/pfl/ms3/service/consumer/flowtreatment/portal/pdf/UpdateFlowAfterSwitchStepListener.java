package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityValidationDetails;
import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityValidationService;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.ms3.util.DepositMode;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.SwitchDocumentModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatusConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * {@code UpdateFlowAfterSwitchStepListener} - Perform consume event messages from the producer of
 * deposit flow after finished {@code Switch} step to update flow traceability.
 *
 * @see KafkaUtils#UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC
 */
@Slf4j
@Component("updateFlowTraceabilityAfterSwitchStep")
@RequiredArgsConstructor
public class UpdateFlowAfterSwitchStepListener
    extends AbstractFlowTraceabilityConsumer<UpdateFlowStatusModel> {

  private final FlowTraceabilityValidationService flowValidationService;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void accept(UpdateFlowStatusModel payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_AFTER_SWITCH_STEP >>");
      var flowTraceability = this.getFlowTraceabilityByFileId(payload.getFileId());

      // check status when validated to prevent the action of removing status 'To finalize'
      // when receive the message
      log.info("Flow status : {}", payload.getStatus());
      Date currentDate = new Date();

      flowTraceability.setLastModifiedBy(payload.getCreatedBy());
      flowTraceability.setDateStatus(currentDate);

      List<String> portalDepositModes = List.of(DepositMode.PORTAL.getValue(),
          DepositMode.VIRTUAL_PRINTER.getValue());
      var isPortalDeposit = portalDepositModes.stream()
          .anyMatch(
              portalDepositMode ->
                  portalDepositMode.equalsIgnoreCase(flowTraceability.getDepositMode()));
      if (isPortalDeposit && FlowTraceabilityStatus.IN_PROCESS.getValue()
          .equalsIgnoreCase(payload.getStatus())) {
        flowTraceability.setUnloadingDate(currentDate);
      }

      if (Arrays.asList(
              FlowTraceabilityStatus.TO_VALIDATE.getValue(),
              FlowTraceabilityStatus.IN_PROCESS.getValue(),
              FlowTraceabilityStatus.SCHEDULED.getValue())
          .contains(payload.getStatus())) {
        this.updateFlowDeposit(flowTraceability.getId());
      }

      switch (FlowTraceabilityStatus.valueOfLabel(payload.getStatus())) {
        case TO_VALIDATE:
          flowTraceability.setCreatedAt(currentDate);
          this.toValidateFlow(flowTraceability, payload);
          break;
        case VALIDATED:
          flowTraceability.setDateStatus(flowTraceability.getCreatedAt());
          this.validateFlow(flowTraceability, payload);
          break;
        case SCHEDULED:
          if (flowTraceability.getStatus()
              .equalsIgnoreCase(FlowTraceabilityStatusConstant.TREATMENT)) {
            flowTraceability.setCreatedAt(currentDate);
          }
          this.scheduleFlow(flowTraceability, payload);
          break;
        default:
          this.launchFlow(flowTraceability, payload, isPortalDeposit);
      }
    } catch (RuntimeException e) {
      log.error("", e);
    }
  }


  /**
   * To create or update document history by validate history status.
   *
   * @param server   refer to server name.
   * @param document refer to object {@link FlowDocument}.
   */
  private void createOrUpdateDocumentHistory(
      String server, FlowDocument document, String createdBy) {
    var history =
        document.getFlowDocumentHistories().stream()
            .filter(h -> document.getStatus().equals(h.getEvent()))
            .findFirst();

    AtomicReference<Date> dateTime = new AtomicReference<>(document.getDateStatus());
    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(document.getChannel())
        && document.getStatus().equalsIgnoreCase(FlowTraceabilityStatus.SCHEDULED.getValue())) {
      dateTime.set(new Date());
    }

    history.ifPresent(
        item -> {
          if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(document.getChannel())) {
            item.setDateTime(dateTime.get());
            item.setCreatedBy(createdBy);
          }
          document.addFlowDocumentHistory(item);
        });
    boolean alreadyPlanified =
        document.getFlowDocumentHistories().stream()
            .anyMatch(
                docHistory ->
                    FlowDocumentStatus.SCHEDULED
                        .getValue()
                        .equalsIgnoreCase(docHistory.getEvent()));

    // create document history when history is not existing.
    if (history.isEmpty() || alreadyPlanified) {
      document.setStatus(
          alreadyPlanified ? FlowDocumentStatus.IN_PROGRESS.getValue() : document.getStatus());
      // Create flow document history.
      var flowDocumentHistory =
          new FlowDocumentHistory(
              server, document.getStatus(), createdBy, createdBy, dateTime.get());
      // Add flow document history to document.
      document.addFlowDocumentHistory(flowDocumentHistory);
    }
  }

  private void updateFlowDeposit(long flowId) {
    this.findFlowDeposit(flowId)
        .ifPresent(
            value -> {
              value.setStatus(FlowTraceabilityStatusConstant.FINALIZED);
              this.saveFlowDeposit(value);
            });
  }

  private void toValidateFlow(FlowTraceability flow, UpdateFlowStatusModel payload) {
    final int pageError = flow.getFlowTraceabilityDetails().getPageError();
    final int pageProcessed = flow.getFlowTraceabilityDetails().getPageProcessed();
    FlowTraceabilityValidationDetails flowTraceabilityValidationDetails =
        flow.getFlowTraceabilityValidationDetails();
    flowTraceabilityValidationDetails.setTotalDocument(pageProcessed);
    flowTraceabilityValidationDetails.setTotalRemaining((long) pageProcessed - pageError);
    flow.setFlowTraceabilityValidationDetails(flowTraceabilityValidationDetails);
    flow.setStatus(FlowTraceabilityStatus.valueOfLabel(payload.getStatus()).getValue());
    this.updateFlowTraceability(flow, mapping(payload, BaseUpdateFlowFromProcessCtrl.class), true);
    this.updateDocumentsStatus(flow, payload);
  }

  /**
   * To validate a flow or documents of a flow.
   *
   * @param flow    refers to the object of {@link FlowTraceability}
   * @param payload refers to th
   */
  private void validateFlow(FlowTraceability flow, UpdateFlowStatusModel payload) {
    var documents = this.getFlowDocumentsByStatus(flow.getId(), FlowDocumentStatus.TO_VALIDATE)
        .stream()
        .map(doc -> this.mapFlowDocumentStatus(doc, FlowDocumentStatus.VALIDATED, payload))
        .collect(Collectors.toList());
    this.calculateTotalRemainingDocs(flow, payload);
    this.saveAllFlowDocuments(documents);

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_COMMITTED) {
              UpdateFlowAfterSwitchStepListener.this.flowValidationService.updateFlowStatusAfterValidation(
                  flow.getId(), payload.getCreatedBy(), "", true);

              // Update flow document status report.
              TransactionSynchronizationManager.registerSynchronization(
                  new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                      if (status == TransactionSynchronization.STATUS_COMMITTED) {
                        updateFlowDocumentStatusReports(documents);
                      }
                    }
                  });
            }
          }
        });
  }

  /**
   * To calculate the validated documents and remaining documents of a flow in validation
   * processing.
   *
   * @param flow    refers to the object of {@link FlowTraceability}
   * @param payload refers to the object of {@link UpdateFlowStatusModel}
   * @see UpdateFlowAfterSwitchStepListener#validateFlow(FlowTraceability, UpdateFlowStatusModel)
   */
  private void calculateTotalRemainingDocs(FlowTraceability flow, UpdateFlowStatusModel payload) {
    FlowTraceabilityValidationDetails validationDetails =
        flow.getFlowTraceabilityValidationDetails();
    if (payload.isValidateDocument()) {
      long numOfDocs = payload.getDocuments().stream()
          .filter(doc -> doc.getStatus().equalsIgnoreCase(FlowDocumentStatus.VALIDATED.getValue()))
          .count();
      final long totalValidation =
          validationDetails.getTotalDocumentValidation() + numOfDocs;
      validationDetails.setTotalDocumentValidation(totalValidation);
      final long totalRemaining =
          validationDetails.getTotalRemaining() - numOfDocs;
      validationDetails.setTotalRemaining(totalRemaining);
    } else {
      validationDetails.setTotalDocumentValidation(
          validationDetails.getTotalDocumentValidation() + validationDetails.getTotalRemaining());
      validationDetails.setTotalRemaining(0);
    }
    flow.setFlowTraceabilityValidationDetails(validationDetails);
    this.saveFlowTraceability(flow);
  }

  private FlowDocument mapFlowDocumentStatus(FlowDocument document, FlowDocumentStatus status,
      UpdateFlowStatusModel payload) {
    payload.getDocuments().stream()
        .filter(doc -> doc.getDocUuid().equalsIgnoreCase(document.getFileId())).findFirst()
        .ifPresent(value -> {
          var detail = document.getDetail();
          detail.setImpression(value.getImpression());
          detail.setDocName(value.getDocName());
          document.setDetail(detail);
          document.setFileSize(value.getSize());
          document.setSheetNumber(value.getNbSheets());
          if (!document.getStatus().equalsIgnoreCase(FlowDocumentStatusConstant.VALIDATED)
              && !document.getStatus().equals(FlowDocumentStatusConstant.IN_PROGRESS)) {
            document.setCreatedAt(new Date());
          }
          if (!StringUtils.hasText(document.getStatus()) || !status.getValue()
              .equalsIgnoreCase(document.getStatus())) {
            document.setStatus(status.getValue());
            document.setCreatedBy(payload.getCreatedBy());
          } else {
            document.setLastModifiedBy(payload.getCreatedBy());
          }
          document.setDateStatus(new Date());
          var history = document.getFlowDocumentHistories().stream()
              .filter(his -> his.getEvent().equals(status.getValue())).findFirst()
              .orElse(new FlowDocumentHistory(document, payload.getServer()));
          document.addFlowDocumentHistory(history);
        });
    return document;
  }

  private List<FlowDocument> getFlowDocumentsByStatus(long flowId, FlowDocumentStatus status) {
    return this.getFlowDocumentsByFlowId(flowId).stream()
        .filter(doc -> doc.getStatus().equalsIgnoreCase(status.getValue()))
        .collect(Collectors.toList());
  }

  private void scheduleFlow(FlowTraceability flow, UpdateFlowStatusModel payload) {
    var documents = new ArrayList<FlowDocument>();
    var validatedDocs = this.getFlowDocumentsByStatus(flow.getId(), FlowDocumentStatus.VALIDATED);
    // Condition of scheduling documents of a flow
    if (!validatedDocs.isEmpty()) {
      documents.addAll(validatedDocs
          .stream()
          .map(doc -> this.mapFlowDocumentStatus(doc, FlowDocumentStatus.SCHEDULED, payload))
          .collect(Collectors.toList()));
    } else {
      // Condition of setting flow and its documents schedule without validation
      documents.addAll(this.getFlowDocumentsByFlowId(flow.getId()).stream()
          .filter(doc -> !doc.getStatus().equalsIgnoreCase(
              FlowDocumentStatusConstant.IN_ERROR) && !StringUtils.hasText(doc.getStatus())).map(
              doc -> this.mapFlowDocumentStatus(doc, FlowDocumentStatus.SCHEDULED,
                  payload)).collect(Collectors.toList()));
      this.updateDocumentErrorFileSize(flow, payload);
      this.updateFlowValidationDetails(flow, payload);
    }
    this.saveAllFlowDocuments(documents);
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            log.info("Transaction status: {}", status);
            if (status == TransactionSynchronization.STATUS_COMMITTED && validatedDocs.isEmpty()) {
              UpdateFlowAfterSwitchStepListener.this.flowValidationService
                  .updateFlowStatusAfterValidation(
                      flow.getId(),
                      payload.getCreatedBy(),
                      FlowTraceabilityStatus.SCHEDULED.getValue(),
                      false);
            }
            if (status == TransactionSynchronization.STATUS_COMMITTED) {
              updateFlowDocumentStatusReports(documents);
            }
          }
        });
  }

  private void updateDocumentErrorFileSize(FlowTraceability flow, UpdateFlowStatusModel payload) {
    var documents = new ArrayList<FlowDocument>();
    this.getFlowDocumentsByStatus(flow.getId(), FlowDocumentStatus.IN_ERROR)
        .forEach(doc -> {
          payload.getDocuments().stream().filter(dto -> dto.getDocUuid().equals(doc.getFileId()))
              .findFirst().ifPresent(value -> doc.setFileSize(value.getSize()));
          documents.add(doc);
        });
    this.saveAllFlowDocuments(documents);
    // Update flow document status report.
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_COMMITTED) {
              updateFlowDocumentStatusReports(documents);
            }
          }
        });
  }

  private void updateDocumentsStatus(FlowTraceability flow, UpdateFlowStatusModel payload) {
    List<FlowDocument> flowDocuments = this.getFlowDocumentsByFlowId(flow.getId());

    var docIds = payload.getDocuments().stream().map(SwitchDocumentModel::getDocUuid)
        .collect(Collectors.toList());

    // Update document from portal deposit document flow
    var results =
        flowDocuments.stream()
            .filter(
                doc -> !doc.getStatus().equalsIgnoreCase(FlowDocumentStatus.REFUSE.getValue())
                    && docIds.contains(doc.getFileId()))
            .map(
                doc ->
                    this.mapSwitchDocument(
                        doc,
                        payload.getDocuments(),
                        payload.getServer(),
                        payload.getStatus(),
                        payload.getCreatedBy(),
                        new Date(),
                        payload.getUnloadingDate()))
            .collect(Collectors.toList());
    this.saveAllFlowDocuments(results);
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

  private void launchFlow(FlowTraceability flow, UpdateFlowStatusModel payload,
      boolean isPortalDeposit) {
    this.updateDocumentsStatus(flow, payload);
    if (Objects.nonNull(payload.getUnloadingDate())) {
      flow.setUnloadingDate(payload.getUnloadingDate());
    }

    this.saveFlowTraceability(flow);
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            log.info("Transaction status: {}", status);
            if (status == TransactionSynchronization.STATUS_COMMITTED && isPortalDeposit) {
              UpdateFlowAfterSwitchStepListener.this.flowValidationService.updateFlowStatusAfterValidation(
                  flow.getId(), payload.getCreatedBy(), "", false);
            }
          }
        });
  }

  /**
   * Map collection objects of {@link SwitchDocumentModel} to {@link FlowDocument}.
   *
   * @param document       refer to object {@link FlowDocument}.
   * @param documentModels refer to {@link List} of {@link SwitchDocumentModel}.
   * @param server         refer to name of server.
   * @return reference object of {@link FlowDocument}
   */
  private FlowDocument mapSwitchDocument(
      FlowDocument document,
      List<SwitchDocumentModel> documentModels,
      String server,
      String flowStatus,
      String createdBy,
      Date createdAt,
      Date unloadingDate) {
    documentModels.stream()
        .filter(doc -> doc.getDocUuid().equalsIgnoreCase(document.getFileId()))
        .findFirst()
        .ifPresent(mapFlowDocument(document, server, flowStatus, createdBy, createdAt,unloadingDate));
    return document;
  }

  /**
   * Map flow document and create or update document history.
   *
   * @param document refer to object {@link FlowDocument}.
   * @param server refer to server name.
   * @return object of {@link SwitchDocumentModel} wrapped by {@link Consumer}
   */
  private Consumer<SwitchDocumentModel> mapFlowDocument(
      FlowDocument document,
      String server,
      String flowStatus,
      String createdBy,
      Date createdAt,
      Date unloadingDate) {
    return value -> {
      document.setStatus(value.getStatus());
      document.setDateStatus(new Date());
      if (FlowDocumentStatusConstant.IN_PROGRESS.equalsIgnoreCase(document.getStatus())) {
        document.setUnloadingDate(unloadingDate);
      } else {
        document.setCreatedAt(createdAt);
      }
      document.setLastModifiedBy(value.getModifiedBy());
      document.setIdDoc(value.getIdDoc());
      document.setFileSize(value.getSize());

      FlowDocumentSubChannel flowDocumentSubChannel =
          FlowDocumentSubChannel.valueOfLabel(value.getSubChannel());
      if (!ObjectUtils.isEmpty(flowDocumentSubChannel)) {
        document.setSubChannel(flowDocumentSubChannel.getValue());
      }
      if (FlowDocumentStatus.IN_ERROR.getValue().equalsIgnoreCase(value.getStatus())) {
        document.setStatus(FlowDocumentStatus.IN_ERROR.getValue());
      } else {
        if (FlowDocumentStatus.TO_VALIDATE.getValue().equalsIgnoreCase(flowStatus)) {
          document.setStatus(FlowDocumentStatus.TO_VALIDATE.getValue());
        } else {
          document.setStatus(flowStatus);
        }
      }
      var dt = document.getDetail();
      dt.setDocName(value.getDocName());
      dt.setColor(value.getColor());
      dt.setArchiving(value.getArchiving());
      dt.setPostage(value.getPostage());
      dt.setEnvelope(value.getEnvelope());
      dt.setImpression(value.getImpression());
      document.setPageNumber(value.getNbPages());
      document.setSheetNumber(value.getNbSheets());
      document.setDetail(dt);
      // Add or update document history
      this.createOrUpdateDocumentHistory(server, document, createdBy);
    };
  }

  /**
   * To update information detail of validation flow when processing the flow and its documents
   * without the validation processing.
   *
   * @param flow    refers to the object of {@link FlowTraceability}
   * @param payload refers to the object of {@link  UpdateFlowStatusModel}
   * @see UpdateFlowAfterSwitchStepListener#scheduleFlow(FlowTraceability, UpdateFlowStatusModel)
   */
  private void updateFlowValidationDetails(FlowTraceability flow, UpdateFlowStatusModel payload) {
    FlowTraceabilityValidationDetails flowValidationDetails =
        flow.getFlowTraceabilityValidationDetails();
    final long totalValidation =
        flowValidationDetails.getTotalDocumentValidation() + payload.getDocuments().stream()
            .filter(
                doc -> doc.getStatus().equalsIgnoreCase(FlowDocumentStatus.SCHEDULED.getValue()))
            .count();
    flowValidationDetails.setTotalDocumentValidation(totalValidation);
    flow.setFlowTraceabilityValidationDetails(flowValidationDetails);
  }
}
