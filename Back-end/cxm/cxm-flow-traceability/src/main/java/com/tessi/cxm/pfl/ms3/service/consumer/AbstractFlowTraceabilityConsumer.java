package com.tessi.cxm.pfl.ms3.service.consumer;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.dto.FlowDetailReview;
import com.tessi.cxm.pfl.ms3.entity.BaseHistoryEvent;
import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms3.repository.FlowCampaignDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDepositRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentDetailsRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentHistoryRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowHistoryRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.service.ReportingService;
import com.tessi.cxm.pfl.ms3.util.DepositMode;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentHistoryStatusReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowDocumentStatusReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Transactional
@Getter
@Slf4j
public abstract class AbstractFlowTraceabilityConsumer<T> implements Consumer<T> {

  private FlowTraceabilityRepository flowTraceabilityRepository;
  private FlowTraceabilityDetailRepository flowTraceabilityDetailRepository;
  private FlowDocumentRepository flowDocumentRepository;
  private FlowDocumentHistoryRepository flowDocumentHistoryRepository;
  private FlowDocumentDetailsRepository flowDocumentDetailsRepository;
  private FlowHistoryRepository flowHistoryRepository;

  private FlowCampaignDetailRepository flowCampaignDetailRepository;

  private FlowDepositRepository flowDepositRepository;

  @Getter(AccessLevel.PROTECTED)
  private ReportingService reportingService;

  /**
   * Maps {@code source} to an instance of {@code destination}. Mapping is performed according to
   * the corresponding TypeMap. If no TypeMap exists for {@code source.getClass()} and {@code
   * destinationType} then one is created.
   *
   * @param <E>         destination type
   * @param source      object to map from
   * @param destination type to map to
   * @return fully mapped instance of {@code destination}
   */
  public static <T, E> E mapping(T source, Class<E> destination) {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    return modelMapper.map(source, destination);
  }

  @Autowired
  public void setFlowDepositRepository(FlowDepositRepository flowDepositRepository) {
    this.flowDepositRepository = flowDepositRepository;
  }

  @Autowired
  public void setFlowHistoryRepository(FlowHistoryRepository flowHistoryRepository) {
    this.flowHistoryRepository = flowHistoryRepository;
  }

  @Autowired
  public void setFlowTraceabilityRepository(FlowTraceabilityRepository flowTraceabilityRepository) {
    this.flowTraceabilityRepository = flowTraceabilityRepository;
  }

  @Autowired
  public void setFlowDocumentRepository(FlowDocumentRepository flowDocumentRepository) {
    this.flowDocumentRepository = flowDocumentRepository;
  }

  @Autowired
  public void setFlowDocumentHistoryRepository(
      FlowDocumentHistoryRepository flowDocumentHistoryRepository) {
    this.flowDocumentHistoryRepository = flowDocumentHistoryRepository;
  }

  @Autowired
  public void setFlowTraceabilityDetailRepository(
      FlowTraceabilityDetailRepository flowTraceabilityDetailRepository) {
    this.flowTraceabilityDetailRepository = flowTraceabilityDetailRepository;
  }

  @Autowired
  public void setFlowDocumentDetailsRepository(
      FlowDocumentDetailsRepository flowDocumentDetailsRepository) {
    this.flowDocumentDetailsRepository = flowDocumentDetailsRepository;
  }

  @Autowired
  public void setFlowCampaignDetailRepository(
      FlowCampaignDetailRepository flowCampaignDetailRepository) {
    this.flowCampaignDetailRepository = flowCampaignDetailRepository;
  }

  @Autowired
  public void setReportingService(ReportingService reportingService) {
    this.reportingService = reportingService;
  }

  public FlowTraceability saveFlowTraceability(FlowTraceability flowTraceability) {
    return this.flowTraceabilityRepository.save(flowTraceability);
  }

  public FlowDocument saveFlowDocument(FlowDocument flowDocument) {
    return flowDocumentRepository.save(flowDocument);
  }

  public FlowTraceabilityDetails saveFlowTraceabilityDetails(
      FlowTraceabilityDetails flowTraceabilityDetails) {
    return this.flowTraceabilityDetailRepository.save(flowTraceabilityDetails);
  }

