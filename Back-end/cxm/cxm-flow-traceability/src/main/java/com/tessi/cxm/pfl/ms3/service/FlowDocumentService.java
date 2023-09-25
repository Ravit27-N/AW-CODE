package com.tessi.cxm.pfl.ms3.service;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.tessi.cxm.pfl.ms3.dto.*;
import com.tessi.cxm.pfl.ms3.entity.*;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentDetailsNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentStatusNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingSubChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.repository.*;
import com.tessi.cxm.pfl.ms3.service.restclient.HubDigitalFlowFeignClient;
import com.tessi.cxm.pfl.ms3.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms3.service.restclient.ServiceGatewayFeignClient;
import com.tessi.cxm.pfl.ms3.service.specification.ElementAssociationSpecification;
import com.tessi.cxm.pfl.ms3.service.specification.FlowDocumentSpecification;
import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.ms3.util.I18nConstant;
import com.tessi.cxm.pfl.ms3.util.SubChannel;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.model.ProcessingResponse;
import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.shared.model.SearchCriteria;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.SharedPairValue;
import com.tessi.cxm.pfl.shared.model.SharedStatusInfoDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthResponse;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.AttachmentPositionConstant;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.ClientSettingCriteriaDistributionValidationUtil;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.GenericStatusUtils;
import com.tessi.cxm.pfl.shared.utils.Go2pdfBackgroundPositionConstant;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


/**
 * Handling business logic of {@link FlowDocument}.
 *
 * @author Piseth KHON
 * @author Sokhour LACH
 * @author Vichet CHANN
 * @author Pisey CHORN
 */
@Service
@Transactional
@Slf4j
public class FlowDocumentService extends AbstractCrudService<FlowDocumentDto, FlowDocument, Long> {

  public static final String DOC_FILTER = "Filler";
  private final FlowDocumentRepository flowDocumentRepository;
  private final FlowDocumentHistoryRepository flowDocumentHistoryRepository;
  private final FlowTraceabilityRepository flowTraceabilityRepository;
  private final FlowDocumentDetailsRepository flowDocumentDetailsRepository;
  private final HubDigitalFlowFeignClient hubDigitalFlowFeignClient;
  private final MessageSource messageSource;
  private final ServiceGatewayFeignClient serviceGatewayFeignClient;
  private final ElementAssociationRepository elementAssociationRepository;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private Environment environment;

  @Value("${prefac.url}")
  private String prefacLsApiUrl;
  private final SettingFeignClient settingFeignClient;

  /**
   * Constructor to initialize require bean by this service.
   *
   * @param flowDocumentRepository refers to bean of {@link FlowDocumentRepository}
   * @param flowDocumentHistoryRepository refers to bean of {@link FlowDocumentHistoryRepository}
   * @param keycloakService refers to the bean of {@link KeycloakService}
   * @param modelMapper refers to the bean of {@link ModelMapper}
   * @param flowTraceabilityRepository refers to the bean of {@link FlowTraceabilityRepository}
   * @param flowDocumentDetailsRepository refers to the bean of {@link
   *     FlowDocumentDetailsRepository}
   * @param hubDigitalFlowFeignClient refers to the bean of {@link HubDigitalFlowFeignClient}
   * @param messageSource refers to the bean of {@link MessageSource}
   * @param serviceGatewayFeignClient refers to the bean of {@link ServiceGatewayFeignClient}
   * @param elementAssociationRepository
   * @param environment
   */
  @Autowired
  public FlowDocumentService(
      FlowDocumentRepository flowDocumentRepository,
      FlowDocumentHistoryRepository flowDocumentHistoryRepository,
      KeycloakService keycloakService,
      ModelMapper modelMapper,
      FlowTraceabilityRepository flowTraceabilityRepository,
      FlowDocumentDetailsRepository flowDocumentDetailsRepository,
      HubDigitalFlowFeignClient hubDigitalFlowFeignClient,
      MessageSource messageSource,
      ServiceGatewayFeignClient serviceGatewayFeignClient,
      ElementAssociationRepository elementAssociationRepository,
      SettingFeignClient settingFeignClient,
      RestTemplate restTemplate,
      ObjectMapper objectMapper){
    this.hubDigitalFlowFeignClient = hubDigitalFlowFeignClient;
    this.messageSource = messageSource;
    this.serviceGatewayFeignClient = serviceGatewayFeignClient;
    this.elementAssociationRepository = elementAssociationRepository;
    super.setRepository(flowDocumentRepository);
    this.flowDocumentRepository = flowDocumentRepository;
    this.flowDocumentHistoryRepository = flowDocumentHistoryRepository;
    this.flowDocumentDetailsRepository = flowDocumentDetailsRepository;
    this.keycloakService = keycloakService;
    this.flowTraceabilityRepository = flowTraceabilityRepository;
    this.modelMapper = modelMapper;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.environment = environment;
    this.settingFeignClient = settingFeignClient;
  }

