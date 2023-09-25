package com.tessi.cxm.pfl.ms3.core.batch.listener;

import com.tessi.cxm.pfl.ms3.constant.DateConvertor;
import com.tessi.cxm.pfl.ms3.core.batch.reader.NotificationJpaReader;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.xml.Document;
import com.tessi.cxm.pfl.ms3.entity.xml.Job;
import com.tessi.cxm.pfl.ms3.service.ReportingService;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowDocumentStatusReportModel;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Listener class around the reading of an item.
 *
 * @author Piseth KHON
 */
@Component
@StepScope
@Slf4j
public class NotificationReaderListener implements ItemReadListener<Job> {

  private static final String SERVER_NAME;

  static {
    SERVER_NAME = ComputerSystemProduct.getDeviceId();
  }

  private ReportingService reportingService;
  private final NotificationJpaReader<FlowDocument> notificationJpaReader;
  private List<List<String>> queryMethodArguments;
  private int pageSize;

  public NotificationReaderListener(NotificationJpaReader<FlowDocument> notificationJpaReader) {
    this.notificationJpaReader = notificationJpaReader;
  }

  @Autowired
  public void setReportingService(ReportingService reportingService) {
    this.reportingService = reportingService;
  }

  /** Called before {@link ItemReader#read()}. */
  public void beforeRead() {
    // do nothing
  }

  /**
   * Called after {@link ItemReader#read()}. This method is called only for actual items (ie it is
   * not called when the reader returns null).
   */
  @Override
  public void afterRead(Job item) {
    var idDocs = item.getDocuments().stream().map(Document::getIdDoc).collect(Collectors.toList());
    this.pageSize = idDocs.size();
    this.queryMethodArguments = List.of(idDocs);
    final var flowDocuments = doRead();
    if (flowDocuments.isEmpty()) {
      log.error("The notification document XML does not a valid document.");
      item.getDocuments().clear();
    } else {
      flowDocuments.forEach(
          flowDocument -> {
            if (!CollectionUtils.isEmpty(flowDocument.getNotifications())) {
              removeNotificationIfExists(flowDocument, item);
            }
            setDocumentToNotification(item, flowDocument);
          });
    }
  }

  /**
   * Called if an error occurs while trying to read.
   *
   * @param ex thrown from {@link ItemReader}
   */
  @Override
  public void onReadError(Exception ex) {
    // do nothing
  }

  /**
   * Read next item from input.
   *
   * @return an item or {@code null} if the data source is exhausted
   */
  private List<FlowDocument> doRead() {
    notificationJpaReader.setArguments(queryMethodArguments);
    notificationJpaReader.setPageSize(pageSize);
    try {
      notificationJpaReader.readItem();
    } catch (Exception e) {
      throw new ItemStreamException("Could not read item from repository", e);
    }
    return notificationJpaReader.getFlowDocuments();
  }

  /**
   * Set document id to notification and update document it self.
   *
   * @param job refer to object that read from exm file
   * @param documentNotifications object of {@link FlowDocument}
   */
  private void setDocumentToNotification(Job job, FlowDocument documentNotifications) {
    job.getDocuments()
        .forEach(
            document -> {
              if (document.getIdDoc().equals(documentNotifications.getIdDoc())) {
                if (!FlowDocumentStatus.isAbleMapStatusToCompleted(
                    document.getNotification().getStep())) {
                  this.invalidDocumentStepName(document);
                  return;
                }
                document.setId(documentNotifications.getId());
                FlowDocumentStatus flowDocumentStatus =
                    FlowDocumentStatus.valueOfLabel(document.getNotification().getStep());
                documentNotifications.setStatus(flowDocumentStatus.getValue());
                documentNotifications.setDateStatus(
                    convertStringToDate(
                        document.getNotification().getDate())); // update sub-status date
                documentNotifications.addFlowDocumentHistory(
                    new FlowDocumentHistory(documentNotifications, SERVER_NAME)); // create history
                documentNotifications
                    .getDetail()
                    .setPostage(document.getNotification().getStampReal()); // update detail
                documentNotifications.setSubChannel(
                    this.mappingToSubChannel(document.getNotification().getStampReal()));

                documentNotifications.setStatus(FlowDocumentStatus.COMPLETED.getValue());

                UpdateFlowDocumentStatusReportModel flowUpdate =
                    UpdateFlowDocumentStatusReportModel.builder()
                        .documentId(documentNotifications.getId())
                        .status(FlowDocumentStatus.COMPLETED.getValue())
                        .dateStatus(documentNotifications.getDateStatus())
                        .subChannel(documentNotifications.getSubChannel())
                        .numReco(document.getNotification().getNumReco())
                        .build();
                reportingService.updateFlowDocumentStatusReport(flowUpdate);
                //
                if (StringUtils.hasText(document.getNotification().getAccuseReception())
                    && !StringUtils.hasText(documentNotifications.getRelatedItem())) {
                  documentNotifications.setRelatedItem("RELATED");
                }
              }
            });
  }

  /***
   * Log document invalid by step name are not already defined and null.
   * @param document reference to {@link Document} object.
   */
  private void invalidDocumentStepName(Document document) {
    String idDoc = document.getIdDoc();
    String step = document.getNotification().getStep();
    String errorMessage =
        StringUtils.hasLength(step)
            ? String.format(
                "IdDoc %s with step name \"%s\" does not exist in the configuration.", idDoc, step)
            : String.format("Step name of IdDoc \"%s\" is empty or null.", idDoc);
    log.error(errorMessage);
  }

  /**
   * Remove any document data from XML job integration if exists by {@code
   * com.tessi.cxm.pfl.ms3.entities.FlowDocument}'s idDoc and status.
   *
   * @param job refer to object that read from exm file
   * @param flowDocument Reference {@code com.tessi.cxm.pfl.ms3.entities.FlowDocument}.
   */
  private void removeNotificationIfExists(FlowDocument flowDocument, Job job) {
    job.getDocuments()
        .removeIf(
            document -> {
              boolean isExistStep =
                  flowDocument.getFlowDocumentHistories().stream()
                      .map(FlowDocumentHistory::getEvent)
                      .anyMatch(document.getNotification().getStep()::equalsIgnoreCase);
              return flowDocument.getIdDoc().equalsIgnoreCase(document.getIdDoc())
                  && (isExistStep
                      && (flowDocument
                              .getStatus()
                              .equalsIgnoreCase(FlowDocumentStatus.COMPLETED.getValue())
                          || flowDocument
                              .getStatus()
                              .equalsIgnoreCase(FlowDocumentStatus.IN_ERROR.getValue())));
            });
  }

  private String mappingToSubChannel(String stampReal) {
    if (!StringUtils.hasText(stampReal)) {
      return "";
    }
    if (stampReal.equalsIgnoreCase("R1 avec AR")) {
      return "Reco AR";
    }
    return stampReal;
  }

  private Date convertStringToDate(String stringDate) {
    return DateConvertor.parisTimeZoneToUTC(stringDate);
  }
}