  public FlowDocumentDetails saveFlowDocumentDetails(FlowDocumentDetails flowDocumentDetails) {
    return this.flowDocumentDetailsRepository.save(flowDocumentDetails);
  }

  public FlowDocumentHistory saveFlowDocumentHistory(FlowDocumentHistory flowDocumentHistory) {
    return flowDocumentHistoryRepository.save(flowDocumentHistory);
  }

  public void updateFlowDocument(FlowDocument flowDocument) {
    this.flowDocumentRepository.updateFlowDocument(
        flowDocument.getCsvLineNumber(),
        flowDocument.getFlowTraceability().getId(),
        flowDocument.getFileSize(),
        flowDocument.getStatus(),
        flowDocument.getFileId(),
        flowDocument.getVersion());
  }

  public FlowTraceability getFlowByCampaignId(long campaignId) {
    return flowTraceabilityRepository
        .findByFlowTraceabilityDetailsCampaignId(campaignId)
        .orElseThrow(
            () ->
                new FlowTraceabilityNotFoundException(
                    "The flow traceability with the campaign id "
                        + campaignId
                        + " is not available"));
  }

  public Optional<FlowTraceabilityDetails> getFlowDetailReviewByFlowId(long flowId) {
    return flowTraceabilityDetailRepository.findFlowTraceabilityDetailsByCampaignId(flowId);
  }

  public FlowDetailReview getFlowDetailsByCampaignId(long campaignId) {
    return flowTraceabilityDetailRepository.findByCampaignId(campaignId);
  }

  public FlowDocument getFlowDocumentReferenceByCsvLineNumber(int csvLineNumber, long flowId) {
    return flowDocumentRepository
        .findByCsvLineNumberAndFlowTraceabilityId(csvLineNumber, flowId)
        .orElseThrow(() -> new FlowDocumentNotFoundException(csvLineNumber));
  }

  /**
   * To get flow traceability by flow name.
   *
   * @param fileId refer to name of flow traceability
   * @return object of {@link FlowTraceability}
   */
  public FlowTraceability getFlowTraceabilityByFileId(String fileId) {
    return flowTraceabilityRepository
        .findByFileId(fileId)
        .orElseThrow(() -> new FlowTraceabilityNotFoundException("Flow traceability not found."));
  }

  /**
   * To get flow traceability by flow file id.
   *
   * @param fileId refer to name of flow traceability
   * @return object of {@link FlowTraceability}
   */
  public Optional<FlowTraceability> getFlowByFileId(String fileId) {
    return flowTraceabilityRepository.findByFileId(fileId);
  }