  @Autowired
  public void setProcessControlFeignClient(
      ProcessControlFeignClient processControlFeignClient) {
    this.processControlFeignClient = processControlFeignClient;
  }
  private ProcessControlFeignClient processControlFeignClient;

  @Override
  public FlowDocumentDto findById(Long id) {
    return this.mapData(this.findByEntity(id), new FlowDocumentDto());
  }

  @Override
  public List<FlowDocumentDto> findAll() {
    return flowDocumentRepository.findAll().stream()
            .map(u -> mapData(u, new FlowDocumentDto()))
            .collect(Collectors.toList());
  }

  /**
   * Method used to retrieve flow document with pageable.
   *
   * @param filterCriteria refer to {@link BaseFilterCriteria}
   * @return content of {@link FlowDocument} list converted to {@link FlowDocumentDto} wrapped by
   * {@link Page}
   */
  @Transactional(readOnly = true)
  public Page<FlowDocumentDto> findAll(Pageable pageable, BaseFilterCriteria filterCriteria) {
    var ownerIds =
            PrivilegeValidationUtil.getUserPrivilegeDetails(
                            ProfileConstants.CXM_FLOW_TRACEABILITY,
                            Privilege.FlowDocument.VIEW_DOCUMENT,
                            true,
                            true)
                    .getRelatedOwners();
    var specification =
            Specification.where(FlowDocumentSpecification.ownerIdIn(ownerIds))
                    .and(FlowDocumentSpecification.channelIn(filterCriteria.getChannels()))
                    .and(FlowDocumentSpecification.subChannelIn(filterCriteria.getCategories()))
                    .and(FlowDocumentSpecification.statusIn(filterCriteria.getStatus()))
                    .and(FlowDocumentSpecification.statusIsNotBlank())
                    .and(
                            FlowDocumentSpecification.betweenOrEqualStatusDate(
                                    filterCriteria.getStartDate(), filterCriteria.getEndDate()))
                    .and(FlowDocumentSpecification.isFlowNotDelete())
                    .and(
                            FlowDocumentSpecification.containFillers(
                                    filterCriteria.getFillers(),
                                    filterCriteria.getSearchByFiller(),
                                    this.getFillers()));

    if (!filterCriteria.getFilter().isEmpty()) {
      specification =
              specification.and(
                      FlowDocumentSpecification.containFlowNameOrDocumentName(filterCriteria.getFilter())
                              .or(FlowDocumentSpecification.containsReference(filterCriteria.getFilter())));
    }
    return flowDocumentRepository
            .findAll(specification, pageable)
            .map(e -> this.mapData(e, new FlowDocumentDto()));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public FlowDocumentDto save(FlowDocumentDto dto) {
    final var flowTraceability = this.findFlowById(dto.getFlowTraceabilityId());
    var channel = FlowDocumentChannel.valueOfLabel(dto.getChannel());
    final var status = FlowDocumentStatus.valueOfLabel(dto.getStatus());

    if (channel == null) {
      throw new SendingChannelNotFoundException();
    }
    if (SubChannel.valueOfLabel(dto.getSubChannel()) == null) {
      throw new SendingSubChannelNotFoundException("Sub Channel not found.");
    }

    String subChannel = channel.valueOfSubChannel(dto.getSubChannel());
    if (subChannel == null) {
      throw new SendingSubChannelNotFoundException();
    }
    if (status == null) {
      throw new FlowDocumentStatusNotFoundException("Status not found;");
    }

    FlowDocument entity = mapEntity(dto, new FlowDocument());

    FlowDocumentSubChannel flowDocumentSubChannel = FlowDocumentSubChannel.valueOfLabel(subChannel);
    if (!ObjectUtils.isEmpty(flowDocumentSubChannel)) {
      entity.setSubChannel(flowDocumentSubChannel.getValue());
    } else {
      entity.setSubChannel(subChannel);
    }
    entity.setChannel(channel.getValue());
    entity.setCreatedBy(this.getUsername());
    entity.setStatus(status.getValue());
    // save {@link FlowTraceability} id.
    entity.setFlowTraceability(flowTraceability);
    // process update summary flow
    this.updateFlowSummary(flowTraceability, entity);
    var flowDocument = flowDocumentRepository.save(entity);

    if (dto.getDetails() != null) {
      var flowDocumentDetails = modelMapper.map(dto.getDetails(), FlowDocumentDetails.class);
      flowDocumentDetails.setFlowDocument(flowDocument);
      flowDocumentDetailsRepository.save(flowDocumentDetails);
    }
    // process create flow document history
    var documentHistory = new FlowDocumentHistory();
    documentHistory.setFlowDocument(flowDocument);
    documentHistory.setServer(dto.getServer());
    documentHistory.setCreatedBy(this.getUsername());
    documentHistory.setEvent(status.getValue());
    flowDocumentHistoryRepository.save(documentHistory);
    // end process create flow document history
    return mapData(flowDocument, new FlowDocumentDto());
  }

  /**
   * Setter Injection.
   */
  @Autowired
  @Override
  public void setProfileFeignClient(ProfileFeignClient profileFeignClient) {
    super.setProfileFeignClient(profileFeignClient);
  }

  private FlowDocument findByEntity(long id) {
    return flowDocumentRepository
            .findById(id)
            .orElseThrow(() -> new FlowDocumentNotFoundException(id));
  }

  /**
   * Return {@link FlowTraceabilityDocument} by flow id.
   *
   * @param id refer to {@link FlowTraceability} identity.
   * @return {@link FlowTraceabilityDocument} instead of {@link FlowTraceability}.
   * @see FlowTraceabilityRepository#findFlowTraceabilityById(Long)
   * @see FlowTraceability
   * @see FlowTraceabilityDocument
   */
  private FlowTraceabilityDocument getSummaryFlow(long id) {
    return flowTraceabilityRepository.findFlowTraceabilityById(id);
  }

  /**
   * To collect of {@link FlowDocument} related to the {@link FlowTraceability}.
   *
   * @param token          refers to bearer authorization token
   * @param flowId         refers to the identity of a {@link FlowTraceability}
   * @param pageable       refers object of {@link Pageable}
   * @param filterCriteria refers to additional criteria to filter
   * @return the object of {@link FlowDocumentDto} wrapped by {@link EntityResponseHandler}
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<FlowDocumentDto> findAllByFlowId(
          String token, long flowId, Pageable pageable, BaseFilterCriteria filterCriteria) {

    // find flow traceability by id.
    var flowTraceability =
            flowTraceabilityRepository
                    .findById(flowId)
                    .orElseThrow(() -> new FlowTraceabilityNotFoundException(flowId));

    PrivilegeValidationUtil.validateUserAccessPrivilege(
            ProfileConstants.CXM_FLOW_TRACEABILITY,
            Privilege.FlowDocument.SELECT_AND_OPEN,
            true,
            flowTraceability.getOwnerId());

    return findAllByFlowId(flowId, pageable, filterCriteria);
  }

  /**
   * Get all document detail ids only.
   *
   * @param flowId         refer to {@link Long}.
   * @param pageable       refer to {@link Pageable}.
   * @param filterCriteria refer to {@link BaseFilterCriteria}.
   * @return @{@link List}<{@link Long}>.
   */
  @Transactional(readOnly = true)
  public List<Long> getDocumentDetailIds(
          long flowId, Pageable pageable, BaseFilterCriteria filterCriteria) {
    Specification<FlowDocument> specification = getDocumentSpecification(flowId, filterCriteria);
    Page<DocumentIdsProjection> documentDetails =
            flowDocumentRepository.findAll(specification, DocumentIdsProjection.class, pageable);
    return documentDetails.getContent().stream()
            .map(DocumentIdsProjection::getId)
            .collect(Collectors.toList());
  }

  /**
   * Method used to retrieve flow document with pageable by {@link FlowTraceability} id.
   *
   * @param filterCriteria refer to {@link SearchCriteria}
   * @param flowId         refer to {@link FlowTraceability} id
   * @return content of {@link FlowDocument} list converted to {@link FlowDocumentDto} wrapped by
   * {@link EntityResponseHandler}
   */
  public EntityResponseHandler<FlowDocumentDto> findAllByFlowId(
          long flowId, Pageable pageable, BaseFilterCriteria filterCriteria) {
    Specification<FlowDocument> specification = getDocumentSpecification(flowId, filterCriteria);
    Page<FlowDocumentDto> flowDocumentDtoPage =
            flowDocumentRepository
                    .findAll(Specification.where(specification), pageable)
                    .map(e -> this.mapData(e, new FlowDocumentDto()));
    return new EntityResponseHandler<>(flowDocumentDtoPage, this.getSummaryFlow(flowId));
  }

  private Specification<FlowDocument> getDocumentSpecification(
          long flowId, BaseFilterCriteria filterCriteria) {
    Specification<FlowDocument> specification =
            Specification.where(FlowDocumentSpecification.equalToFlowId(flowId))
                    .and(FlowDocumentSpecification.channelIn(filterCriteria.getChannels()))
                    .and(FlowDocumentSpecification.subChannelIn(filterCriteria.getCategories()))
                    .and(FlowDocumentSpecification.statusIn(filterCriteria.getStatus()))
                    .and(FlowDocumentSpecification.statusIsNotBlank())
                    .and(
                            FlowDocumentSpecification.betweenOrEqualStatusDate(
                                    filterCriteria.getStartDate(), filterCriteria.getEndDate()))
                    .and(
                            FlowDocumentSpecification.containFillers(
                                    filterCriteria.getFillers(),
                                    filterCriteria.getSearchByFiller(),
                                    this.getFillers()));
    if (!filterCriteria.getFilter().isEmpty()) {
      specification =
              specification.and(
                      FlowDocumentSpecification.containFlowNameOrDocumentName(filterCriteria.getFilter())
                              .or(FlowDocumentSpecification.containsReference(filterCriteria.getFilter())));
    }
    return specification;
  }

  /**
   * To get data of filter criteria of flow document.
   *
   * @return {@link FlowDocumentFilterCriteriaDto}
   */
  public FlowDocumentFilterCriteriaDto getFilterCriteria(String channel) {
    var filterCriteria = new FlowDocumentFilterCriteriaDto();
    filterCriteria.setFlowDocumentStatus(FlowDocumentStatus.getFilterKeyValue());

    filterCriteria.setSendingChannel(FlowDocumentChannel.getKeyValue());

    if (Strings.isNullOrEmpty(channel)) {
      filterCriteria.setSendingSubChannel(new ArrayList<>());
    }
    filterCriteria.setSendingSubChannel(getSubChannel(channel));

    return filterCriteria;
  }

  /**
   * To get collection of sub-channel depending on {@link FlowDocumentChannel}.
   *
   * @param channel refers to the value of {@link FlowDocumentChannel}
   * @return the collect of sub-channel
   * @see FlowDocumentChannel#getValue()
   * @see FlowDocumentChannel#getSendingSubChannel()
   */
  public List<String> getSubChannel(String channel) {
    if (Strings.isNullOrEmpty(channel)) {
      return new ArrayList<>();
    }

    var sendingChannel = FlowDocumentChannel.valueOfLabel(channel);
    if (sendingChannel == null) {
      throw new SendingChannelNotFoundException();
    }
    return sendingChannel.getSendingSubChannel();
  }

  /**
   * get sending sub-channel by value of sending channel.
   *
   * @param channel refer to value of sending channel.
   * @return value as {@link Map} with key as {@link String} and value as type
   */
  public Map<String, Object> getSubChannelByChannel(String channel) {
    if (Strings.isNullOrEmpty(channel)) {
      return Map.of(FlowTraceabilityConstant.SENDING_SUB_CHANNEL, new ArrayList<>());
    }

    var sendingChannel = FlowDocumentChannel.valueOfLabel(channel);
    if (sendingChannel == null) {
      throw new SendingChannelNotFoundException();
    }
    return Map.of(
            FlowTraceabilityConstant.SENDING_SUB_CHANNEL, sendingChannel.getSendingSubChannel());
  }

  // get Flow reference.
  private FlowTraceability findFlowById(long id) {
    return flowTraceabilityRepository
            .findById(id)
            .orElseThrow(() -> new FlowTraceabilityNotFoundException(id));
  }

  /**
   * To update summary of flow during flow-document processing.
   *
   * @param flowTraceability refers to object of {@link FlowTraceability}
   * @param flowDocument     refers to object of {@link FlowDocument}
   */
  @Transactional(rollbackFor = Exception.class)
  // update flowSummary when FlowDocument process.
  public void updateFlowSummary(FlowTraceability flowTraceability, FlowDocument flowDocument) {

    if (flowTraceability.getFlowTraceabilityDetails() == null) {
      var flowDetail = new FlowTraceabilityDetails();
      flowDetail.setFlowTraceability(flowTraceability);
      flowTraceability.setFlowTraceabilityDetails(flowDetail);
    }
    if (flowDocument.getId() == 0) {
      flowTraceability
              .getFlowTraceabilityDetails()
              .setPageProcessed(flowTraceability.getFlowTraceabilityDetails().getPageProcessed() + 1);
      flowTraceability
              .getFlowTraceabilityDetails()
              .setPageCount(
                      (int)
                              (flowTraceability.getFlowTraceabilityDetails().getPageCount()
                                      + flowDocument.getPageNumber()));
    }
    if (flowDocument.getStatus().equalsIgnoreCase(FlowDocumentStatus.IN_ERROR.getValue())) {
      flowTraceability
              .getFlowTraceabilityDetails()
              .setPageError(flowTraceability.getFlowTraceabilityDetails().getPageError() + 1);
    }
    flowTraceabilityRepository.save(flowTraceability);
  }

  /**
   * To retrieve details of {@link FlowDocument}.
   *
   * @param id id of {@link FlowDocument}
   * @return object details of {@link LoadFlowDocumentDetailsDto}
   */
  @Transactional(readOnly = true)
  public LoadFlowDocumentDetailsDto getFlowDocumentDetailsById(long id) {
    var objectDetails =
            flowDocumentDetailsRepository
                    .findById(id)
                    .orElseThrow(() -> new FlowDocumentDetailsNotFoundException(id));

    var flowDocumentDetails =
            modelMapper.map(objectDetails.getFlowDocument(), LoadFlowDocumentDetailsDto.class);

    if (FlowDocumentChannel.POSTAL.getValue().equalsIgnoreCase(flowDocumentDetails.getChannel())
        && FlowTraceabilityStatusConstant.SCHEDULED.equalsIgnoreCase(
            flowDocumentDetails.getStatus())) {
      var unloadingDate = processControlFeignClient.getNearestClientUnloadingDate();
      flowDocumentDetails.setUnloadingDate(unloadingDate);
    }

    var flowDetails =
            flowTraceabilityRepository
                    .findFlowTraceabilityDetailsById(flowDocumentDetails.getFlowTraceabilityId())
                    .getFlowTraceabilityDetails();

    var ownerId = flowDetails.getFlowTraceability().getOwnerId();

    // check user permission can read flow document details or not.
    PrivilegeValidationUtil.validateUserAccessPrivilege(
            ProfileConstants.CXM_FLOW_TRACEABILITY,
            Privilege.FlowDocument.SELECT_AND_OPEN,
            true,
            ownerId);

    flowDocumentDetails.setElementAssociations(this.getElementAssociation(id));

    var details = getFlowDocumentDetails(objectDetails);

    details.setOwnerId(ownerId);
    details.setProductCriteria(flowDocumentDetails.getChannel());
    details.setCampaignName(flowDetails.getCampaignName());
    details.setCreatedBy(objectDetails.getFlowDocument().getCreatedBy());
    details.setModelName(objectDetails.getFlowDocument().getFlowTraceability().getModelName());

    if (FlowDocumentChannelConstant.POSTAL.equals(objectDetails.getFlowDocument().getChannel())) {
      String token = ClientSettingCriteriaDistributionValidationUtil.getBearerToken();
      ProcessingResponse enrichmentResponse = this.getEnrichment(objectDetails, token);
      if (enrichmentResponse.containResource()) {
        details.setEnrichment(enrichmentResponse);
      }
    }

    flowDocumentDetails.setDetails(details);
    this.mapUserFullName(flowDocumentDetails);
    return flowDocumentDetails;
  }

  private void mapUserFullName(LoadFlowDocumentDetailsDto dto) {
    var users = this.keycloakService.getUsers();
    dto.getHistories().stream()
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

  private FlowDocumentDetailsDto getFlowDocumentDetails(FlowDocumentDetails flowDocumentDetails) {
    return FlowDocumentDetailsDto.builder()
        .id(flowDocumentDetails.getId())
        .flowName(flowDocumentDetails.getFlowDocument().getFlowTraceability().getFlowName())
        .address(flowDocumentDetails.getAddress())
        .email(flowDocumentDetails.getEmail())
        .telephone(flowDocumentDetails.getTelephone())
        .reference(flowDocumentDetails.getReference())
        .fillers(this.mappingFillers(flowDocumentDetails.getFillers()))
        .docName(flowDocumentDetails.getDocName())
        .archiving(flowDocumentDetails.getArchiving())
        .addition(flowDocumentDetails.getAddition())
        .postage(flowDocumentDetails.getPostage())
        .watermark(flowDocumentDetails.getWatermark())
        .color(flowDocumentDetails.getColor())
        .envelope(flowDocumentDetails.getEnvelope())
        .impression(flowDocumentDetails.getImpression())
        .postalPickup(flowDocumentDetails.getPostalPickup())
        .subChannel(flowDocumentDetails.getFlowDocument().getSubChannel())
        .build();
  }

  /**
   * To get all client filler configured from the <strong>Profile APP</strong>.
   *
   * @return collection {@link List} of the {@link SharedClientFillersDTO}.
   */
  public List<SharedClientFillersDTO> getAllClientFillers() {
    return this.profileFeignClient.getAllClientFillers(this.getAuthTokenWithPrefix()).stream()
            .sorted(Comparator.comparing(SharedClientFillersDTO::getKey))
            .collect(Collectors.toList());
  }

  /**
   * To mapping data of client fillers with the value of flow-document fillers.
   *
   * @param fillers refer to collection {@link List} of the {@link String} that select from the
   *                table of the <strong>Flow-document</strong>
   * @return return mapped object of {@link List} of {@link SharedPairValue}
   */
  private List<FlowDocumentFiller> mappingFillers(String[] fillers) {
    List<FlowDocumentFiller> pairFillers = new ArrayList<>();
    var clientFillers = this.getAllClientFillers();
    if (clientFillers.isEmpty()) {
      return new ArrayList<>();
    }

    IntStream.range(0, clientFillers.size())
            .forEach(
                    idx -> {
                      var filler = clientFillers.get(idx);
                      int idxFiller = Integer.parseInt(StringUtils.getDigits(filler.getKey()));
                      pairFillers.add(
                              new FlowDocumentFiller(
                                      org.springframework.util.StringUtils.hasText(filler.getValue())
                                              ? filler.getValue()
                                              : this.getFillerLabel(filler.getKey()),
                                      ArrayUtils.isEmpty(fillers) ? "" : fillers[idxFiller - 1],
                                      (idx + 1)));
                    });
    return pairFillers;
  }

  /**
   * To get all client filler configured from the <strong>Profile APP</strong>.
   *
   * @return collection {@link List} of the {@link SharedClientFillersDTO}.
   */
  public List<FlowDocumentFiller> getFlowDocumentFillers() {
    var fillers = this.getAllClientFillers();
    List<FlowDocumentFiller> result = new ArrayList<>();
    IntStream.range(0, fillers.size())
            .forEach(
                    index -> {
                      var filler = fillers.get(index);
                      result.add(
                              new FlowDocumentFiller(
                                      filler.getKey(),
                                      org.springframework.util.StringUtils.hasText(filler.getValue())
                                              ? filler.getValue()
                                              : this.getFillerLabel(filler.getKey()),
                                      (index + 1)));
                    });
    return result;
  }

  private List<String> getFillers() {
    return this.getAllClientFillers().stream()
            .map(SharedClientFillersDTO::getKey)
            .collect(Collectors.toList());
  }

  /**
   * To get label of the filler.
   *
   * <pre>
   *   example: Filler1 -> Filler 1
   * </pre>
   *
   * @param key refer to key of the filler that retrieve from the <strong>Profile APP</strong>
   * @return {@link String} value that split.
   */
  private String getFillerLabel(String key) {
    if (StringUtils.isEmpty(key)) {
      return "";
    }
    int numFiller = Integer.parseInt(StringUtils.getDigits(key));
    return DOC_FILTER.concat(" ".concat(String.valueOf(numFiller)));
  }

  public SharedStatusInfoDto getStatusInfo(Long id, String locale) {
    final FlowDocumentDetails fdd =
            flowDocumentDetailsRepository
                    .findById(id)
                    .orElseThrow(() -> new FlowDocumentDetailsNotFoundException(id));
    final String jobUuid = fdd.getFlowDocument().getHubIdDoc();

    SharedStatusInfoDto sharedStatusInfoDto = SharedStatusInfoDto.builder().build();

    if (jobUuid == null) {
      sharedStatusInfoDto.setDescription(messageSource.getMessage(
              I18nConstant.HUB_DOCUMENT_ID_DOES_NOT_EXIST, new Object[0],
              "", new Locale(locale)));
      return sharedStatusInfoDto;
    } else {
      final List<String> fdhEvents = fdd.getFlowDocument().getFlowDocumentHistories().stream().map(
              BaseHistoryEvent::getEvent).collect(Collectors.toList());
      final List<String> statusEvents = List.of(GenericStatusUtils.HARD_BOUNCE,
              GenericStatusUtils.IN_ERROR, GenericStatusUtils.BLOCKED, GenericStatusUtils.SOFT_BOUNCE);

      if (fdhEvents.stream().noneMatch(statusEvents::contains)) {
        sharedStatusInfoDto.setDescription(messageSource.getMessage(
                I18nConstant.ERROR_STATUS_FROM_RETARUS_DOES_NOT_FOUND, new Object[0],
                "", new Locale(locale)));
        return sharedStatusInfoDto;
      }
    }

    if (!this.serviceGatewayFeignClient.isServiceAvailable(FeignClientConstants.CXM_HUB_DIGITALFLOW,
            this.getAuthTokenWithPrefix())) {
      sharedStatusInfoDto.setDescription(messageSource.getMessage(
              I18nConstant.SERVICE_HUB_NOT_READY_TO_START,
              new Object[]{FeignClientConstants.CXM_HUB_DIGITALFLOW},
              "", new Locale(locale)));

      return sharedStatusInfoDto;
    }

    final String token = getUserHubToken(getAuthTokenWithPrefix());
    try {
      return hubDigitalFlowFeignClient.getStatusInfo(jobUuid, locale, token);
    } catch (Exception e) {
      sharedStatusInfoDto.setDescription(messageSource.getMessage(
              I18nConstant.SERVICE_HUB_NOT_READY_TO_START,
              new Object[]{FeignClientConstants.CXM_HUB_DIGITALFLOW},
              "", new Locale(locale)));
    }

    return sharedStatusInfoDto;
  }

  private String getUserHubToken(String token) {
    final UserHubAccount userHubInfo = profileFeignClient.getUserHubAccount(token);

    final AuthResponse hubToken =
            hubDigitalFlowFeignClient.getAuthToken(
                    new AuthRequest(userHubInfo.getUsername(), userHubInfo.getPassword()));
    return BearerAuthentication.PREFIX_TOKEN.concat(hubToken.getToken());
  }

  private Set<ElementAssociationDto> getElementAssociation(long documentId) {
    return this.elementAssociationRepository
            .findAll(
                    Specification.where(ElementAssociationSpecification.containFlowDocumentId(documentId)))
            .stream()
            .map(elementAssociation -> modelMapper.map(elementAssociation, ElementAssociationDto.class))
            .collect(Collectors.toSet());
  }

  public void updateDataBaseFromCsvFile(MultipartFile csvFile) throws IOException {

    try {
      log.info("Le service est appelé !");

      // Créer un fichier temporaire à partir du contenu du MultipartFile
      File tempFile = File.createTempFile("temp", ".csv");
      csvFile.transferTo(tempFile);

      // Lire les lignes du fichier CSV temporaire
      List<Long> documentIds = Files.lines(tempFile.toPath())
              .skip(1)
              .map(String::trim)
              .map(Long::parseLong)
              .collect(Collectors.toList());

      //flowDocumentRepository.updateStatusDocuments(documentIds);
      System.out.println(documentIds);

      // Supprimer le fichier temporaire
      tempFile.delete();
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * get ProcessingResponse from FlowDocumentDetails
   * if all data is blank return null
   *
   * @param flowDocumentDetails
   * @param token
   * @return
   */
  private ProcessingResponse getEnrichment(FlowDocumentDetails flowDocumentDetails, String token) {
    ProcessingResponse enrichment = new ProcessingResponse();

    enrichment.setWatermark(
        StringUtils.isBlank(flowDocumentDetails.getWatermark())
            ? null
            : flowDocumentDetails.getWatermark());

    enrichment.setSignature(
        StringUtils.isBlank(flowDocumentDetails.getSignature())
            ? null
            : this.getFileNameResource(flowDocumentDetails.getSignature(), token));

    BackgroundPage backgroundPage =
        this.getBackgroundPage(flowDocumentDetails.getFlowDocumentBackgrounds(), token);

    Attachments attachments =
        this.getAttachmentPage(flowDocumentDetails.getFlowDocumentAttachments(), token);

    enrichment.setAttachments(attachments);
    enrichment.setBackgroundPage(backgroundPage);

    return enrichment;
  }

  /**
   * get BackgroundPage from list FlowDocumentBackground
   *
   * @param flowDocumentBackgrounds
   * @param token
   * @return
   */
  private BackgroundPage getBackgroundPage(
      List<FlowDocumentBackground> flowDocumentBackgrounds, String token) {
    BackgroundPage backgroundPage = new BackgroundPage();

    if (flowDocumentBackgrounds.isEmpty()) {
      return null;
    }

    flowDocumentBackgrounds.forEach(
        flowDocumentBackground -> {
          // split file name
          String fileName = FilenameUtils.getName(flowDocumentBackground.getBackground());
          // get file name from resource
          fileName = this.getFileNameResource(fileName, token);

          if (Go2pdfBackgroundPositionConstant.ALL.equals(flowDocumentBackground.getPosition())) {
            backgroundPage.setBackground(fileName);
            backgroundPage.setPosition(BackgroundPosition.ALL_PAGES.value);
          } else {
            switch (flowDocumentBackground.getPosition()) {
              case Go2pdfBackgroundPositionConstant.FIRST:
                backgroundPage.setBackgroundFirst(fileName);
                backgroundPage.setPositionFirst(BackgroundPosition.FIRST_PAGE.value);
                break;
              case Go2pdfBackgroundPositionConstant.NEXT:
                backgroundPage.setBackground(fileName);
                backgroundPage.setPosition(BackgroundPosition.NEXT_PAGES.value);
                break;
              case Go2pdfBackgroundPositionConstant.LAST:
                backgroundPage.setBackgroundLast(fileName);
                backgroundPage.setPositionLast(BackgroundPosition.LAST_PAGE.value);
                break;
              default:
                break;
            }
          }
        });
    return backgroundPage;
  }

  /**
   * get Attachments from list flowDocumentAttachment
   *
   * @param flowDocumentAttachments
   * @param token
   * @return
   */
  private Attachments getAttachmentPage(
      List<FlowDocumentAttachment> flowDocumentAttachments, String token) {
    Attachments attachments = new Attachments();

    if (flowDocumentAttachments.isEmpty()) {
      return null;
    }

    flowDocumentAttachments.forEach(
        flowDocumentAttachment -> {

          // split file name
          String fileName = FilenameUtils.getName(flowDocumentAttachment.getAttachment());
          // get file name from resource
          fileName = this.getFileNameResource(fileName, token);

          switch (flowDocumentAttachment.getPosition()) {
            case AttachmentPositionConstant.FIRST_POSITION:
              attachments.setAttachment1(fileName);
              break;
            case AttachmentPositionConstant.SECOND_POSITION:
              attachments.setAttachment2(fileName);
              break;
            case AttachmentPositionConstant.THIRD_POSITION:
              attachments.setAttachment3(fileName);
              break;
            case AttachmentPositionConstant.FOURTH_POSITION:
              attachments.setAttachment4(fileName);
              break;
            case AttachmentPositionConstant.FIFTH_POSITION:
              attachments.setAttachment5(fileName);
              break;
            default:
              break;
          }
        });

    return attachments;
  }

  /**
   * get file name from resource not found return uuid.pdf
   *
   * @param filename
   * @param token
   * @return
   */
  private String getFileNameResource(String filename, String token) {
    try {
      String uuid = FilenameUtils.getBaseName(filename);
      ResourceLibraryDto resourceLibraryDto =
          this.settingFeignClient.findResourceByFileId(uuid, token);
      return FilenameUtils.getBaseName(resourceLibraryDto.getFileName());
    } catch (Exception e) {
      return filename;
    }
  }

  /**
   * Check enrichment contain resource
   *
   * @param enrichment
   * @return
   */

}
















