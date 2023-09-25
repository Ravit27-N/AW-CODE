package com.tessi.cxm.pfl.ms3.service;

import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_TRACEABILITY;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.dto.DepositFlowInfoDto;
import com.tessi.cxm.pfl.ms3.dto.FlowCampaignDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteriaDto;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.StatusNotFoundException;
import com.tessi.cxm.pfl.ms3.repository.FlowCampaignDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDepositRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms3.service.specification.FlowTraceabilitySpecification;
import com.tessi.cxm.pfl.ms3.util.Channel;
import com.tessi.cxm.pfl.ms3.util.DepositMode;
import com.tessi.cxm.pfl.ms3.util.SubChannel;
import com.tessi.cxm.pfl.shared.model.SearchCriteria;
import com.tessi.cxm.pfl.shared.model.SharedUserEntityDTO;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.SharedRepository;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.MessageProducingAction;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handle business logic of {@link FlowTraceabilityService} and control {@link Transactional}. This
 * class specific extension of {@link AbstractCrudService}.
 *
 * @since 10/13/21
 */
@Log4j2
@Service
@Transactional
public class FlowTraceabilityService
    extends AbstractCrudService<FlowTraceabilityDto, FlowTraceability, Long> {

  private final FlowTraceabilityRepository flowTraceabilityRepository;
  private final FlowDocumentRepository flowDocumentRepository;
  private final FlowCampaignDetailRepository flowCampaignDetailRepository;
  private final FlowDepositRepository flowDepositRepository;
  private FlowTraceabilityDetailRepository flowTraceabilityDetailRepository;
  private StreamBridge streamBridge;
  private ProcessControlFeignClient processControlFeignClient;

  /**
   * Initialize required bean for this service.
   *
   * @param flowTraceabilityRepository refer to bean of {@link FlowTraceabilityRepository}
   * @param modelMapper refer to bean of {@link ModelMapper}
   * @param keycloakService refer to bean of {@link KeycloakService}
   * @param profileFeignClient refer to bean of {@link ProfileFeignClient}
   * @param flowDocumentRepository refer to bean of {@link FlowDocumentRepository}
   * @param flowCampaignDetailRepository refer to bean of {@link FlowCampaignDetailRepository}
   * @param flowDepositRepository refer to bean of {@link FlowDepositRepository}
   */
  @Autowired
  public FlowTraceabilityService(
      FlowTraceabilityRepository flowTraceabilityRepository,
      ModelMapper modelMapper,
      KeycloakService keycloakService,
      ProfileFeignClient profileFeignClient,
      FlowDocumentRepository flowDocumentRepository,
      FlowCampaignDetailRepository flowCampaignDetailRepository,
      FlowDepositRepository flowDepositRepository) {
    super(profileFeignClient);
    this.flowTraceabilityRepository = flowTraceabilityRepository;
    this.flowDocumentRepository = flowDocumentRepository;
    this.modelMapper = modelMapper;
    this.keycloakService = keycloakService;
    this.flowCampaignDetailRepository = flowCampaignDetailRepository;
    this.flowDepositRepository = flowDepositRepository;
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  @Autowired
  public void setStreamBridge(StreamBridge streamBridge) {
    this.streamBridge = streamBridge;
  }

  @Autowired
  @Override
  public void setRepository(SharedRepository<Long> repository) {
    super.setRepository(repository);
  }

  @Autowired
  @Override
  public void setKeycloakService(KeycloakService keycloakService) {
    super.setKeycloakService(keycloakService);
  }

  @Autowired
  public void setProcessControlFeignClient(ProcessControlFeignClient processControlFeignClient) {
    this.processControlFeignClient = processControlFeignClient;
  }

  @Autowired
  public void setFlowTraceabilityDetailRepository(
      FlowTraceabilityDetailRepository flowTraceabilityDetailRepository) {
    this.flowTraceabilityDetailRepository = flowTraceabilityDetailRepository;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  private FlowTraceability findEntity(long id) {
    return flowTraceabilityRepository
        .findById(id)
        .orElseThrow(() -> new FlowTraceabilityNotFoundException(id));
  }

  /**
   * Method used to get {@link FlowTraceabilityDto} by its id.
   *
   * @param id refer to its {@link FlowTraceability} identity.
   * @return {@link FlowTraceabilityDto} else throw exception.
   * @see FlowTraceabilityService#checkViewDetailPrivilege(long)
   * @see FlowTraceabilityService#setUnloadingDate(FlowTraceabilityDto)
   * @see FlowTraceabilityService#mapUserFullName(FlowTraceabilityDto)
   */
  @Transactional(readOnly = true)
  @Override
  public FlowTraceabilityDto findById(Long id) {

    var flowDto = this.mapData(this.findEntity(id), new FlowTraceabilityDto());

    // To check the privilege, the user can do the event view details.
    this.checkViewDetailPrivilege(flowDto.getOwnerId());

    var userDetails = PrivilegeValidationUtil.getUserDetail(flowDto.getOwnerId());
    setUnloadingDate(flowDto);
    flowDto.setService(userDetails.getServiceName());
    flowDto.setDivision(userDetails.getDivisionName());
    this.mapUserFullName(flowDto);
    return flowDto;
  }

  private void setUnloadingDate(FlowTraceabilityDto flowDto) {
    if (FlowTreatmentConstants.PORTAL_PDF.equalsIgnoreCase(flowDto.getDepositType())
        && FlowTraceabilityStatusConstant.SCHEDULED.equalsIgnoreCase(flowDto.getStatus())) {
      var unloadingDate = processControlFeignClient.getNearestClientUnloadingDate();
      flowDto.setUnloadingDate(unloadingDate);
    }
  }

  /**
   * To check the privilege, the user can do the event view details.
   *
   * @param ownerId refer to ownerId of the {@link FlowTraceability#getOwnerId()} ()}
   * @see PrivilegeValidationUtil#validateUserAccessPrivilege(String, String, boolean, long)
   */
  private void checkViewDetailPrivilege(long ownerId) {
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        CXM_FLOW_TRACEABILITY, Privilege.LIST, true, ownerId);
  }

  private void mapUserFullName(FlowTraceabilityDto dto) {
    var users = this.keycloakService.getUsers();
    dto.getFlowHistories().stream()
        .parallel()
        .forEach(
            history -> {
              var usr =
                  users.stream()
                      .parallel()
                      .filter(
                          user ->
                              history.getCreatedBy().equalsIgnoreCase(user.getUsername())
                                  || history.getCreatedBy().equalsIgnoreCase(user.getEmail()))
                      .findFirst()
                      .orElse(null);
              history.setFullName(usr);
            });
  }

  /**
   * To retrieve list of {@link FlowTraceabilityDto} with pagination by {@link SearchCriteria}.
   *
   * @param pageable refer to {@link Pageable}
   * @param filterCriteria refer to {@link FlowFilterCriteria}
   * @return list of {@link FlowTraceabilityDto} with pagination.
   * @see PrivilegeValidationUtil#validateUserAccessPrivilege(String, String, boolean, long)
   */
  @Transactional(readOnly = true)
  public Page<ListFlowTraceabilityDto> findAll(
      Pageable pageable, FlowFilterCriteria filterCriteria) {

    // get the user privilege details based on the functional key and privilege keys.
    var visibilityPrivilege =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            CXM_FLOW_TRACEABILITY, Privilege.LIST, true, true);

    var ownerIds = visibilityPrivilege.getRelatedOwners();

    if (filterCriteria.getUsers() != null && !filterCriteria.getUsers().isEmpty()) {
      ownerIds =
          ownerIds.stream()
              .parallel()
              .filter(ownerId -> filterCriteria.getUsers().contains(ownerId))
              .collect(Collectors.toUnmodifiableList());
    }

    var specification =
        Specification.where(
            FlowTraceabilitySpecification.containFlowName(filterCriteria.getFilter()));
    List<String> statusThatToIgnores = new ArrayList<>();
    statusThatToIgnores.add(FlowTraceabilityStatus.TO_FINALIZE.getValue());
    statusThatToIgnores.add(FlowTraceabilityStatus.TREATMENT.getValue());
    statusThatToIgnores.add("");
    // more statuses
    specification =
        specification
            .and(
                FlowTraceabilitySpecification.statusNotIn(
                    statusThatToIgnores)) // ignore status in the list
            .and(FlowTraceabilitySpecification.statusIn(filterCriteria.getStatus()))
            .and(FlowTraceabilitySpecification.ownerIdIn(ownerIds))
            .and(FlowTraceabilitySpecification.depositModeIn(filterCriteria.getDepositModes()))
            .and(FlowTraceabilitySpecification.subChannelIn(filterCriteria.getCategories()))
            .and(FlowTraceabilitySpecification.channelIn(filterCriteria.getChannels()))
            .and(
                FlowTraceabilitySpecification.betweenOrEqualDepositDate(
                    filterCriteria.getStartDate(), filterCriteria.getEndDate()))
            .and(FlowTraceabilitySpecification.ignoreToFinalizeCampaign())
            .and(FlowTraceabilitySpecification.isNotDelete());
    return flowTraceabilityRepository
        .findAll(Specification.where(specification), pageable)
        .map(data -> this.modelMapper.map(data, ListFlowTraceabilityDto.class));
  }

  /**
   * Method can be producers and consumers to update status of flow traceability.
   *
   * @param id id of the flow traceability
   * @param server refer to status flow traceability
   * @param status status of the flow traceability
   * @param messageProducingAction messageProducingAction is reference to client of kafka. if
   *     isProduce is true it will be producer else it will be consumer.
   * @return return {@link FlowTraceabilityDto}
   */
  @Transactional(rollbackFor = Exception.class)
  public FlowTraceabilityDto updateStatus(
      long id,
      String status,
      String server,
      String username,
      MessageProducingAction messageProducingAction) {
    var flowTraceabilityStatus = FlowTraceabilityStatus.valueOfLabel(status);
    if (flowTraceabilityStatus == null) {
      throw new StatusNotFoundException();
    }
    FlowTraceability flowTraceability;
    if (messageProducingAction == MessageProducingAction.PRODUCE) {
      flowTraceability = this.findEntity(id);
    } else {
      flowTraceability =
          flowTraceabilityRepository
              .findByFlowTraceabilityDetailsCampaignId(id)
              .orElseThrow(() -> new FlowTraceabilityNotFoundException(id));
    }

    flowTraceability.setStatus(flowTraceabilityStatus.getValue());
    flowTraceability.setDateStatus(new Date());
    flowTraceability.setLastModifiedBy(username);
    // process create flow history
    FlowHistory flowHistory = new FlowHistory();
    flowHistory.setCreatedBy(username);
    flowHistory.setEvent(flowTraceabilityStatus.getFlowHistoryStatus());
    flowHistory.setServer(server);
    flowTraceability.addFlowHistory(flowHistory);
    // end process create flow history

    if (status.equalsIgnoreCase(FlowTraceabilityStatus.CANCELED.getValue())) {
      flowDocumentRepository.updateFlowDocumentStatus(
          flowTraceability.getId(),
          FlowTraceabilityStatus.CANCELED.getValue(),
          FlowDocumentStatus.IN_ERROR.getValue());
      final List<FlowDocument> flowDocuments =
          this.flowDocumentRepository.getFlowDocumentsAndStatus(
              flowTraceability.getId(), FlowDocumentStatus.IN_ERROR.getValue());
      flowDocuments.forEach(
          flowDocument ->
              flowDocument.addFlowDocumentHistory(
                  new FlowDocumentHistory(
                      server, FlowDocumentStatus.CANCELED.getValue(), username)));
      flowDocumentRepository.saveAll(flowDocuments);
    }
    if (status.equalsIgnoreCase(FlowTraceabilityStatus.IN_PROCESS.getValue())) {
      flowDocumentRepository.updateFlowDocumentStatus(
          flowTraceability.getId(),
          FlowDocumentStatus.IN_PROGRESS.getValue(),
          FlowDocumentStatus.IN_ERROR.getValue());
    }
    return this.mapData(
        flowTraceabilityRepository.save(flowTraceability), new FlowTraceabilityDto());
  }

  /**
   * update status of flow traceability.
   *
   * @param id id of the flow traceability
   * @param server refer to status flow traceability
   * @param status status of the flow traceability
   * @return return {@link FlowTraceabilityDto}
   * @see #updateStatus(long, String, String, String, MessageProducingAction)
   */
  public FlowTraceabilityDto updateStatus(long id, String status, String server) {
    return updateStatus(id, status, server, this.getUsername(), MessageProducingAction.PRODUCE);
  }

  /**
   * update status of flow traceability.
   *
   * @param id id of the flow traceability
   * @param server refer to status flow traceability
   * @param status status of the flow traceability
   * @return return {@link FlowTraceabilityDto}
   * @see #findEntity(long) to find flow traceabilty by id.
   * @see PrivilegeValidationUtil#validateUserAccessPrivilege(String, String, boolean, long)
   */
  public FlowTraceabilityDto updateFlowStatus(long id, String status, String server) {

    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_FLOW_TRACEABILITY,
        Privilege.SubCancelFlowTraceability.FLOW,
        false,
        this.findEntity(id).getOwnerId());

    return updateStatus(id, status, server);
  }

  /**
   * load filter criteria of flow traceability.
   *
   * @return return {@link FlowFilterCriteriaDto}
   */
  public FlowFilterCriteriaDto loadFlowFilterCriteria() {
    var filterCriteria = new FlowFilterCriteriaDto();
    filterCriteria.setFlowStatus(FlowTraceabilityStatus.getFilterKeyValue());
    filterCriteria.setChannel(Channel.getKeyValue());
    filterCriteria.setDepositMode(DepositMode.getKeyValue());
    filterCriteria.setSubChannel(SubChannel.getKeyValue());
    return filterCriteria;
  }

  /**
   * Get all username of user.
   *
   * @param funcKey refer to functionality key of the profile.
   * @param privilegeKey refer to privilege key of the functionality {@code ex: Create, Update,
   *     List, Delete, and etc.}
   * @return {@link List} of {@link SharedUserEntityDTO}
   */
  public List<SharedUserEntityDTO> getUsersPrivilege(String funcKey, String privilegeKey) {
    return this.profileFeignClient.getAllUserEntities(
        this.getAuthTokenWithPrefix(), funcKey, funcKey.concat("_").concat(privilegeKey), true);
  }

  /**
   * Method used to get composedFile and step.
   *
   * @param flowTraceabilityId refer to the identity of a flow
   * @return object {@link DepositFlowInfoDto}.
   */
  public DepositFlowInfoDto getDepositFlowInfo(long flowTraceabilityId) {
    var details =
        this.flowTraceabilityRepository.findFlowTraceabilityDetailsById(flowTraceabilityId);
    return new DepositFlowInfoDto(
        details.getFlowTraceabilityDetails().getStep(),
        details.getFlowTraceabilityDetails().getComposedId(),
        details.getFlowTraceabilityDetails().isValidation());
  }

  /**
   * Method used to update composedFile and step.
   *
   * @param flowTraceabilityId refer to the identity of a flow
   * @return object {@link DepositFlowInfoDto}.
   */
  @Transactional(rollbackFor = Exception.class)
  public DepositFlowInfoDto updateDepositFlow(
      long flowTraceabilityId, int step, String composedId, boolean validation) {
    // TODO: apply the checking privilege.
    var details =
        this.flowTraceabilityRepository.findFlowTraceabilityDetailsById(flowTraceabilityId);
    var flowTraceabilityDetails = details.getFlowTraceabilityDetails();
    flowTraceabilityDetails.setStep(step);
    flowTraceabilityDetails.setComposedId(composedId);
    flowTraceabilityDetails.setValidation(validation);
    this.flowTraceabilityDetailRepository.save(flowTraceabilityDetails);
    return new DepositFlowInfoDto(
        flowTraceabilityDetails.getStep(),
        flowTraceabilityDetails.getComposedId(),
        flowTraceabilityDetails.isValidation());
  }

  /**
   * Get flow detail related to campaign.
   *
   * @param id identity of {@link FlowTraceability}
   * @return the object of {@link FlowCampaignDto}
   * @see FlowTraceabilityService#checkViewDetailPrivilege(long)
   */
  @Transactional(readOnly = true)
  public FlowCampaignDto getFlowCampaignDetailById(long id) {
    var flowCampaign =
        this.flowTraceabilityRepository
            .findFlowCampaignDetailById(id)
            .orElseThrow(
                () -> new FlowTraceabilityNotFoundException("Flow traceability is not found!"));

    // To check the privilege, the user can do the event view details.
    this.checkViewDetailPrivilege(flowCampaign.getOwnerId());
    var userDetails = PrivilegeValidationUtil.getUserDetail(flowCampaign.getOwnerId());
    var flowCampaignDto = this.modelMapper.map(flowCampaign, FlowCampaignDto.class);
    flowCampaignDto.setService(userDetails.getServiceName());
    flowCampaignDto.setDivision(userDetails.getDivisionName());
    return flowCampaignDto;
  }
}