  public void createHistoryOfFlow(FlowTraceability flowTraceability, String server) {
    var result =
        flowTraceability.getFlowHistories().stream()
            .filter(
                his ->
                    his.getEvent()
                        .equalsIgnoreCase(
                            FlowTraceabilityStatus.valueOfLabel(flowTraceability.getStatus())
                                .getFlowHistoryStatus()))
            .findFirst();
    // To prevent the duplicate status creation
    if (result.isPresent()) {
      var history = result.get();
      history.setDateTime(flowTraceability.getDateStatus());
      flowTraceability.setFlowHistories(Set.of(history));
    } else {
      var flowHistory = new FlowHistory();
      flowHistory.setCreatedBy(flowTraceability.getCreatedBy());
      flowHistory.setServer(server);
      flowHistory.setLastModifiedBy(flowTraceability.getCreatedBy());
      flowHistory.setEvent(
          FlowTraceabilityStatus.valueOfLabel(flowTraceability.getStatus()).getFlowHistoryStatus());
      flowTraceability.addFlowHistory(flowHistory);
    }
  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void saveAllFlowDocuments(List<FlowDocument> flowDocuments) {
    flowDocumentRepository.saveAll(flowDocuments);
  }

  public Optional<FlowTraceabilityDetails> getFlowTraceabilityDetailsById(long id) {
    return this.flowTraceabilityDetailRepository.findById(id);
  }

  public Optional<FlowDocument> findFlowDocumentByFileId(String fileId) {
    return this.flowDocumentRepository.findByFileId(fileId);
  }

  public Optional<FlowDocumentDetails> findFlowDocumentDetailsByDocument(
      FlowDocument flowDocument) {
    return this.flowDocumentDetailsRepository.findByFlowDocument(flowDocument);
  }

  @Transactional(readOnly = true)
  public List<FlowDocument> getFlowDocumentsByFlowId(long flowTraceabilityId) {
    return this.flowDocumentRepository.getFlowDocuments(flowTraceabilityId);
  }

  public void deleteFlowHistory(long flowTraceabilityId, String event) {
    this.flowHistoryRepository.deleteFlowHistory(flowTraceabilityId, event);
  }

  /**
   * To update flow traceability.
   *
   * @param flowTraceability refer to object of {@link FlowTraceability}
   * @param baseUpdate       refer to object of {@link BaseUpdateFlowFromProcessCtrl}
   */
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void updateFlowTraceability(
      FlowTraceability flowTraceability,
      BaseUpdateFlowFromProcessCtrl baseUpdate,
      boolean isCreateHistory) {
    updateFlowTraceability(flowTraceability, baseUpdate, isCreateHistory, "");
  }

  public void updateFlowTraceability(
      FlowTraceability flowTraceability,
      BaseUpdateFlowFromProcessCtrl baseUpdate,
      boolean isCreateHistory,
      String htmlContent) {

    if (isCreateHistory) {
      var existingHistory =
          flowTraceability.getFlowHistories().stream()
              .filter(
                  his ->
                      his.getEvent()
                          .equalsIgnoreCase(
                              FlowTraceabilityStatus.valueOfLabel(
                                      (baseUpdate
                                          .getStatus()
                                          .equals(FlowTraceabilityStatus.VALIDATED.getValue()))
                                          ? baseUpdate.getStatus()
                                          : flowTraceability.getStatus())
                                  .getFlowHistoryStatus()))
              .findFirst();
      FlowHistory history;
      if (existingHistory.isPresent()) {
        history = existingHistory.get();
        log.info("Update flow traceability history status: {}", history.getEvent());
        history.setDateTime(flowTraceability.getDateStatus());
      } else {
        // If Status equal to schedule, we used unloading date for flow history date status.
        Date dateTime = flowTraceability.getDateStatus();
        if (flowTraceability
            .getStatus()
            .equalsIgnoreCase(FlowTraceabilityStatus.SCHEDULED.getValue())
            && flowTraceability.getDepositMode().equalsIgnoreCase(DepositMode.PORTAL.getValue())) {
          //          dateTime = flowTraceability.getUnloadingDate();
          dateTime = new Date();
        }

        history = new FlowHistory(baseUpdate, dateTime);
        flowTraceability.addFlowHistory(history);
        log.info("Create flow traceability history status: {}", history.getEvent());
      }
    }
    // add campaign batch details from version 4.0 (BatchTreatment_ENI_V4.0.drawio)
    // from this version deposit batch details use campaign details instead of create new table
    final FlowTraceabilityDetails flowTraceabilityDetails =
        flowTraceability.getFlowTraceabilityDetails();
    final String portalDepositType = flowTraceabilityDetails.getPortalDepositType();
    if (org.springframework.util.StringUtils.hasText(portalDepositType)
        && FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(portalDepositType)) {
      final FlowCampaignDetail flowCampaignDetail =
          this.getFlowCampaignDetailByFlowId(flowTraceability.getId())
              .orElse(initializeCampaignBatchDetails(flowTraceability));
      flowCampaignDetail.setLastModifiedBy(flowTraceability.getLastModifiedBy());
      flowCampaignDetail.setTotalRecord(flowTraceabilityDetails.getPageProcessed());
      flowCampaignDetail.setHtmlTemplate(
          StringUtils.isBlank(htmlContent) ? flowCampaignDetail.getHtmlTemplate() : htmlContent);
      flowCampaignDetail.setCampaignType(flowTraceability.getDepositMode().toUpperCase());
      this.saveCampaignBatchDetails(flowCampaignDetail);
    }
    this.flowTraceabilityRepository.save(flowTraceability);
  }

  /**
   * Mapping flow document object by passed by reference object.
   *
   * @param flowDocument object reference.
   * @param document     object of {@link FileFlowDocument}
   * @param createdBy    user who created
   * @param status       status of the flow document.
   * @param docIndex     index of document.
   */
  public void setFlowDocument(
      FlowDocument flowDocument,
      FileFlowDocument document,
      String createdBy,
      String status,
      int docIndex,
      String recipient) {
    flowDocument.setDocument(document.getUuid());
    flowDocument.setRecipient(recipient);
    flowDocument.setStatus(status);
    flowDocument.setChannel(document.getChannel());
    flowDocument.setCreatedBy(createdBy);
    flowDocument.setLastModifiedBy(createdBy);
    flowDocument.setCsvLineNumber(docIndex + 1);
    flowDocument.setFileId(document.getUuid());
    flowDocument.setPageNumber(
        StringUtils.isEmpty(document.getNbPages()) ? 0 : Integer.parseInt(document.getNbPages()));
    flowDocument.setSheetNumber(
        StringUtils.isEmpty(document.getNbPages()) ? 0 : Integer.parseInt(document.getNbPages()));
    FlowDocumentSubChannel flowDocumentSubChannel =
            FlowDocumentSubChannel.valueOfLabel(document.getSubChannel());
    if (!ObjectUtils.isEmpty(flowDocumentSubChannel)) {
      flowDocument.setSubChannel(flowDocumentSubChannel.getValue());
    }else {
      flowDocument.setSubChannel(document.getSubChannel());
    }
  }

  /**
   * To update total of message error during process campaign.
   *
   * @param campaignId refer to id of flow or campaign in {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param sendError  refer number of send message error
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalErrorByCampaignId(long campaignId, int sendError) {
    this.flowCampaignDetailRepository.updateTotalErrorByCampaignId(campaignId, sendError);
  }

  public void updateTotalErrorById(long id, int sendError) {
    this.flowCampaignDetailRepository.updateTotalErrorById(id, sendError);
  }

  /**
   * To update total of delivered message during process campaign.
   *
   * @param id        refer to id of flow or campaign in {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param delivered refer number of message delivered
   */
  public void updateTotalDeliveredById(long id, int delivered) {
    this.flowCampaignDetailRepository.updateTotalDeliveredById(id, delivered);
  }

  /**
   * To update total of cancelled message during process campaign sms.
   *
   * @param id             refer to id of flow or campaign in {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param totalCancelled refer number of message cancelled
   */
  public void updateTotalCancelledById(long id, int totalCancelled) {
    this.flowCampaignDetailRepository.updateTotalCanceledById(id, totalCancelled);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateFlowDocumentHubIdDoc(FlowDocument flowDocument) {
    this.flowDocumentRepository.updateEmailCampaignDocumentUuid(
        flowDocument.getCsvLineNumber(),
        flowDocument.getFlowTraceability().getId(),
        flowDocument.getHubIdDoc(),
        flowDocument.getStatus(),
        flowDocument.getVersion());
  }

  /**
   * To retrieve flow document by hubIdDoc of {@link FlowDocument}.
   *
   * @param hubIdDoc refer to field of {@link FlowDocument}
   * @return object of {@link FlowDocument}
   */
  @Transactional(readOnly = true)
  public Optional<FlowDocument> findFlowDocumentByHubIdDoc(String hubIdDoc) {
    return this.flowDocumentRepository.findByHubIdDoc(hubIdDoc);
  }

  /**
   * To get flow traceability by id.
   *
   * @param id refer to id of flow traceability
   * @return object of {@link FlowTraceability}
   */
  public FlowTraceability findFlowTraceabilityById(long id) {
    return flowTraceabilityRepository
        .findById(id)
        .orElseThrow(() -> new FlowTraceabilityNotFoundException("Flow traceability not found."));
  }

  public long countDocumentNoErrorByFlowId(long flowId) {
    return this.flowDocumentRepository.countDocumentNoErrorByFlowId(flowId);
  }

  public long countDocumentCompletedByFlowId(long flowId) {
    return this.flowDocumentRepository.countDocumentCompletedByFlowId(flowId);
  }

  public void updateDocumentHistoryByDocumentIdAndEvent(
      long documentId, String event, Date dateStatus) {
    this.flowDocumentHistoryRepository.updateHistoryByDocumentIdAndEvent(
        dateStatus, documentId, event);
  }

  public void updateFlowDocumentHistory(
      UpdateFlowStatusModel updateFlowStatusModel, FlowDocument flowDocument, String status) {
    var flowDocumentHistories = flowDocument.getFlowDocumentHistories();
    if (flowDocumentHistories.stream().noneMatch(his -> his.getEvent().equalsIgnoreCase(status))) {
      var history = new FlowDocumentHistory();
      history.setDateTime(flowDocument.getDateStatus());
      history.setEvent(FlowDocumentStatus.valueOfLabel(status).getValue());
      history.setCreatedBy(updateFlowStatusModel.getCreatedBy());
      history.setServer(updateFlowStatusModel.getServer());
      flowDocument.addFlowDocumentHistory(history);
    }
  }

  public boolean existsDocumentHistoryByDocumentIdAndEvent(long documentId, String event) {
    return this.flowDocumentHistoryRepository.existsByFlowDocumentIdAndEvent(documentId, event);
  }

  public boolean existsByFlowDocumentIdAndEventIn(long documentId, List<String> event) {
    return this.flowDocumentHistoryRepository.existsByFlowDocumentIdAndEventIn(documentId, event);
  }

  /**
   * To check flow document all completed or not.
   *
   * @param campaignId refer to id of <b>Campaign</b>
   * @return true if flow document completed.
   */
  public boolean isFlowCampaignCompleted(long campaignId) {
    return this.flowCampaignDetailRepository.isFlowCampaignCompleted(campaignId);
  }

  public boolean isFlowCampaignCompleted(FlowTraceabilityDetails traceabilityDetails) {
    return this.flowCampaignDetailRepository.isFlowCampaignCompletedByFlowId(
        traceabilityDetails.getId());
  }

  /**
   * To check flow document all errors or not.
   *
   * @param campaignId refer to id of <b>Campaign</b>
   * @return true if flow document completed.
   */
  public boolean isFlowCampaignError(long campaignId) {
    return this.flowCampaignDetailRepository.isFlowCampaignError(campaignId);
  }

  public boolean isFlowCampaignError(FlowTraceabilityDetails traceabilityDetails) {
    return this.flowCampaignDetailRepository.isFlowCampaignErrorByFlowId(
        traceabilityDetails.getId());
  }

  /**
   * To update total of bounce message during process campaign.
   *
   * @param id        refer to id of flow. {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numBounce refer number of message bounce.
   */
  public void updateTotalBounceById(long id, int numBounce) {
    this.flowCampaignDetailRepository.updateTotalBounceById(id, numBounce);
  }

  /**
   * To update total of clicked message during process campaign.
   *
   * @param id         refer to id of flow {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numClicked refer number of message clicked.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalClickedById(long id, int numClicked) {
    this.flowCampaignDetailRepository.updateTotalClickedById(id, numClicked);
  }

  /**
   * To update total of opened message during process campaign.
   *
   * @param id        refer to id of flow or campaign in {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numOpened refer number of message opened.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalOpenedById(long id, int numOpened) {
    this.flowCampaignDetailRepository.updateTotalOpenedById(id, numOpened);
  }

  /**
   * To update total of blocked message during process campaign.
   *
   * @param id         refer to id of flow. {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numBlocked refer number of message bounce.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalBlockedById(long id, int numBlocked) {
    this.flowCampaignDetailRepository.updateTotalBlockedById(id, numBlocked);
  }

  /**
   * To update total of <b>PermanentError</b> message during process campaign.
   *
   * @param id                refer to id of flow. {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numPermanentError refer number of message <b>Hard Bounce</b>.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalPermanentErrorById(long id, int numPermanentError) {
    this.flowCampaignDetailRepository.updateTotalPermanentErrorById(id, numPermanentError);
  }

  /**
   * To update total of <b>TemporaryError</b> message during process campaign.
   *
   * @param id                refer to id of flow. {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numTemporaryError refer number of message <b>Soft Bounce</b>.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalTemporaryErrorById(long id, int numTemporaryError) {
    this.flowCampaignDetailRepository.updateTotalTemporaryErrorById(id, numTemporaryError);
  }

  /**
   * To update total of <b>Renvoi</b> message during process campaign.
   *
   * @param id        refer to id of flow. {@link com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail}
   * @param numResent refer number of message <b>Resent</b>.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateTotalResentById(long id, int numResent) {
    this.flowCampaignDetailRepository.updateTotalResentById(id, numResent);
  }

  /**
   * To update flow traceability status to completed when all document are completed.
   *
   * @param flowTraceability refer to object of {@link FlowTraceability}
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateFlowTraceabilityStatus(FlowTraceability flowTraceability, String server) {
    final FlowTraceabilityDetails flowTraceabilityDetails =
        flowTraceability.getFlowTraceabilityDetails();
    if (this.isFlowCampaignError(flowTraceabilityDetails)) {
      updateFlowStatusBaseOnCampaignDetail(flowTraceability, FlowTraceabilityStatus.IN_ERROR,
          server);
    } else if (this.isFlowCampaignCompleted(flowTraceabilityDetails)) {
      updateFlowStatusBaseOnCampaignDetail(flowTraceability, FlowTraceabilityStatus.COMPLETED,
          server);
    }
  }

  private void updateFlowStatusBaseOnCampaignDetail(
      FlowTraceability flowTraceability, FlowTraceabilityStatus status, String server) {
    int currentStep = 6;
    this.validateBatchStatusOrder(flowTraceability, List.of(currentStep - 1, currentStep));

    if (Stream.of(
            FlowTraceabilityStatus.COMPLETED.getValue(), FlowTraceabilityStatus.IN_ERROR.getValue())
        .noneMatch(f -> f.equalsIgnoreCase(flowTraceability.getStatus()))) {
      flowTraceability.setDateStatus(new Date());
    }
    flowTraceability.setStatus(status.getValue());

    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(flowTraceability.getDepositMode())) {
      var flowDetails = flowTraceability.getFlowTraceabilityDetails();
      flowDetails.setStep(currentStep);
    }

    var existingStatus =
        flowTraceability.getFlowHistories().stream()
            .filter(
                flowHistory ->
                    flowHistory
                        .getEvent()
                        .equalsIgnoreCase(
                            FlowTraceabilityStatus.valueOfLabel(status.getValue())
                                .getFlowHistoryStatus()))
            .findFirst();

    if (existingStatus.isEmpty()) {
      FlowHistory newFlowHistory = new FlowHistory(flowTraceability, server);
      newFlowHistory.setCreatedBy(flowTraceability.getCreatedBy());
      flowTraceability.addFlowHistory(newFlowHistory);
    }
    this.saveFlowTraceability(flowTraceability);
  }

  /**
   * update progress flowDocument.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateProgressDocument(String status, long flowId) {
    switch (FlowDocumentStatus.valueOfLabel(status)) {
      case SENT:
        this.updateTotalDeliveredById(flowId, 1);
        break;
      case IN_ERROR:
        this.updateTotalErrorById(flowId, 1);
        break;
      case HARD_BOUNCE:
      case SOFT_BOUNCE:
        this.updateTotalBounceById(flowId, 1);
        break;
      case CANCELED:
        this.updateTotalCancelledById(flowId, 1);
        break;
      default:
    }
  }

  public void saveFlowDeposit(FlowDeposit flowDeposit) {
    this.flowDepositRepository.save(flowDeposit);
  }

  public Optional<FlowDeposit> findFlowDeposit(long flowId) {
    return this.flowDepositRepository.findById(flowId);
  }

  public Optional<FlowCampaignDetail> getFlowCampaignDetailByFlowId(long flowId) {
    return this.flowCampaignDetailRepository.findById(flowId);
  }

  public void saveCampaignBatchDetails(FlowCampaignDetail flowCampaignDetail) {
    this.flowCampaignDetailRepository.save(flowCampaignDetail);
  }

  /**
   * initialize campaignBatchDetails of flow.
   */
  public FlowCampaignDetail initializeCampaignBatchDetails(FlowTraceability flowTraceability) {
    var flowDetail = new FlowCampaignDetail();
    flowDetail.setId(flowTraceability.getId());
    flowDetail.setFlowTraceability(flowTraceability);
    flowDetail.setCampaignName(flowTraceability.getFlowName());
    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(flowTraceability.getDepositMode())) {
      flowDetail.setCampaignType(flowTraceability.getDepositMode().toUpperCase());
    } else {
      flowDetail.setCampaignType(flowTraceability.getSubChannel());
    }
    flowDetail.setCreatedBy(flowTraceability.getCreatedBy());
    return flowDetail;
  }

  public void validateBatchStatusOrder(
      FlowTraceability flowTraceability, List<Integer> requiredSteps) {
    var flowDetails = flowTraceability.getFlowTraceabilityDetails();
    var currentStep = flowDetails.getStep();
    var allow = requiredSteps.stream().noneMatch(requiredStep -> currentStep == requiredStep);
    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(flowTraceability.getDepositMode())
        && allow) {
      throw new StatusNotInOrderException(requiredSteps, currentStep);
    }
  }

