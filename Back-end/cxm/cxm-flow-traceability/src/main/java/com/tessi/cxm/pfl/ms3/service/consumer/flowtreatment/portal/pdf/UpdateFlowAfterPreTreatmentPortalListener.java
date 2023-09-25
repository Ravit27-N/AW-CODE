package com.tessi.cxm.pfl.ms3.service.consumer.flowtreatment.portal.pdf;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentAttachment;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentBackground;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityValidationDetails;
import com.tessi.cxm.pfl.ms3.repository.FlowCampaignDetailRepository;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.ms3.util.Channel;
import com.tessi.cxm.pfl.ms3.util.DepositMode;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.model.kafka.PreProcessingUpdateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowTraceabilityReportModel;
import com.tessi.cxm.pfl.shared.utils.AttachmentPosition;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowHistoryStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@code UpdateFlowAfterPreTreatmentPortalListener} - Perform consume event messages from the
 * producer of deposit flow after finished {@code PreTreatmentPortal} step to update flow
 * traceability.
 *
 * @see KafkaUtils#UPDATE_FLOW_AFTER_PRE_TREATMENT_STEP_TOPIC_PORTAL
 */
@Slf4j
@Component("updateFlowAfterPreTreatmentPortalStep")
@RequiredArgsConstructor
public class UpdateFlowAfterPreTreatmentPortalListener
    extends AbstractFlowTraceabilityConsumer<PreProcessingUpdateFlowTraceabilityModel> {

  private final FlowCampaignDetailRepository flowCampaignDetailRepository;

  @Override
  public void accept(PreProcessingUpdateFlowTraceabilityModel payload) {
    try {
      log.info("<< UPDATE_FLOW_TRACEABILITY_AFTER_PRE_TREATMENT_PORTAL_STEP >>");

      // Process update flow traceability and flow history.
      var flowTraceability = this.getFlowTraceabilityByFileId(payload.getFileId());
      flowTraceability.setStatus(payload.getStatus());
      flowTraceability.setLastModifiedBy(payload.getCreatedBy());
      // Condition for PDF flow deposit
      if (org.springframework.util.StringUtils.hasText(payload.getSubChannel())) {
        flowTraceability.setSubChannel(payload.getSubChannel());
      }

      log.info("<< Flow document status: {} >>", payload.getStatus());

      // Check and update flow history when flow traceability's status is errored.
      this.updateFlowHistoryOnFlowError(flowTraceability, payload.getStatus());

      var flowDetails =
          this.getFlowTraceabilityDetailsById(flowTraceability.getId())
              .orElse(new FlowTraceabilityDetails());
      flowDetails.setPageCount(Integer.parseInt(payload.getNbPages()));
      flowDetails.setPageError(
          payload.getFlowDocuments().stream()
              .mapToInt(
                  doc ->
                      (!doc.getAnalyse().equalsIgnoreCase(HttpStatus.OK.getReasonPhrase())) ? 1 : 0)
              .sum());
      flowDetails.setPageProcessed(Integer.parseInt(payload.getNbDocuments()));
      flowTraceability.addFlowTraceabilityDetails(flowDetails);

      // update flowValidation details
      FlowTraceabilityValidationDetails flowTraceabilityValidationDetails =
          flowTraceability.getFlowTraceabilityValidationDetails();
      flowTraceabilityValidationDetails.setTotalDocument(flowDetails.getPageProcessed());
      flowTraceabilityValidationDetails.setTotalDocumentError(flowDetails.getPageError());

      // Save flow traceability
      var flowTraceabilityResponse = this.saveFlowTraceability(flowTraceability);

      if (PortalDepositType.isPortalDepositCampaignType(payload.getFlowType())) {
        this.flowCampaignDetailRepository.setTotalRecord(
            flowTraceability.getId(), Long.parseLong(payload.getNbDocuments()));
      }

      // Process list of document.
      this.processFlowDocuments(payload, flowTraceabilityResponse);

      // Produce message to update Flow traceability report
      this.updateFlowReport(flowTraceability);
    } catch (RuntimeException e) {
      log.error("", e);
    }
  }

  /**
   * To process flow documents create or update (flow documents, flow history, flow traceability).
   *
   * @param payload refer to object of {@link PreProcessingUpdateFlowTraceabilityModel}
   * @param flowTraceabilityResponse refer to object of {@link FlowTraceability}
   */
  private void processFlowDocuments(
      PreProcessingUpdateFlowTraceabilityModel payload, FlowTraceability flowTraceabilityResponse) {
    var flowDocuments = this.getFlowDocumentsByFlowId(flowTraceabilityResponse.getId());
    payload.getFlowDocuments().stream()
        .collect(
            HashMap<Integer, FileFlowDocument>::new,
            (hashMap, document) -> hashMap.put(hashMap.size(), document),
            (nonHashMap, nonHashMap2) -> {})
        .forEach(
            (idx, doc) -> {
              FlowDocument flowDocument =
                  flowDocuments.stream()
                      .filter(fd -> fd.getFileId().equals(doc.getUuid()))
                      .findFirst()
                      .orElse(new FlowDocument());

              // Set flow traceability to flow document.
              flowDocument.setFlowTraceability(flowTraceabilityResponse);

              this.mappingFlowDocument(flowDocument, doc, payload, idx, flowTraceabilityResponse);

              // Process create flow document detail.
              FlowDocumentDetails flowDocumentDetail = flowDocument.getDetail();

              // mapping values for flow document details
              mappingPostalDocBg(flowDocument, doc, payload, flowDocumentDetail);

              // Mapping value for flow document detail with attachment.
              mappingPostalDocAttachment(flowDocument, doc, payload, flowDocumentDetail);

              // Mapping value for flow document detail with signature
              mappingPostalDocSignature(flowDocument, doc, payload, flowDocumentDetail);

              // Mapping value for flow document detail with watermark
              mappingPostalDocWatermark(flowDocument, doc, payload, flowDocumentDetail);
              // Create flow document detail
              flowDocument.setDetail(flowDocumentDetail);
              flowDocuments.add(flowDocument);
            });
    this.saveAllFlowDocuments(flowDocuments);

    // Create flow document reports.
    final var depositMode = flowTraceabilityResponse.getDepositMode();
    final var channel = flowTraceabilityResponse.getChannel();
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_COMMITTED &&
              (DepositMode.PORTAL.getValue().equalsIgnoreCase(depositMode) ||
              DepositMode.VIRTUAL_PRINTER.getValue().equalsIgnoreCase(depositMode)) &&
              !Channel.DIGITAL.getValue().equalsIgnoreCase(channel)) {
              createFlowDocumentReport(flowDocuments);
            }
          }
        });
  }

  /**
   * Set background page for Postal Flow document.
   *
   * @param flowDocument Reference {@link FlowDocument}
   * @param doc Reference {@link FileFlowDocument}
   * @param payload Reference {@link PreProcessingUpdateFlowTraceabilityModel}
   */
  private void mappingPostalDocBg(
      FlowDocument flowDocument,
      FileFlowDocument doc,
      PreProcessingUpdateFlowTraceabilityModel payload,
      FlowDocumentDetails flowDocumentDetails) {
    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(flowDocument.getChannel())
        && Objects.nonNull(payload.getBackgroundPage())) {
      // Add page background to flow detail.
      createOrUpdateFlowDocumentBackground(payload.getBackgroundPage(), flowDocumentDetails);
    }
    this.mappingDocumentDetails(flowDocumentDetails, doc);
    flowDocumentDetails.setFlowDocument(flowDocument);
  }

  /**
   * Mapping values for {@link FlowDocumentDetails}.
   *
   * @param flowDocumentDetail object of {@link FlowDocumentDetails}
   * @param document object of {@link FileFlowDocument}
   */
  private void mappingDocumentDetails(
      FlowDocumentDetails flowDocumentDetail, FileFlowDocument document) {
    var addresses = document.getAddress();
    Map<String, String> sortedAddress = new LinkedHashMap<>();
    if (addresses != null) {
      addresses.entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .filter(addr -> !"".equals(addr.toString().trim()))
          .forEachOrdered(x -> sortedAddress.put(x.getKey(), x.getValue()));
      flowDocumentDetail.setAddress(String.join(", ", sortedAddress.values()));
    }

    flowDocumentDetail.setAddress(String.join(", ", sortedAddress.values()));
    flowDocumentDetail.setEmail(document.getEmailRecipient());
    var isDigitalEmail =
        FlowDocumentChannelConstant.DIGITAL.equalsIgnoreCase(document.getChannel())
            && "email".equalsIgnoreCase(document.getSubChannel());
    if (isDigitalEmail) {
      flowDocumentDetail.setEmail(document.getRecipientID());
    }

    var isSms =
        !StringUtils.isEmpty(document.getSubChannel())
            && document.getSubChannel().equalsIgnoreCase("sms");
    flowDocumentDetail.setTelephone(isSms ? document.getRecipientID() : "");

    flowDocumentDetail.setReference("");

    // Business rule SFG - EX 29.2 - CH 29.2_003
    // Postal case:  Reference = idDest or first line of address if idDest not present
    var isPostal = FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(document.getChannel());
    if (isPostal) {
      if (StringUtils.isNotBlank(document.getRecipientID())) {
        flowDocumentDetail.setReference(document.getRecipientID());
      } else {
        Optional<String> firstAddress =
            addresses.values().stream().filter(StringUtils::isNotBlank).findFirst();

        flowDocumentDetail.setReference(firstAddress.orElse("").toUpperCase());
      }
    }

    var docProcessing = document.getProcessing();
    flowDocumentDetail.setFillers(
        Arrays.asList(
                docProcessing.getFiller1(),
                docProcessing.getFiller2(),
                docProcessing.getFiller3(),
                docProcessing.getFiller4(),
                docProcessing.getFiller5())
            .toArray(new String[] {}));

    flowDocumentDetail.setDocName(docProcessing.getDocName());
    flowDocumentDetail.setArchiving(document.getProduction().getArchiving());
    flowDocumentDetail.setAddition(document.getProduction().getValidation());
    flowDocumentDetail.setColor(document.getProduction().getColor());
    flowDocumentDetail.setEnvelope(document.getProduction().getWrap());
    flowDocumentDetail.setImpression("");
    flowDocumentDetail.setPostage(document.getProduction().getUrgency());
    flowDocumentDetail.setWatermark(document.getProduction().getWatermark());
    flowDocumentDetail.setPostalPickup("");
  }

  /**
   * Map values to {@link FlowDocument} object.
   *
   * @param flowDocument refer to object reference of {@link FlowDocument}.
   * @param document refer to object {@link FlowDocument}.
   * @param payload refer to object of {@link PreProcessingUpdateFlowTraceabilityModel}.
   * @param docIndex refers to index of document.
   */
  private void mappingFlowDocument(
      FlowDocument flowDocument,
      FileFlowDocument document,
      PreProcessingUpdateFlowTraceabilityModel payload,
      int docIndex,
      FlowTraceability flowTraceabilityResponse) {
    var isStatusOk =
        document.getAnalyse().equalsIgnoreCase(FlowTreatmentConstants.FLOW_ANALYSIS_OK);
    var status = "";
    if (!isStatusOk) {
      status = FlowDocumentStatus.IN_ERROR.getValue();
    }

    // Business rule SFG - EX 29.2 - CH 29.2_003
    // Postal case only:  Recipient equal to the first non-empty value address
    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(document.getChannel())) {
      LinkedHashMap<String, String> sortAddressByKey =
          document.getAddress().entrySet().stream()
              .sorted(Entry.comparingByKey())
              .collect(
                  Collectors.toMap(
                      Entry::getKey, Entry::getValue, (key, value) -> key, LinkedHashMap::new));
      Optional<String> firstAddress =
          sortAddressByKey.values().stream().filter(StringUtils::isNotBlank).findFirst();

      this.setFlowDocument(
          flowDocument,
          document,
          payload.getCreatedBy(),
          status,
          docIndex,
          firstAddress.orElse(document.getRecipientID()));

    } else {
      this.setFlowDocument(
          flowDocument,
          document,
          payload.getCreatedBy(),
          status,
          docIndex,
          document.getRecipientID());
    }

    if (status.equalsIgnoreCase(FlowDocumentStatus.SCHEDULED.getValue())) {
      validateBeforeCreateDocumentHistory(
          flowDocument, payload.getServer(), payload.getCreatedBy(), flowTraceabilityResponse);
    } else {
      this.validateBeforeCreateDocumentHistory(
          flowDocument, payload.getServer(), payload.getCreatedBy());
    }
  }

  /**
   * validate flow document before create flow document history. Flow history is created when no
   * history's status in-progress.
   *
   * @param flowDocument object reference of {@link FlowDocument}.
   * @param server server name.
   * @param createdBy user who created the document.
   */
  private void validateBeforeCreateDocumentHistory(
      FlowDocument flowDocument, String server, String createdBy) {
    if (!StringUtils.isEmpty(flowDocument.getStatus())) {
      var hasHistory =
          flowDocument.getFlowDocumentHistories().stream()
              .filter(
                  flowDocumentHistory ->
                      flowDocumentHistory.getEvent().equalsIgnoreCase(flowDocument.getStatus()))
              .findFirst();

      if (hasHistory.isEmpty()) {
        // Process create flow document history.
        var flowDocumentHistory = new FlowDocumentHistory(flowDocument, server);
        // Add flow document history to flow document.
        flowDocument.addFlowDocumentHistory(flowDocumentHistory);
      } else {
        var history = hasHistory.get();
        history.setDateTime(flowDocument.getDateStatus());
        history.setLastModifiedBy(createdBy);
        history.setLastModified(new Date());
        flowDocument.addFlowDocumentHistory(history);
      }
    }
  }

  /**
   * validate flow document before create flow document history. Flow history is created when no
   * history's status in-progress.
   *
   * @param flowDocument object reference of {@link FlowDocument}.
   * @param server server name.
   * @param createdBy user who created the document.
   * @param flowTraceability object reference of {@link FlowTraceability}
   */
  private void validateBeforeCreateDocumentHistory(
      FlowDocument flowDocument,
      String server,
      String createdBy,
      FlowTraceability flowTraceability) {
    if (!StringUtils.isEmpty(flowDocument.getStatus())) {
      var hasHistory =
          flowDocument.getFlowDocumentHistories().stream()
              .filter(
                  flowDocumentHistory ->
                      flowDocumentHistory.getEvent().equalsIgnoreCase(flowDocument.getStatus()))
              .findFirst();

      if (hasHistory.isEmpty()) {
        // Process create flow document history.
        var flowDocumentHistory = new FlowDocumentHistory(flowDocument, server);
        // Add flow document history to flow document.
        flowDocumentHistory.setDateTime(flowTraceability.getDateStatus());
        flowDocument.addFlowDocumentHistory(flowDocumentHistory);
      } else {
        var history = hasHistory.get();
        history.setDateTime(flowTraceability.getDateStatus());
        history.setLastModifiedBy(createdBy);
        history.setLastModified(new Date());
        flowDocument.addFlowDocumentHistory(history);
      }
    }
  }

  /**
   * To update flow history when no document valid exists.
   *
   * @param flowTraceability refer to object of {@link FlowTraceability}
   * @param status refer to status from payload.
   */
  private void updateFlowHistoryOnFlowError(FlowTraceability flowTraceability, String status) {
    if (status.equalsIgnoreCase(FlowTraceabilityStatus.IN_ERROR.getValue())) {
      flowTraceability.getFlowHistories().stream()
          .filter(
              history ->
                  history.getEvent().equalsIgnoreCase(FlowHistoryStatus.TO_FINALIZE.getValue()))
          .forEach(
              history -> {
                var datetime = new Date();
                history.setDateTime(datetime);
                history.setEvent(FlowTraceabilityStatus.IN_ERROR.getFlowHistoryStatus());
                flowTraceability.addFlowHistory(history);
                flowTraceability.setDateStatus(datetime);
              });
    }
  }

  private void updateFlowReport(FlowTraceability flowTraceability) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_COMMITTED) {
              var flowTraceabilityReportModel =
                  UpdateFlowTraceabilityReportModel.builder()
                      .flowId(flowTraceability.getId())
                      .subChannel(flowTraceability.getSubChannel())
                      .build();

              UpdateFlowAfterPreTreatmentPortalListener.this
                  .getReportingService()
                  .updateFlowTraceabilityReport(flowTraceabilityReportModel);
            }
          }
        });
  }

  public void createOrUpdateFlowDocumentBackground(
      BackgroundPage backgroundPage, FlowDocumentDetails flowDocumentDetails) {
    List<FlowDocumentBackground> flowDocumentBackgrounds =
        ObjectUtils.defaultIfNull(
            flowDocumentDetails.getFlowDocumentBackgrounds(), new ArrayList<>());
    Map<String, FlowDocumentBackground> flowDocumentBackgroundMap =
        flowDocumentBackgrounds.stream()
            .collect(
                Collectors.toMap(
                    FlowDocumentBackground::getPosition,
                    flowDocumentBackground -> flowDocumentBackground));
    if (org.apache.commons.lang3.StringUtils.isNotBlank(backgroundPage.getPositionFirst())
        && isFilePresent(backgroundPage.getBackgroundFirst())) {
      FlowDocumentBackground flowDocumentBackground =
          ObjectUtils.defaultIfNull(
              flowDocumentBackgroundMap.get(backgroundPage.getPositionFirst()),
              new FlowDocumentBackground());
      flowDocumentBackground.setBackground(backgroundPage.getBackgroundFirst());
      flowDocumentBackground.setPosition(backgroundPage.getPositionFirst());
      flowDocumentBackground.setFlowDocumentDetails(flowDocumentDetails);
      flowDocumentBackgrounds.add(flowDocumentBackground);
    }
    if (org.apache.commons.lang3.StringUtils.isNotBlank(backgroundPage.getPosition())
        && isFilePresent(backgroundPage.getBackground())) {
      FlowDocumentBackground flowDocumentBackground =
          ObjectUtils.defaultIfNull(
              flowDocumentBackgroundMap.get(backgroundPage.getPosition()),
              new FlowDocumentBackground());
      flowDocumentBackground.setBackground(backgroundPage.getBackground());
      flowDocumentBackground.setPosition(backgroundPage.getPosition());
      flowDocumentBackground.setFlowDocumentDetails(flowDocumentDetails);
      flowDocumentBackgrounds.add(flowDocumentBackground);
    }
    if (org.apache.commons.lang3.StringUtils.isNotBlank(backgroundPage.getPositionLast())
        && isFilePresent(backgroundPage.getBackgroundLast())) {
      FlowDocumentBackground flowDocumentBackground =
          ObjectUtils.defaultIfNull(
              flowDocumentBackgroundMap.get(backgroundPage.getPositionLast()),
              new FlowDocumentBackground());
      flowDocumentBackground.setBackground(backgroundPage.getBackgroundLast());
      flowDocumentBackground.setPosition(backgroundPage.getPositionLast());
      flowDocumentBackground.setFlowDocumentDetails(flowDocumentDetails);
      flowDocumentBackgrounds.add(flowDocumentBackground);
    }
    flowDocumentDetails.setFlowDocumentBackgrounds(flowDocumentBackgrounds);
  }

  private boolean isFilePresent(String filename) {
    return FilenameUtils.isExtension(filename, "pdf");
  }

  private void mappingPostalDocAttachment(FlowDocument flowDocument, FileFlowDocument doc,
      PreProcessingUpdateFlowTraceabilityModel payload, FlowDocumentDetails flowDocumentDetail) {
    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(flowDocument.getChannel())
        && Objects.nonNull(payload.getAttachments())) {
      final Attachments attachmentPayload = payload.getAttachments();

      Map<String, FlowDocumentAttachment> attachmentEntitiesMap =
          flowDocumentDetail.getFlowDocumentAttachments().stream().collect(Collectors.toMap(
              FlowDocumentAttachment::getPosition,
              flowDocumentAttachment -> flowDocumentAttachment
          ));

      Map<String, String> attachmentPositionMap = new HashMap<>();
      Arrays.stream(attachmentPayload.getClass().getDeclaredFields()).collect(Collectors.toList())
          .stream().filter(field -> filterFieldValue(field, attachmentPayload))
          .forEach(field -> {
            try {
              String attachment = (String) field.get(attachmentPayload);
              String position = Objects.requireNonNull(
                  this.getAttachmentPosition(field.getName())).value;
              attachmentPositionMap.put(position, attachment);
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          });

      List<FlowDocumentAttachment> attachmentEntities = new ArrayList<>();
      attachmentPositionMap.forEach((position, attachment) -> {
        FlowDocumentAttachment flowDocumentAttachment = ObjectUtils.defaultIfNull(
            attachmentEntitiesMap.get(position), new FlowDocumentAttachment());
        flowDocumentAttachment.setPosition(position);
        flowDocumentAttachment.setAttachment(attachment);
        flowDocumentAttachment.setFlowDocumentDetails(flowDocumentDetail);

        attachmentEntities.add(flowDocumentAttachment);
      });

      flowDocumentDetail.setFlowDocumentAttachments(attachmentEntities);

      // Filter to remove attachments.
      if (attachmentPositionMap.size() != attachmentEntitiesMap.size()) {
        var attachmentPositions = attachmentPositionMap.entrySet().stream().map(Entry::getKey)
            .collect(Collectors.toList());

        attachmentEntitiesMap.forEach((position, flowDocumentAttachment) -> {
          if (!attachmentPositions.contains(position)) {
            flowDocumentDetail.getFlowDocumentAttachments().remove(flowDocumentAttachment);
          }
        });
      }
    }

    this.mappingDocumentDetails(flowDocumentDetail, doc);
    flowDocumentDetail.setFlowDocument(flowDocument);
  }

  /**
   * Filter all field of {@link Attachments} that has value.
   *
   * @param field             - object of {@link Field}.
   * @param attachmentPayload - object of {@link Attachments}.
   * @return - value of {@link Boolean}.
   */
  private boolean filterFieldValue(Field field, Attachments attachmentPayload) {
    try {
      field.setAccessible(true);
      String fieldValue = (String) field.get(attachmentPayload);
      return !fieldValue.isBlank();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Map field of {@link Attachments} with {@link AttachmentPosition}.
   *
   * @param attachmentField - field name of {@link Attachments}.
   * @return - enumeration of {@link AttachmentPosition}.
   */
  private AttachmentPosition getAttachmentPosition(String attachmentField) {
    if ("attachment1".equals(attachmentField)) {
      return AttachmentPosition.FIRST_POSITION;
    }
    if ("attachment2".equals(attachmentField)) {
      return AttachmentPosition.SECOND_POSITION;
    }
    if ("attachment3".equals(attachmentField)) {
      return AttachmentPosition.THIRD_POSITION;
    }
    if ("attachment4".equals(attachmentField)) {
      return AttachmentPosition.FOURTH_POSITION;
    }
    if ("attachment5".equals(attachmentField)) {
      return AttachmentPosition.FIFTH_POSITION;
    }

    return null;
  }

  private void mappingPostalDocSignature(
      FlowDocument flowDocument,
      FileFlowDocument doc,
      PreProcessingUpdateFlowTraceabilityModel payload,
      FlowDocumentDetails flowDocumentDetails) {
    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(flowDocument.getChannel())
        && Objects.nonNull(payload.getSignature())) {
        flowDocumentDetails.setSignature(payload.getSignature());
    }
    this.mappingDocumentDetails(flowDocumentDetails, doc);
    flowDocumentDetails.setFlowDocument(flowDocument);
  }

  private void mappingPostalDocWatermark(
          FlowDocument flowDocument,
          FileFlowDocument doc,
          PreProcessingUpdateFlowTraceabilityModel payload,
          FlowDocumentDetails flowDocumentDetails) {
    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(flowDocument.getChannel())
            && Objects.nonNull(payload.getWatermark())) {
      flowDocumentDetails.setWatermark(payload.getWatermark());
    }
    this.mappingDocumentDetails(flowDocumentDetails, doc);
    flowDocumentDetails.setFlowDocument(flowDocument);
  }
}
