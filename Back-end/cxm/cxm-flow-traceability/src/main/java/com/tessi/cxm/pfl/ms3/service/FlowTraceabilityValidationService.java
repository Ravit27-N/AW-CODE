package com.tessi.cxm.pfl.ms3.service;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.tessi.cxm.pfl.ms3.constant.FlowValidationConstant;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentValidationRequest;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.FlowValidationResponse;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityValidationDto;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityValidationDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowValidationBadRequestException;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms3.service.specification.FlowDocumentSpecification;
import com.tessi.cxm.pfl.ms3.service.specification.FlowTraceabilitySpecification;
import com.tessi.cxm.pfl.shared.exception.ClientUnloadingNotConfiguredException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.DepartmentProjection;
import com.tessi.cxm.pfl.shared.model.Group;
import com.tessi.cxm.pfl.shared.model.SummaryFlowValidation;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowDocumentStatusReportModel;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatusConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.EspaceValidation;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_ESPACE_VALIDATION;
import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_TRACEABILITY;

/**
 * Handling business logic of flows and documents validation.
 *
 * @author Piseth KHON
 * @author Chamrong THOR
 * @author Vichet CHANN
 * @version 1.9.0
 * @since 12 September 2022
 */
@Slf4j
@Service
public class FlowTraceabilityValidationService
    extends AbstractCrudService<FlowTraceabilityDto, FlowTraceability, Long> {

  private static final String CURRENT_SERVER_NAME;

  static {
    CURRENT_SERVER_NAME = ComputerSystemProduct.getDeviceId();
  }

  private final FlowTraceabilityRepository flowTraceabilityRepository;
  private final FlowDocumentRepository flowDocumentRepository;
  private final ProcessControlFeignClient processControlFeignClient;
  private final ReportingService reportingService;

  /**
   * Initialize require bean.
   *
   * @param flowTraceabilityRepository refers to bean of {@link FlowTraceabilityRepository}
   * @param flowDocumentRepository refers to the bean of {@link FlowDocumentRepository}
   * @param profileFeignClient refers to the bean of {@link ProfileFeignClient}
   * @param processControlFeignClient refers to the bean of {@link ProcessControlFeignClient}
   * @param keycloakService refers to the bean of {@link KeycloakService}
   * @param modelMapper refers to the bean of {@link ModelMapper}
   */
  public FlowTraceabilityValidationService(
      FlowTraceabilityRepository flowTraceabilityRepository,
      FlowDocumentRepository flowDocumentRepository,
      ProfileFeignClient profileFeignClient,
      ProcessControlFeignClient processControlFeignClient,
      KeycloakService keycloakService,
      ModelMapper modelMapper,
      ReportingService reportingService) {
    this.flowTraceabilityRepository = flowTraceabilityRepository;
    this.flowDocumentRepository = flowDocumentRepository;
    this.profileFeignClient = profileFeignClient;
    this.keycloakService = keycloakService;
    this.modelMapper = modelMapper;
    this.processControlFeignClient = processControlFeignClient;
    this.reportingService = reportingService;
  }

  /**
   * Getting all flows.
   *
   * @param pageable refers to object of {@link Pageable}
   * @param filterCriteria refers to the filter criteria
   * @return the page of {@link ListFlowTraceabilityDto}
   */
  public Page<ListFlowTraceabilityDto> getFlowValidationList(
      Pageable pageable, FlowFilterCriteria filterCriteria) {

    // get owner ids from the relatedOwners field.
    List<Long> ownerIds = this.getOwnerIdsByVisibilityList();

    if (filterCriteria.getUsers() != null && !filterCriteria.getUsers().isEmpty()) {
      ownerIds =
          ownerIds.stream()
              .parallel()
              .filter(ownerId -> filterCriteria.getUsers().contains(ownerId))
              .collect(Collectors.toUnmodifiableList());
    }

    final Specification<FlowTraceability> specification =
        FlowTraceabilitySpecification.equalStatus(FlowTraceabilityStatus.TO_VALIDATE.getValue())
            .and(FlowTraceabilitySpecification.ownerIdIn(ownerIds))
            .and(FlowTraceabilitySpecification.channelIn(filterCriteria.getChannels()))
            .and(FlowTraceabilitySpecification.subChannelIn(filterCriteria.getCategories()))
            .and(FlowTraceabilitySpecification.containFlowName(filterCriteria.getFilter()))
            .and(FlowTraceabilitySpecification.ignoreToFinalizeCampaign())
            .and(
                FlowTraceabilitySpecification.betweenOrEqualDepositDate(
                    filterCriteria.getStartDate(), filterCriteria.getEndDate()));
    return flowTraceabilityRepository
        .findAll(Specification.where(specification), pageable)
        .map(this::map);
  }

  private ListFlowTraceabilityValidationDto map(FlowTraceability flowTraceability) {
    ListFlowTraceabilityDto listFlowTraceabilityDto =
        this.modelMapper.map(flowTraceability, ListFlowTraceabilityDto.class);
    final FlowTraceabilityValidationDetails flowTraceabilityValidationDetails =
        flowTraceability.getFlowTraceabilityValidationDetails();
    ListFlowTraceabilityValidationDto listFlowTraceabilityValidationDto =
        new ListFlowTraceabilityValidationDto();
    if (!ObjectUtils.isEmpty(flowTraceabilityValidationDetails)) {
      listFlowTraceabilityValidationDto.setTotalRemainingValidationDocument(
          flowTraceabilityValidationDetails.getTotalRemaining());
      listFlowTraceabilityValidationDto.setTotalDocument(
          flowTraceabilityValidationDetails.getTotalDocument());
    }
    BeanUtils.copyProperties(listFlowTraceabilityDto, listFlowTraceabilityValidationDto);
    return listFlowTraceabilityValidationDto;
  }

  /**
   * Get all service of user.
   *
   * @return {@link List} of {@link Group}
   */
  public List<DepartmentProjection> getServicesOfUser() {
    return this.loadAllServicesInVisibilityLevel(
        CXM_FLOW_TRACEABILITY, ProfileConstants.Privilege.LIST);
  }

  /**
   * To validate all documents of the flows.
   *
   * @param fileIds refers flow file identity related to {@link FlowTraceability}
   * @param action refers to the action to be validated or refused
   * @return object of {@link FlowValidationResponse}
   */
  @Transactional(rollbackFor = Exception.class)
  public FlowValidationResponse validateFlow(List<String> fileIds, String action) {
    // Validate approval status.
    if (!action.equalsIgnoreCase(FlowTraceabilityStatus.REFUSE_DOC.getValue())
        && !action.equalsIgnoreCase(FlowTraceabilityStatus.VALIDATED.getValue())) {
      throw new FlowValidationBadRequestException(
          "Flow can approve only with status in "
              + FlowTraceabilityStatus.REFUSE_DOC.getValue()
              + " or "
              + FlowTraceabilityStatus.VALIDATED.getValue());
    }
    if (!checkPrivilegeCanValidateOrRefuseFlow()) {
      return FlowValidationResponse.builder()
          .totalError(fileIds.size())
          .totalSuccess(0)
          .errors(fileIds)
          .successes(Collections.emptyList())
          .build();
    }

    // Validate visibility level.
    final List<Long> ownerIds =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
                CXM_ESPACE_VALIDATION, EspaceValidation.VALIDATE_OR_REFUSE, false, true)
            .getRelatedOwners();

    final Specification<FlowTraceability> specification =
        FlowTraceabilitySpecification.ownerIdIn(ownerIds)
            .and(
                FlowTraceabilitySpecification.equalStatus(
                    FlowTraceabilityStatus.TO_VALIDATE.getValue()))
            .and(FlowTraceabilitySpecification.fileIdByIn(fileIds));

    final var visibleFlows = this.flowTraceabilityRepository.findAll(specification);
    final List<String> visibleFlowIds =
        visibleFlows.stream().map(FlowTraceability::getFileId).collect(Collectors.toList());
    final List<String> invisibleFlowIds =
        fileIds.stream().filter(e -> !visibleFlowIds.contains(e)).collect(Collectors.toList());
    final List<String> visibleComposedIds =
        visibleFlows.stream()
            .map(e -> e.getFlowTraceabilityDetails().getComposedId())
            .collect(Collectors.toList());
    final List<FlowDocument> visibleFlowsDocs =
        this.flowDocumentRepository.findAllFlowDocumentByFlowIdIn(
            visibleFlowIds, FlowDocumentStatusConstant.TO_VALIDATE);

    Date dateStatus = new Date();
    String currentUser = this.keycloakService.getUserInfo().getUsername();

    // Set up validation flow request.
    var request = new ValidationFlowRequest();
    request.setFlowIds(visibleFlowIds);
    request.setComposedIds(visibleComposedIds);
    request.setCreatedBy(currentUser);

    // Process update flow-traceability and flow-document to status refuse.
    if (action.equalsIgnoreCase(FlowTraceabilityStatusConstant.REFUSE_DOC)) {
      request.setValidate(false);
      this.flowDocumentRepository.saveAll(
          this.updateFlowDocStatus(
              visibleFlowsDocs,
              FlowDocumentStatusConstant.REFUSE_DOC,
              FlowDocumentStatusConstant.REFUSE_DOC,
              dateStatus,
              currentUser));
      visibleFlows.stream()
          .parallel()
          .forEach(
              flowTraceability ->
                  refuseDocument(
                      visibleFlowsDocs.stream()
                          .filter(
                              doc ->
                                  Objects.equals(
                                      doc.getFlowTraceability().getId(), flowTraceability.getId()))
                          .map(FlowDocument::getFileId)
                          .collect(Collectors.toList()),
                      flowTraceability.getId(),
                      currentUser));
      visibleFlows.stream()
          .parallel()
          .forEach(
              flowTraceability ->
                  updateFlowStatusAfterProcessCompleted(
                      flowTraceability.getId(), currentUser, false));
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
              if (status == TransactionSynchronization.STATUS_COMMITTED) {
                FlowTraceabilityValidationService.this.processControlFeignClient.validateFlow(
                    request, FlowTraceabilityValidationService.this.getAuthTokenWithPrefix());
              }
            }
          });
    }
    // End update flow-traceability and flow-document to status refuse.

    if (action.equalsIgnoreCase(FlowTraceabilityStatus.VALIDATED.getValue())) {
      // Process update flow-traceability and flow-document to status validate.
      var clientUnloadDetails =
          this.profileFeignClient.getClientUnloadDetails(this.getAuthTokenWithPrefix());
      if (clientUnloadDetails.getClientUnloads().isEmpty()) {
        throw new ClientUnloadingNotConfiguredException(clientUnloadDetails.getClientId());
      }
      request.setValidate(true);
      try {
        this.flowDocumentRepository.saveAll(
            this.updateFlowDocStatus(
                visibleFlowsDocs,
                FlowDocumentStatusConstant.VALIDATED,
                FlowDocumentStatusConstant.VALIDATED,
                dateStatus,
                currentUser));
        this.flowTraceabilityRepository.saveAll(
            this.updateFlowStatus(
                visibleFlows,
                FlowDocumentStatusConstant.VALIDATED,
                FlowDocumentStatusConstant.VALIDATED,
                dateStatus,
                currentUser));

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
              @Override
              public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                  String serverId =
                      FlowTraceabilityValidationService.this.processControlFeignClient.validateFlow(
                          request, FlowTraceabilityValidationService.this.getAuthTokenWithPrefix());
                  FlowTraceabilityValidationService.this.flowTraceabilityRepository
                      .updateServerIdFlowHistoryValidated(
                          serverId, FlowTraceabilityStatusConstant.VALIDATED, fileIds);
                  FlowTraceabilityValidationService.this.flowDocumentRepository
                      .updateServerIdFlowDocumentHistoryValidated(
                          serverId, FlowTraceabilityStatusConstant.VALIDATED, fileIds);
                }
              }
            });
        // End update flow-traceability and flow-document to status validate.
      } catch (Exception e) {
        // Process update flow-traceability and flow-document to status in-error.
        this.flowDocumentRepository.saveAll(
            this.updateFlowDocStatus(
                visibleFlowsDocs,
                FlowDocumentStatusConstant.IN_ERROR,
                FlowDocumentStatusConstant.IN_ERROR,
                dateStatus,
                currentUser));
        this.flowTraceabilityRepository.saveAll(
            this.updateFlowStatus(
                visibleFlows,
                FlowDocumentStatusConstant.IN_ERROR,
                FlowDocumentStatusConstant.IN_ERROR,
                dateStatus,
                currentUser));
        // End update flow-traceability and flow-document to status in-error.
      }
    }

    return FlowValidationResponse.builder()
        .totalError((long) fileIds.size() - visibleFlows.size())
        .totalSuccess(visibleFlows.size())
        .errors(invisibleFlowIds)
        .successes(visibleFlowIds)
        .build();
  }

  private List<FlowTraceability> updateFlowStatus(
      List<FlowTraceability> flowTraceability,
      String status,
      String historyStatus,
      Date dateStatus,
      String currentUser) {
    flowTraceability.forEach(
        e -> {
          e.setStatus(status);
          e.setDateStatus(dateStatus);
          e.setLastModifiedBy(currentUser);

          FlowHistory flowHistory = new FlowHistory();
          flowHistory.setFlowTraceability(e);
          flowHistory.setCreatedBy(currentUser);
          flowHistory.setEvent(historyStatus);
          flowHistory.setServer(CURRENT_SERVER_NAME);
          flowHistory.setDateTime(dateStatus);
          e.addFlowHistory(flowHistory);
        });
    return flowTraceability;
  }

  private List<FlowDocument> updateFlowDocStatus(
      List<FlowDocument> visibleFlowsDocs,
      String status,
      String historyStatus,
      Date dateStatus,
      String currentUser) {
    visibleFlowsDocs.forEach(
        e -> {
          e.setStatus(status);
          e.setDateStatus(dateStatus);
          e.setLastModifiedBy(currentUser);
          FlowDocumentHistory flowDocumentHistory = new FlowDocumentHistory();
          flowDocumentHistory.setFlowDocument(e);
          flowDocumentHistory.setCreatedBy(currentUser);
          flowDocumentHistory.setEvent(historyStatus);
          flowDocumentHistory.setServer(CURRENT_SERVER_NAME);
          flowDocumentHistory.setDateTime(dateStatus);
          e.addFlowDocumentHistory(flowDocumentHistory);
        });
    return visibleFlowsDocs;
  }

  /**
   * Getting the documents of a flow to validate.
   *
   * @param filter refers to name of flow or a document to filter
   * @param flowId refers to the identity of {@link FlowTraceability}
   * @param pageable refers to object of {@link Pageable}
   * @return the page of {@link FlowDocumentDto}
   * @see #getOwnerIdsByVisibilityList()
   */
  @Transactional(readOnly = true)
  public Page<FlowDocumentDto> getFlowDocumentValidationList(
      String filter, long flowId, Pageable pageable) {
    Specification<FlowDocument> specification =
        Specification.where(
            FlowDocumentSpecification.equalStatus(FlowTraceabilityStatus.TO_VALIDATE.getValue()));
    if (flowId != 0) {
      // to get owner id from flow traceability.
      var flowTraceability = this.findFlowTraceabilityById(flowId);
      PrivilegeValidationUtil.validateUserAccessPrivilege(
          ProfileConstants.CXM_ESPACE_VALIDATION,
          ProfileConstants.EspaceValidation.LIST,
          true,
          flowTraceability.getOwnerId());
      specification = specification.and(FlowDocumentSpecification.equalToFlowId(flowId));

    } else {
      if (!hasVisibility(CXM_ESPACE_VALIDATION, EspaceValidation.LIST)) {
        throw new UserAccessDeniedExceptionHandler();
      }
      List<Long> ownerIds = this.getOwnerIdsByVisibilityList();
      specification = specification.and(FlowDocumentSpecification.ownerIdIn(ownerIds));
    }
    if (StringUtils.hasText(filter)) {
      specification =
          specification.and(FlowDocumentSpecification.containFlowNameOrDocumentName(filter));
    }
    if (pageable.isUnpaged()) {
      final Stream<FlowDocumentDto> flowDocumentDtoStream =
          this.flowDocumentRepository.findAll(Specification.where(specification)).stream()
              .map(flowDocument -> this.modelMapper.map(flowDocument, FlowDocumentDto.class));
      return new PageImpl<>(flowDocumentDtoStream.collect(Collectors.toList()));
    }
    return this.flowDocumentRepository
        .findAll(Specification.where(specification), pageable)
        .map(flowDocument -> this.modelMapper.map(flowDocument, FlowDocumentDto.class));
  }

  /**
   * To validate documents of a flow.
   *
   * @param request refers to the object of {@link FlowDocumentValidationRequest}
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void validateFlowDocument(FlowDocumentValidationRequest request) {
    final var createdBy = this.getUsername();

    var flowTraceability = this.findFlowTraceabilityById(request.getFlowId());

    // checking the privilege user based on the functional key and privilege key.
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        CXM_ESPACE_VALIDATION,
        EspaceValidation.VALIDATE_OR_REFUSE,
        false,
        flowTraceability.getOwnerId());

    final boolean isValidate =
        request.getAction().equalsIgnoreCase(FlowValidationConstant.VALIDATE);
    if (isValidate) {
      var clientUnloadDetails =
          this.profileFeignClient.getClientUnloadDetails(this.getAuthTokenWithPrefix());
      if (clientUnloadDetails.getClientUnloads().isEmpty()) {
        throw new ClientUnloadingNotConfiguredException(clientUnloadDetails.getClientId());
      }
      this.validateDocument(request.getDocumentIds(), request.getFlowId(), createdBy);
    }

    if (FlowValidationConstant.REFUSE.equalsIgnoreCase(request.getAction())) {
      this.refuseDocument(request.getDocumentIds(), request.getFlowId(), createdBy);
      this.updateFlowStatusAfterProcessCompleted(request.getFlowId(), createdBy, true);

      ///
      this.updateFlowDocumentRefuseStatusReport(request.getFlowId(), request.getDocumentIds());
    }

    var flowValidation =
        this.flowTraceabilityRepository
            .getDocumentDocIdToValidate(request.getFlowId())
            .orElseThrow(() -> new FlowTraceabilityNotFoundException(request.getFlowId()));
    var requestValidation =
        new ValidationFlowDocumentRequest(
            flowValidation.getFileId(),
            flowValidation.getComposedId(),
            createdBy,
            request.getDocumentIds(),
            isValidate);

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            log.info("Transaction status: {}", status);
            if (status == TransactionSynchronization.STATUS_COMMITTED && isValidate) {
              FlowTraceabilityValidationService.this.processControlFeignClient.validateFlowDocument(
                  requestValidation,
                  FlowTraceabilityValidationService.this.getAuthTokenWithPrefix());
            }
          }
        });
  }

  private void validateDocument(List<String> ids, long flowId, String createdBy) {
    var documents =
        this.flowDocumentRepository.getFlowDocuments(flowId).parallelStream()
            .filter(doc -> ids.contains(doc.getFileId()))
            .map(
                document ->
                    this.mapDocument(document, FlowDocumentStatus.VALIDATED.getValue(), createdBy))
            .collect(Collectors.toList());
    this.flowDocumentRepository.saveAll(documents);
  }

  private void refuseDocument(List<String> ids, long flowId, String createdBy) {
    List<FlowDocument> flowDocuments = this.flowDocumentRepository.getFlowDocuments(flowId);

    Optional<FlowDocumentHistory> firstDocumentValidated =
        flowDocuments.stream()
            .flatMap(flowDocument -> flowDocument.getFlowDocumentHistories().stream())
            .filter(
                docHistory ->
                    FlowDocumentStatus.VALIDATED.getValue().equalsIgnoreCase(docHistory.getEvent()))
            .findFirst();
    var documents =
        flowDocuments.parallelStream()
            .filter(doc -> ids.contains(doc.getFileId()))
            .map(
                document ->
                    this.mapDocument(document, FlowDocumentStatus.REFUSE.getValue(), createdBy))
            .collect(Collectors.toList());
    this.flowDocumentRepository.saveAll(documents);
    this.getFlowValidationInfo(flowId)
        .ifPresent(
            value -> {
              var validationDetails = value.getFlowTraceabilityValidationDetails();
              var totalRefuses = validationDetails.getTotalDocumentRefused() + documents.size();
              var totalRemaining = validationDetails.getTotalRemaining() - documents.size();
              validationDetails.setTotalRemaining(totalRemaining);
              validationDetails.setTotalDocumentRefused(totalRefuses);
              value.setFlowTraceabilityValidationDetails(validationDetails);
              if (firstDocumentValidated.isPresent()) {
                FlowDocumentHistory flowDocumentHistory = firstDocumentValidated.get();
                var newHistory =
                    new FlowHistory(
                        FlowTraceabilityStatus.VALIDATED.getValue(),
                        CURRENT_SERVER_NAME,
                        flowDocumentHistory.getDateTime());
                FlowHistory modifyFlowHistory =
                    value.getFlowHistories().stream()
                        .filter(
                            flowHistory ->
                                flowHistory
                                    .getEvent()
                                    .equalsIgnoreCase(FlowTraceabilityStatus.VALIDATED.getValue()))
                        .findFirst()
                        .orElse(newHistory);
                modifyFlowHistory.setCreatedBy(flowDocumentHistory.getCreatedBy());
                modifyFlowHistory.setDateTime(flowDocumentHistory.getDateTime());
                modifyFlowHistory.setLastModified(flowDocumentHistory.getLastModified());
                modifyFlowHistory.setLastModifiedBy(flowDocumentHistory.getLastModifiedBy());
                value.addFlowHistory(modifyFlowHistory);
              }
              this.flowTraceabilityRepository.save(value);
            });
  }

  /**
   * Handling the process of updating flow status after validation consume from the process-control
   * and create the new sql transaction.
   *
   * @param id refers to the identity of a flow
   * @param createdBy refers the user is working on this process
   * @param status refers to
   * @param isIncludeValidateStatus refers to the option of including the 'validated' status into
   *     the flow history
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateFlowStatusAfterValidation(
      long id, String createdBy, String status, boolean isIncludeValidateStatus) {
    if (StringUtils.hasText(status)) {
      this.updateFlowStatusAfterProcessCompleted(id, createdBy, status, isIncludeValidateStatus);
    } else {
      this.updateFlowStatusAfterProcessCompleted(id, createdBy, isIncludeValidateStatus);
    }
  }

  /**
   * Handling the process of providing the status to a flow during the validation process.
   *
   * @param id refers to the identity of a flow
   * @param createdBy refers the user is working on this process
   * @param isIncludeValidateStatus refers to the option of including the 'validated' status into
   *     the flow history
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateFlowStatusAfterProcessCompleted(
      long id, String createdBy, boolean isIncludeValidateStatus) {
    var scheduleStatus =
        List.of(FlowDocumentStatusConstant.VALIDATED, FlowDocumentStatusConstant.SCHEDULED);
    var isSchedule =
        this.flowDocumentRepository.getFlowDocuments(id).stream()
            .anyMatch(doc -> scheduleStatus.contains(doc.getStatus()));
    if (isSchedule) {
      this.updateFlowStatusAfterProcessCompleted(
          id, createdBy, FlowTraceabilityStatus.SCHEDULED.getValue(), isIncludeValidateStatus);
    } else {
      this.updateFlowStatusAfterProcessCompleted(
          id, createdBy, FlowTraceabilityStatus.IN_PROCESS.getValue(), isIncludeValidateStatus);
    }
  }

  /**
   * Handling process of update status flow and flow history creation if the flow or all documents
   * of a flow are validated.
   *
   * @param id refers to identity of {@link FlowTraceability}
   * @param createdBy refers user working on this process
   * @param flowStatus refers the status of a flow
   * @param isIncludeValidateStatus refers to the option of including the 'validated' status into
   *     the flow history
   * @see FlowTraceabilityValidationService#includeValidateStatus(FlowTraceability, String, boolean)
   */
  public void updateFlowStatusAfterProcessCompleted(
      long id, String createdBy, String flowStatus, boolean isIncludeValidateStatus) {
    var flow = this.getFlowValidationInfo(id);
    if (flow.isPresent()) {
      var value = flow.get();
      var detail = value.getFlowTraceabilityValidationDetails();
      final var approvalDocs =
          detail.getTotalDocumentValidation()
              + detail.getTotalDocumentError()
              + detail.getTotalDocumentRefused();
      if (approvalDocs == detail.getTotalDocument()) {
        this.flowTraceabilityRepository
            .findById(id)
            .ifPresent(
                ft -> {
                  // Update status to 'In process'  if number of document validation greater than 0
                  if (detail.getTotalDocumentValidation() > 0) {
                    this.includeValidateStatus(ft, createdBy, isIncludeValidateStatus);
                    ft.setStatus(flowStatus);
                    ft.setDateStatus(new Date());
                    ft.setLastModifiedBy(createdBy);
                    // Set schedule status to flow when refused flow but any document of flow is in
                    // progress status
                    if (!flowStatus.equalsIgnoreCase(FlowTraceabilityStatus.SCHEDULED.getValue())
                        && ft.getFlowHistories().stream()
                            .noneMatch(
                                his ->
                                    his.getEvent()
                                        .equals(
                                            FlowTraceabilityStatus.SCHEDULED
                                                .getFlowHistoryStatus()))) {
                      var scheduleHistory =
                          new FlowHistory(
                              FlowTraceabilityStatus.SCHEDULED.getFlowHistoryStatus(),
                              CURRENT_SERVER_NAME,
                              new Date());
                      scheduleHistory.setCreatedBy(createdBy);
                      ft.addFlowHistory(scheduleHistory);
                    }

                    var inProcessHistory =
                        new FlowHistory(
                            FlowTraceabilityStatus.valueOfLabel(flowStatus).getFlowHistoryStatus(),
                            CURRENT_SERVER_NAME,
                            new Date());
                    inProcessHistory.setCreatedBy(createdBy);
                    ft.addFlowHistory(inProcessHistory);
                  } else {
                    ft.setStatus(FlowTraceabilityStatus.REFUSE_DOC.getValue());
                    ft.setLastModifiedBy(createdBy);
                    ft.setDateStatus(new Date());
                    var refuseHistory =
                        new FlowHistory(
                            FlowTraceabilityStatus.REFUSE_DOC.getFlowHistoryStatus(),
                            CURRENT_SERVER_NAME,
                            ft.getDateStatus());
                    refuseHistory.setCreatedBy(createdBy);
                    ft.addFlowHistory(refuseHistory);
                  }
                  this.flowTraceabilityRepository.save(ft);
                });
      }
    }
  }


  public SummaryFlowValidation getUserFlowValidation() {

    final Specification<FlowTraceability> specification =
            FlowTraceabilitySpecification
                    .equalStatus(FlowTraceabilityStatus.TO_VALIDATE.getValue())
                    .and(FlowTraceabilitySpecification.ownerIdIn(getOwnerIdsByVisibilityList()))
                    .and(FlowTraceabilitySpecification.ignoreToFinalizeCampaign());

    var count = this.flowTraceabilityRepository
            .count(Specification.where(Specification.where(specification)));

    var first = this.flowTraceabilityRepository
            .findAll(Specification.where(specification), PageRequest.of(0, 1, Sort.by(FlowTraceability_.DATE_STATUS).ascending()))
            .stream().findFirst().orElse(null);

    var last = this.flowTraceabilityRepository
            .findAll(Specification.where(specification), PageRequest.of(0, 1, Sort.by(FlowTraceability_.DATE_STATUS).descending()))
            .stream().findFirst().orElse(null);

    return SummaryFlowValidation.builder()
            .total(count)
            .startDate(count > 0 ? Objects.requireNonNull(first).getDateStatus().toString() : null)
            .endDate(count > 0 ? Objects.requireNonNull(last).getDateStatus().toString() : null)
            .build();
  }
  

  private void includeValidateStatus(FlowTraceability flow, String createdBy, boolean isInclude) {
    if (isInclude) {
      var history =
          flow.getFlowHistories().stream()
              .filter(
                  his ->
                      his.getEvent()
                          .equalsIgnoreCase(
                              FlowTraceabilityStatus.VALIDATED.getFlowHistoryStatus()))
              .findFirst()
              .orElse(
                  new FlowHistory(
                      FlowTraceabilityStatus.VALIDATED.getFlowHistoryStatus(),
                      CURRENT_SERVER_NAME,
                      new Date()));
      history.setCreatedBy(createdBy);
      flow.addFlowHistory(history);
    }
  }

  private Optional<FlowTraceability> getFlowValidationInfo(long id) {
    var specification =
        Specification.where(FlowTraceabilitySpecification.getFlowValidationInfo(id));
    return this.flowTraceabilityRepository.findOne(specification);
  }

  private FlowDocument mapDocument(FlowDocument document, String status, String createdBy) {
    document.setDateStatus(new Date());
    document.setStatus(status);
    var history =
        document.getFlowDocumentHistories().stream()
            .filter(his -> his.getEvent().equalsIgnoreCase(status))
            .findFirst()
            .orElse(new FlowDocumentHistory(document, CURRENT_SERVER_NAME));
    history.setCreatedBy(createdBy);
    document.addFlowDocumentHistory(history);
    return document;
  }

  /**
   * To get user details by privilege key.
   *
   * <pre>
   *   functionality key: cxm_espace_validation
   *   privilege key: cxm_espace_validation_list
   * </pre>
   *
   * @return collection {@link List} of {@link Long}. owner ids or user id that are related with
   *     this functionality key.
   * @see PrivilegeValidationUtil#getUserPrivilegeDetails(String, String, boolean, boolean)
   */
  protected List<Long> getOwnerIdsByVisibilityList() {
    return PrivilegeValidationUtil.getUserPrivilegeDetails(
            CXM_ESPACE_VALIDATION, EspaceValidation.LIST, true, true)
        .getRelatedOwners();
  }

  /**
   * To retrieve flow traceability by id.
   *
   * @param flowId refer to identify of flow.
   * @return if present return object of {@link FlowTraceability} else
   * @throws FlowTraceabilityNotFoundException exception with flow id.
   */
  private FlowTraceability findFlowTraceabilityById(Long flowId) {
    return flowTraceabilityRepository
        .findById(flowId)
        .orElseThrow(() -> new FlowTraceabilityNotFoundException(flowId));
  }

  /**
   * To check the privilege of the user can validate or refuse flow traceability base on the key
   * below.
   *
   * <pre>
   *   functionality key: <strong>cxm_espace_validation</strong>
   *   privilege key: <strong>cxm_espace_validation_validate_or_refuse</strong>
   * </pre>
   *
   * @return true, otherwise false
   */
  private boolean checkPrivilegeCanValidateOrRefuseFlow() {
    try {
      this.profileFeignClient.getUserPrivilegeRelatedOwner(
          this.getAuthTokenWithPrefix(),
          CXM_ESPACE_VALIDATION,
          CXM_ESPACE_VALIDATION.concat("_").concat(EspaceValidation.VALIDATE_OR_REFUSE),
          false,
          false);
      return true;
    } catch (FeignException.NotFound privilegeNotFoundException) {
      return false;
    }
  }

  private void updateFlowDocumentRefuseStatusReport(Long flowId, List<String> documentIds) {
    flowDocumentRepository.getFlowDocuments(flowId).parallelStream()
        .filter(doc -> documentIds.contains(doc.getFileId()))
        .forEach(
            flowDocument -> {
              UpdateFlowDocumentStatusReportModel flowDocumentStatusReportModel =
                  UpdateFlowDocumentStatusReportModel.builder()
                      .documentId(flowDocument.getId())
                      .status(flowDocument.getStatus())
                      .dateStatus(flowDocument.getDateStatus())
                      .build();

              reportingService.updateFlowDocumentStatusReport(flowDocumentStatusReportModel);
            });
  }
}