  public void createFlowDocumentReport(List<FlowDocument> flowDocuments) {
    flowDocuments.forEach(
        flowDocument -> {
          Set<CreateFlowDocumentHistoryStatusReportModel> flowDocumentHistoryModels =
              flowDocument.getFlowDocumentHistories().stream()
                  .map(
                      hist ->
                          new CreateFlowDocumentHistoryStatusReportModel(
                              hist.getId(),
                              flowDocument.getId(),
                              hist.getEvent(),
                              hist.getDateTime()))
                  .collect(Collectors.toSet());
          CreateFlowDocumentReportModel flowDocumentReportModel =
              CreateFlowDocumentReportModel.builder()
                  .documentId(flowDocument.getId())
                  .flowId(flowDocument.getFlowTraceability().getId())
                  .status(flowDocument.getStatus())
                  .subChannel(flowDocument.getSubChannel())
                  .dateStatus(flowDocument.getDateStatus())
                  .fillers(flowDocument.getDetail().getFillers())
                  .totalPage(flowDocument.getPageNumber())
                  .recipient(flowDocument.getRecipient())
                  .fileId(flowDocument.getFileId())
                  .createFlowDocumentHistoryStatusReportModels(flowDocumentHistoryModels)
                  .build();
          reportingService.createFlowDocumentReport(flowDocumentReportModel);
        });
  }

  public void updateFlowDocumentStatusReports(List<FlowDocument> flowDocuments) {
    flowDocuments.forEach(
        flowDocument -> {
          UpdateFlowDocumentStatusReportModel flowDocumentStatusReportModel =
              UpdateFlowDocumentStatusReportModel.builder()
                  .documentId(flowDocument.getId())
                  .status(flowDocument.getStatus())
                  .dateStatus(flowDocument.getDateStatus())
                  .totalPage(flowDocument.getPageNumber())
                  .subChannel(flowDocument.getSubChannel())
                  .idDoc(flowDocument.getIdDoc())
                  .build();
          if (reportingService.updateFlowDocumentStatusReport(flowDocumentStatusReportModel)) {
            createFlowDocumentHistoryReport(flowDocument);
          }
        });
  }

  public void updateFlowDocumentStatusReport(FlowDocument flowDocument) {
    UpdateFlowDocumentStatusReportModel flowDocumentStatusReportModel =
        UpdateFlowDocumentStatusReportModel.builder()
            .documentId(flowDocument.getId())
            .status(flowDocument.getStatus())
            .dateStatus(flowDocument.getDateStatus())
            .subChannel(flowDocument.getSubChannel())
            .idDoc(flowDocument.getIdDoc())
            .build();
    if (reportingService.updateFlowDocumentStatusReport(flowDocumentStatusReportModel)) {
      createFlowDocumentHistoryReport(flowDocument);
    }
  }

  public void createFlowDocumentHistoryReport(FlowDocument flowDocument) {

    final FlowDocumentHistory flowDocumentHistory =
        getLastEventDateFromFlowDocumentHistory(flowDocument.getFlowDocumentHistories());
    CreateFlowDocumentHistoryStatusReportModel historyStatusReportModel =
        CreateFlowDocumentHistoryStatusReportModel.builder()
            .id(flowDocumentHistory.getId())
            .flowDocumentId(flowDocument.getId())
            .status(flowDocumentHistory.getEvent())
            .dateStatus(flowDocumentHistory.getDateTime())
            .build();
    reportingService.createFlowDocumentEventHistory(historyStatusReportModel);
  }

  private FlowDocumentHistory getLastEventDateFromFlowDocumentHistory(
      Set<FlowDocumentHistory> flowDocumentHistories) {
    return flowDocumentHistories.stream()
        .max(Comparator.comparing(BaseHistoryEvent::getDateTime))
        .orElse(null);
  }
}
