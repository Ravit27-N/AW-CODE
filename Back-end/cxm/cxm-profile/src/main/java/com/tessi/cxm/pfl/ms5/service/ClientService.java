package com.tessi.cxm.pfl.ms5.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.dto.ClientCriteriaDto;
import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.dto.ClientServiceDetailsDTO;
import com.tessi.cxm.pfl.ms5.dto.ClientServiceDetailsDTO.ClientResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.ClientUnloadDetails;
import com.tessi.cxm.pfl.ms5.dto.ClientUnloadingDto;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.dto.FunctionalitiesReference;
import com.tessi.cxm.pfl.ms5.dto.LoadClient;
import com.tessi.cxm.pfl.ms5.dto.ProfileDetailDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.dto.PublicHolidayDto;
import com.tessi.cxm.pfl.ms5.dto.SubFunctionalitiesReference;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.ClientAllowUnloading;
import com.tessi.cxm.pfl.ms5.entity.ClientFillers;
import com.tessi.cxm.pfl.ms5.entity.ClientFunctionalitiesDetails;
import com.tessi.cxm.pfl.ms5.entity.ClientUnloading;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.Functionalities;
import com.tessi.cxm.pfl.ms5.entity.ReturnAddress;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.projection.ClientInfo;
import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserPrivilegeDetails;
import com.tessi.cxm.pfl.ms5.exception.ClientFillerKeysNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.ClientNameConflictException;
import com.tessi.cxm.pfl.ms5.exception.ClientNameNotModifiableException;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.ClientSettingJDBCException;
import com.tessi.cxm.pfl.ms5.exception.DepartmentConflictNameException;
import com.tessi.cxm.pfl.ms5.exception.DepartmentNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNameConflictException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.FormatTimeNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalitiesNotFound;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityNotModifiableException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityRequiredException;
import com.tessi.cxm.pfl.ms5.exception.INIConfigurationFileNotAcceptable;
import com.tessi.cxm.pfl.ms5.exception.NotRegisteredServiceUserException;
import com.tessi.cxm.pfl.ms5.exception.PortalConfigurationFailureException;
import com.tessi.cxm.pfl.ms5.exception.PortalSettingConfigFailureException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotBelongToServiceException;
import com.tessi.cxm.pfl.ms5.repository.ClientFillersRepository;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DivisionRepository;
import com.tessi.cxm.pfl.ms5.repository.FunctionalitiesRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileDetailsRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.restclient.CampaignFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.FlowFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.TemplateFeignClient;
import com.tessi.cxm.pfl.ms5.service.specification.ClientSpecification;
import com.tessi.cxm.pfl.ms5.service.specification.DivisionSpecification;
import com.tessi.cxm.pfl.ms5.service.specification.UserSpecification;
import com.tessi.cxm.pfl.ms5.util.HolidayCalculator;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersion;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersionDto;
import com.tessi.cxm.pfl.shared.model.ProfileClientSettingRequest;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDTO;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import com.tessi.cxm.pfl.shared.utils.DepositMode;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import feign.FeignException.FeignClientException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@Transactional
public class ClientService extends AbstractCrudService<ClientDto, Client, Long>
    implements AdminService, SharedService {

  private static final String CLIENT = "client";
  private final ClientRepository clientRepository;
  private final UserRepository userRepository;
  private final CampaignFeignClient campaignFeignClient;
  private final TemplateFeignClient templateFeignClient;
  private final FlowFeignClient flowFeignClient;
  private ProfileService profileService;
  private FileManagerResource fileManagerResource;
  private DivisionRepository divisionRepository;
  private UserProfileRepository userProfileRepository;
  private FunctionalitiesRepository functionalitiesRepository;
  private ProfileDetailsRepository profileDetailsRepository;
  private ProcessControlFeignClient processControlFeignClient;
  private ObjectMapper objectMapper;
  private ClientFillersRepository clientFillersRepository;
  private SettingFeignClient settingFeignClient;
  private ReturnAddressService returnAddressService;

  @Value("${cxm.user.admin-id}")
  private String adminUserId;

  public ClientService(
      ClientRepository clientRepository,
      UserRepository userRepository,
      ModelMapper modelMapper,
      KeycloakService keycloakService,
      CampaignFeignClient campaignFeignClient,
      TemplateFeignClient templateFeignClient,
      FlowFeignClient flowFeignClient) {
    this.clientRepository = clientRepository;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.setKeycloakService(keycloakService);
    this.campaignFeignClient = campaignFeignClient;
    this.templateFeignClient = templateFeignClient;
    this.flowFeignClient = flowFeignClient;
  }

  @Autowired
  public void setReturnAddressService(ReturnAddressService returnAddressService) {
    this.returnAddressService = returnAddressService;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Autowired
  public void setFunctionalitiesRepository(FunctionalitiesRepository functionalitiesRepository) {
    this.functionalitiesRepository = functionalitiesRepository;
  }

  @Autowired
  public void setProfileService(ProfileService profileService) {
    this.profileService = profileService;
  }

  @Autowired
  public void setFileManagerResource(FileManagerResource fileManagerResource) {
    this.fileManagerResource = fileManagerResource;
  }

  @Autowired
  public void setDivisionRepository(DivisionRepository divisionRepository) {
    this.divisionRepository = divisionRepository;
  }

  @Autowired
  public void setUserProfileRepository(UserProfileRepository userProfileRepository) {
    this.userProfileRepository = userProfileRepository;
  }

  @Autowired
  public void setProfileDetailsRepository(ProfileDetailsRepository profileDetailsRepository) {
    this.profileDetailsRepository = profileDetailsRepository;
  }

  @Autowired
  public void setProcessControlFeignClient(ProcessControlFeignClient processControlFeignClient) {
    this.processControlFeignClient = processControlFeignClient;
  }

  @Autowired
  public void setClientFillersRepository(ClientFillersRepository clientFillersRepository) {
    this.clientFillersRepository = clientFillersRepository;
  }

  @Autowired
  public void setSettingFeignClient(SettingFeignClient settingFeignClient) {
    this.settingFeignClient = settingFeignClient;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }

  @Transactional(readOnly = true)
  public Client findEntity(long id) {
    return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
  }

  @Override
  public ClientDto findById(Long id) {
    boolean isAdmin = true;
    if (!this.isAdmin()) {
      if (profileService.notContainsPrivilege(
          ProfileConstants.CXM_CLIENT_MANAGEMENT,
          ProfileConstants.CXM_CLIENT_MANAGEMENT.concat("_").concat(Privilege.MODIFY))) {
        throw new UserAccessDeniedExceptionHandler();
      }
      isAdmin = false;
    }
    var entity = this.getClient(id);
    var client = this.mapping(entity, new ClientDto());
    client.setFunctionalities(
        this.getFunctionalitiesByClientId(id).stream()
            .map(Functionalities::getKey)
            .collect(Collectors.toSet()));

    var holidays = entity.getClientAllowUnloads().stream()
        .filter(Objects::nonNull)
        .map(ClientAllowUnloading::getHolidayId)
        .collect(Collectors.toSet());
    client.setPublicHolidays(holidays);
    client.setDepositModes(this.loadDepositModes(client.getName()));
    if (isAdmin) {
      client.setPortalConfigEnable(this.getClientPortSettingConfig(entity.getName()));
      client.setCriteriaDistributions(this.getCriteriaDistributions(entity.getName()));
    }
    client.setFillers(getClientFiller(client));

    List<ReturnAddress> addressList = this.returnAddressService.getReturnAddress(client.getId());
    this.mapClientReturnAddress(client, addressList);
    return client;
  }

  private void mapClientReturnAddress(ClientDto clientDto, List<ReturnAddress> addressList) {
    if (CollectionUtils.isEmpty(addressList)) {
      return;
    }
    Map<AddressType, List<ReturnAddress>> returnAddressMap =
        addressList.stream().collect(Collectors.groupingBy(ReturnAddress::getType));
    var clientAddress = returnAddressMap.get(AddressType.CLIENT);

    //    Map object address to client itself.
    if (!CollectionUtils.isEmpty(clientAddress)) {
      clientAddress.stream()
          .filter(returnAddress -> returnAddress.getRefId() == clientDto.getId())
          .findFirst()
          .ifPresent(
              returnAddress -> {
                var addressDto = this.modelMapper.map(returnAddress, AddressDto.class);
                clientDto.setAddress(addressDto);
              });
    }
    var divisionReturnAddresses =
        ObjectUtils.defaultIfNull(
                returnAddressMap.get(AddressType.DIVISION), new ArrayList<ReturnAddress>())
            .stream()
            .collect(Collectors.toMap(ReturnAddress::getRefId, returnAddress -> returnAddress));
    var serviceReturnAddresses =
        ObjectUtils.defaultIfNull(
                returnAddressMap.get(AddressType.SERVICE), new ArrayList<ReturnAddress>())
            .stream()
            .collect(Collectors.toMap(ReturnAddress::getRefId, returnAddress -> returnAddress));
    // Map object address to client division level.
    clientDto
        .getDivisions()
        .forEach(
            division -> {
              var divisionReturnAddress = divisionReturnAddresses.get(division.getId());
              if (divisionReturnAddress != null) {
                var addressDto = this.modelMapper.map(divisionReturnAddress, AddressDto.class);
                division.setAddress(addressDto);
              }
              // Map object address to client division level.
              var services = division.getServices();
              if (!services.isEmpty()) {
                services.forEach(
                    service -> {
                      var serviceReturnAddress = serviceReturnAddresses.get(service.getId());
                      if (serviceReturnAddress != null) {
                        var addressDto =
                            this.modelMapper.map(serviceReturnAddress, AddressDto.class);
                        service.setAddress(addressDto);
                      }
                    });
              }
            });
  }

  /**
   * Get client filler DTO.
   *
   * @param client refer to object {@link ClientDto}
   * @return @{@link List}<{@link SharedClientFillersDTO}>
   */
  private List<SharedClientFillersDTO> getClientFiller(ClientDto client) {
    List<String> defaultFillerKeys = List.of("Filler1", "Filler2", "Filler3", "Filler4", "Filler5");
    List<SharedClientFillersDTO> clientFillersDTOS = new ArrayList<>();
    defaultFillerKeys.forEach(
        key -> {
          Optional<SharedClientFillersDTO> fillerDto = client.getFillers().stream()
              .filter(filler -> key.equalsIgnoreCase(filler.getKey()))
              .findFirst();
          if (fillerDto.isPresent()) {
            clientFillersDTOS.add(fillerDto.get());
          } else {
            clientFillersDTOS.add(
                SharedClientFillersDTO.builder()
                    .id(null)
                    .key(key)
                    .value("")
                    .enabled(false)
                    .build());
          }
        });
    return clientFillersDTOS;
  }

  private boolean getClientPortSettingConfig(String clientName) {
    try {
      return this.settingFeignClient.getPortalSettingConfig(clientName,
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()))
          .isActive();
    } catch (FeignClientException e) {
      throw new ClientSettingJDBCException("Fail to get client portal setting configuration");
    }
  }

  /**
   * Get deposit type from cxm-setting by using feignClient.
   *
   * @param customer - value of {@link String}.
   * @return - collection of {@link CustomerDepositModeDto}
   */
  private List<CustomerDepositModeDto> loadDepositModes(String customer) {
    try {
      return this.settingFeignClient.getDepositModes(customer);
    } catch (FeignClientException e) {
      log.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  @Transactional(readOnly = true)
  public Page<LoadClient> loadAllClients(Pageable pageable, String filter) {
    var specification = Specification.where(ClientSpecification.containFilter(filter));

    if (this.isAdmin()) {
      return this.clientRepository.findAll(specification, ClientInfo.class, "ClientList",
          EntityGraphType.FETCH, pageable)
          .map(client -> this.modelMapper.map(client, LoadClient.class));
    }

    if (profileService.notContainsPrivilege(
        ProfileConstants.CXM_CLIENT_MANAGEMENT,
        ProfileConstants.CXM_CLIENT_MANAGEMENT.concat("_").concat(Privilege.LIST))) {
      throw new UserAccessDeniedExceptionHandler();
    } else {
      specification = specification.and(
          ClientSpecification.byUserTechnicalRefAndDeletedFalse(getPrincipalIdentifier()));
      return this.clientRepository.findAll(specification, ClientInfo.class, "ClientList",
          EntityGraphType.FETCH, pageable)
          .map(client -> this.modelMapper.map(client, LoadClient.class));
    }
  }

  protected String getPrincipalIdentifier() {
    return AuthenticationUtils.getPrincipalIdentifier();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ClientDto save(ClientDto dto) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    if (this.validateDuplicateName(0, dto.getName())) {
      throw new ClientNameConflictException(dto.getName());
    }
    // check functionalities field.
    this.functionalitiesRequired(dto.getFunctionalities());

    var clientFunctionalitiesDetails = this.getClientFunctionalitiesDetails(new ArrayList<>(dto.getFunctionalities()));
    this.createAndUpdateClientSetting(
        new ArrayList<>(dto.getFunctionalities()), dto.getName());

    this.validateDuplicate(dto.getDivisions());

    // nothing to do on the action create the client.
    dto.setPublicHolidays(new HashSet<>());
    dto.setUnloads(new ArrayList<>());
    dto.setFillers(new ArrayList<>());

    var entity = this.mapEntity(dto, new Client());
    entity.addDivisions(this.mappingDataRequest(dto));
    entity.addClientFunctionalityDetails(clientFunctionalitiesDetails);
    entity = this.clientRepository.save(entity);

    // Save addresses.
    List<ReturnAddress> addressResponses = this.returnAddressService.saveAll(dto, entity);

    var defaultFuncKeys = new ArrayList<>(
        Set.of(ProfileConstants.CXM_CLIENT_MANAGEMENT, ProfileConstants.CXM_USER_MANAGEMENT));
    var funcDefaultProfile = dto.getFunctionalities().stream()
        .filter(defaultFuncKeys::contains)
        .collect(Collectors.toList());

    if (!funcDefaultProfile.isEmpty()) {
      this.createDefaultProfileOfClient(entity, funcDefaultProfile);
    }

    var responseDto = this.mapping(entity, new ClientDto());
    if (!CollectionUtils.isEmpty(addressResponses)) {
      this.mapClientReturnAddress(responseDto, addressResponses);
    }
    return responseDto;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ClientDto update(ClientDto dto) {
    this.validateOnUpdateClient(dto);

    var entity = this.getClient(dto.getId());

    // check the name of client is modified or not.
    if (!entity.getName().equalsIgnoreCase(dto.getName())) {
      throw new ClientNameNotModifiableException();
    }

    // validate duplicate division and service.
    this.validateDuplicate(dto.getDivisions());

    // validate and check the filler keys of the client.
    this.validateClientFillerKeys(dto.getFillers());

    // Capture fileId from the cxm-file-manager.
    final var refFileId = entity.getFileId();

    var divisionDtoIds = dto.getDivisions().stream()
        .map(DivisionDto::getId)
        .filter(id -> id != 0)
        .collect(Collectors.toList());
    var divisionEntityIds = entity.getDivisions().stream().map(Division::getId).collect(Collectors.toList());
    var serviceDtoIds = this.getServiceIds(dto);
    @SuppressWarnings("unchecked")
    List<Long> divisionRemoved = ListUtils.removeAll(divisionEntityIds, divisionDtoIds);
    var serviceIdsEntity = this.getServiceIds(entity, divisionRemoved);
    @SuppressWarnings("unchecked")
    List<Long> serviceRemoved = ListUtils.removeAll(serviceIdsEntity, serviceDtoIds);

    // Address remove.
    Map<Long, AddressType> addressRemoved = this.filterAddressRemove(serviceRemoved,
        divisionRemoved, dto, entity);

    // validate existing ids for dto requested.
    this.validateExistingDivisionId(divisionDtoIds, divisionEntityIds);
    this.validateExistingServiceId(serviceDtoIds, serviceIdsEntity);

    // clear data of the users from the database and Keycloak server.
    final List<String> deletedUserIds = clearUserDataRelatedClient(serviceRemoved, divisionRemoved);
    final var isAdmin = isAdmin();

    if (!CollectionUtils.isEmpty(dto.getFunctionalities())) {
      // delete functionalities of profile details
      List<String> existsFuncKeys = this.deleteProfileDetails(entity.getId(),
          new ArrayList<>(dto.getFunctionalities()));

      entity.addClientFunctionalityDetails(
          getClientFunctionalitiesDetails(new ArrayList<>(dto.getFunctionalities())));
      if (isAdmin) {
        createAndUpdateClientSetting(new ArrayList<>(dto.getFunctionalities()), dto.getName());
      }
    }

    // mapping data of the client from DTO to entity.
    mappedDtoToEntity(dto, entity);

    var response = clientRepository.save(entity);

    // Update addresses.
    var addressResponses = this.returnAddressService.updateAll(dto, response,
        addressRemoved);

    List<CustomerDepositModeDto> depositModes = new ArrayList<>();
    // Create or update client setting.
    if (isAdmin) {
      depositModes = createClientSetting(entity.getName(), dto.getDepositModes());
    }
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_COMMITTED) {
              final var authToken = ClientService.this.getAuthTokenWithPrefix();
              if (!deletedUserIds.isEmpty()) {
                ClientService.this.deleteKeyCloakUsers(deletedUserIds);
              }
              if (isAdmin && StringUtils.hasText(refFileId) && !refFileId.equals(dto.getFileId())) {
                ClientService.this.deleteFile(refFileId, authToken);
              }

              // Mapping Unload request and request to process control to schedule the
              // unloading process
              var unloadDetails = ClientService.this.getClientUnloads(dto.getId());
              var clientUnloadDetails = ClientService.this.modelMapper.map(
                  unloadDetails,
                  SharedClientUnloadDetailsDTO.class);

              ClientService.this.processControlFeignClient.produceClientUnloads(clientUnloadDetails,
                  authToken);
            }
          }
        });

    var clientDto = this.mapping(response, new ClientDto());
    if (isAdmin) {
      clientDto.setDepositModes(depositModes);
      // set PortalConfigEnable value.
      depositModes.stream()
          .filter(
              customerDepositModeDto ->
                  customerDepositModeDto.getValue().equals(DepositMode.PORTAL.getValue()))
          .findFirst()
          .ifPresent(
              customerDepositModeDto ->
                  clientDto.setPortalConfigEnable(customerDepositModeDto.isScanActivation()));
    }
    if (!CollectionUtils.isEmpty(addressResponses)) {
      this.mapClientReturnAddress(clientDto, addressResponses);
    }
    return clientDto;
  }

  private Map<Long, AddressType> filterAddressRemove(List<Long> serviceRemoved,
      List<Long> divisionRemoved, ClientDto clientDto, Client clientEntity) {
    Map<Long, AddressType> addressRemoved = new HashMap<>();
    if (clientDto.getAddress() == null) {
      addressRemoved.put(clientEntity.getId(), AddressType.CLIENT);
    }
    clientDto.getDivisions().forEach(divisionDto -> {
      if (divisionDto.getAddress() == null) {
        addressRemoved.put(divisionDto.getId(), AddressType.DIVISION);
      }
      divisionDto.getServices().forEach(departmentDto -> {
        if (departmentDto.getAddress() == null) {
          addressRemoved.put(departmentDto.getId(), AddressType.SERVICE);
        }
      });
    });

    if (divisionRemoved.isEmpty()) {
      serviceRemoved.forEach(serviceId -> addressRemoved.put(serviceId, AddressType.SERVICE));
    } else {
      divisionRemoved.forEach(divisionId -> addressRemoved.put(divisionId, AddressType.DIVISION));
      List<Long> serviceIdsRemoved = new ArrayList<>();
      var divisionEntity = divisionRepository.findAll(DivisionSpecification.idIn(divisionRemoved));
      divisionEntity.forEach(
          division -> serviceIdsRemoved.addAll(
              division.getDepartments().stream()
                  .map(Department::getId)
                  .collect(Collectors.toList())));
      serviceIdsRemoved.forEach(serviceId -> addressRemoved.put(serviceId, AddressType.SERVICE));
    }
    return addressRemoved;
  }

  /**
   * Method used to create or modified client setting by using feignClient.
   *
   * @param customer     - customer name {@link String}.
   * @param depositModes - collection of {@link CustomerDepositModeDto}.
   */
  private List<CustomerDepositModeDto> createClientSetting(String customer,
      List<CustomerDepositModeDto> depositModes) {
    try {
      return this.settingFeignClient.createClientSetting(
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()),
          customer, depositModes);
    } catch (FeignClientException e) {
      log.error(e.getMessage(), e);
      throw new ClientSettingJDBCException("Fail to create or modified client setting");
    }
  }

  private void createAndUpdateClientSetting(
      List<String> functionalities, String clientName) {
    List<String> clientSettingFunctionalities = List.of(
        ProfileConstants.CXM_CAMPAIGN,
        ProfileConstants.CXM_SMS_CAMPAIGN,
        ProfileConstants.CXM_FLOW_DEPOSIT);

    List<ProfileClientSettingRequest.Functionality> clientSettingRequests = clientSettingFunctionalities.stream()
        .map(
            csFunc -> new ProfileClientSettingRequest.Functionality(
                csFunc, functionalities.contains(csFunc)))
        .collect(Collectors.toList());

    ProfileClientSettingRequest profileClientSettingRequest = new ProfileClientSettingRequest(clientName,
        clientSettingRequests);
    settingFeignClient.createClientSetting(
        profileClientSettingRequest, this.getAuthTokenWithPrefix());
  }

  /**
   * Mapping dto to entity of {@link Client}
   *
   * @param dto    refer to object of {@link ClientDto}
   * @param entity refer to object that collect from the DB of {@link Client}
   */
  private void mappedDtoToEntity(ClientDto dto, Client entity) {
    entity.setName(dto.getName());
    entity.setContactFirstName(dto.getContactFirstName());
    entity.setContactLastname(dto.getContactLastname());
    entity.setEmail(dto.getEmail());
    entity.setFileId(dto.getFileId());
    entity.setFilename(dto.getFilename());
    entity.setFileSize(dto.getFileSize());
    entity.setLastModified(new Date());

    entity.addClientAllowUnloads(this.getClientAllowUnloads(dto.getPublicHolidays()));
    entity.addClientUnloads(this.getClientUnloads(dto.getUnloads()));
    entity.addDivisions(this.mappingDataRequest(dto));
    entity.addFillers(
        this.modelMapper.map(dto.getFillers(), new TypeToken<List<ClientFillers>>() {
        }.getType()));
  }

  private List<ClientUnloading> getClientUnloads(List<ClientUnloadingDto> unloads) {
    if (unloads.isEmpty()) {
      return new ArrayList<>();
    }

    this.validateTimeFormat(unloads);

    return unloads.stream()
        .map(
            unload -> ClientUnloading.builder()
                .dayOfWeek(unload.getDayOfWeek())
                .time(LocalTime.parse(unload.getTime()))
                .enabled(unload.isEnabled())
                .zoneId(unload.getZoneId())
                .build())
        .collect(Collectors.toList());
  }

  private void validateTimeFormat(List<ClientUnloadingDto> unloads) {
    unloads.forEach(
        unload -> {
          var isMatch = Pattern.compile("^([01]?\\d|2[0-3]):([0-5]?\\d)$")
              .matcher(unload.getTime())
              .matches();
          if (!isMatch) {
            throw new FormatTimeNotAllowedException(unload.getTime());
          }
        });
  }

  private List<ClientAllowUnloading> getClientAllowUnloads(Set<Long> holidays) {
    if (CollectionUtils.isEmpty(holidays)) {
      return new ArrayList<>();
    }

    var holidayOfYear = HolidayCalculator.instance().getHoliday(Calendar.getInstance().get(Calendar.YEAR));

    return holidays.stream()
        .map(
            id -> ClientAllowUnloading.builder()
                .day(holidayOfYear[Math.toIntExact(id)].getDayOfMonth())
                .month(holidayOfYear[Math.toIntExact(id)].getMonthValue())
                .holidayId(id)
                .build())
        .collect(Collectors.toList());
  }

  /**
   * To validate and check the filler keys of the client.
   *
   * @param clientFillers refer to collection {@link List} of
   *                      {@link SharedClientFillersDTO}
   * @see com.tessi.cxm.pfl.ms5.constant.ProfileConstants#CLIENT_FILTERS
   */
  private void validateClientFillerKeys(List<SharedClientFillersDTO> clientFillers) {
    var fillers = clientFillers.stream().map(SharedClientFillersDTO::getKey)
        .collect(Collectors.toList());

    @SuppressWarnings("unchecked")
    List<String> noneMatch = ListUtils.removeAll(
        fillers, com.tessi.cxm.pfl.ms5.constant.ProfileConstants.CLIENT_FILTERS);

    if (!CollectionUtils.isEmpty(noneMatch)) {
      throw new ClientFillerKeysNotAllowedException(noneMatch.toString());
    }
  }

  public SharedClientUnloadDetailsDTO getClientUnloads() {
    long clientId = this.getCurrentUserOrganization(this.getUserId()).getClientId();
    ClientUnloadDetails clientUnloadDetails = this.getClientUnloads(clientId);
    SharedClientUnloadDetailsDTO sharedClientUnloadDetailsDTO = this.modelMapper
        .map(clientUnloadDetails, SharedClientUnloadDetailsDTO.class);
    List<SharedPublicHolidayDTO> publicHolidays = Arrays
        .asList(modelMapper.map(getAllNationalHolidayEvents(), SharedPublicHolidayDTO[].class));
    sharedClientUnloadDetailsDTO.setNationalHolidayEvents(publicHolidays);

    return sharedClientUnloadDetailsDTO;
  }

  public ClientUnloadDetails getClientUnloads(Long clientId) {
    var client = this.findEntity(clientId);
    var clientUnloads = client.getClientUnloads().stream()
        .filter(ClientUnloading::isEnabled)
        .map(unload -> this.modelMapper.map(unload, ClientUnloadingDto.class))
        .collect(Collectors.toList());

    var publicHolidays = client.getClientAllowUnloads().stream()
        .map(
            allowed -> PublicHolidayDto.from(
                LocalDate.of(
                    Calendar.getInstance().get(Calendar.YEAR),
                    allowed.getMonth(),
                    allowed.getDay()),
                "",
                allowed.getHolidayId()))
        .collect(Collectors.toList());

    return ClientUnloadDetails.builder()
        .clientId(clientId)
        .clientUnloads(clientUnloads)
        .publicHolidays(publicHolidays)
        .build();
  }

  /**
   * Validate field and impact on the action update client.
   *
   * @param dto refer to object of d{@link ClientDto}
   */
  private void validateOnUpdateClient(ClientDto dto) {
    if (!this.isAdmin()
        && profileService.notContainsPrivilege(
            ProfileConstants.CXM_CLIENT_MANAGEMENT,
            ProfileConstants.CXM_CLIENT_MANAGEMENT.concat("_").concat(Privilege.MODIFY))) {
      throw new UserAccessDeniedExceptionHandler();
    }

    if (this.isAdmin()) {
      // check functionalities field.
      this.functionalitiesRequired(dto.getFunctionalities());
    }

    if (this.isAdmin() && this.validateDuplicateName(dto.getId(), dto.getName())) {
      throw new ClientNameConflictException(dto.getName());
    }

    // the duplicate name of client.
    if (!this.isAdmin() && !CollectionUtils.isEmpty(dto.getFunctionalities())) {
      throw new FunctionalityNotModifiableException();
    }
  }

  /**
   * Remove profile details when the remove functionalities
   *
   * @param clientId refer to id of {@link Client}
   * @param funcKeys refer to {@link List} of functionality keys as {@link String}
   */
  private List<String> deleteProfileDetails(Long clientId, List<String> funcKeys) {
    var funcKeysEntity = this.loadFunctionalitiesByClientId(clientId);
    @SuppressWarnings("unchecked")
    List<String> funcKeysRemoved = ListUtils.removeAll(funcKeysEntity, funcKeys);
    if (!CollectionUtils.isEmpty(funcKeysRemoved)) {
      this.profileDetailsRepository.deleteProfileDetails(clientId, funcKeysRemoved);
    }
    return funcKeysEntity;
  }

  /**
   * To clear user from the database and Keycloak server that related with
   * services.
   *
   * @param serviceDtoIds  refer to the collections {@link List} of ids
   *                       {@link Department}
   * @param divisionDtoIds refer to the collections {@link List} of ids
   *                       {@link Division}
   */
  private List<String> clearUserDataRelatedClient(
      List<Long> serviceDtoIds, List<Long> divisionDtoIds) {
    if (serviceDtoIds.isEmpty() && divisionDtoIds.isEmpty()) {
      // nothing to do with the below code.
      return List.of();
    }

    List<Long> serviceIds = new ArrayList<>();
    if (!serviceDtoIds.isEmpty()) {
      serviceIds.addAll(serviceDtoIds);
    }
    if (!divisionDtoIds.isEmpty()) {
      var divisionEntity = divisionRepository.findAll(DivisionSpecification.idIn(divisionDtoIds));
      divisionEntity.forEach(
          division -> serviceIds.addAll(
              division.getDepartments().stream()
                  .map(Department::getId)
                  .collect(Collectors.toList())));
    }

    return this.deleteUsersInServices(serviceIds);
  }

  private void deleteKeyCloakUsers(List<String> userIds) {
    userIds.forEach(
        deletedUserId -> {
          try {
            keycloakService.deleteUser(deletedUserId);
          } catch (Exception exception) {
            log.error("Fail to delete a user from Keycloak: " + deletedUserId + ".", exception);
          }
        });
  }

  /**
   * To check and validate the id existing of the {@link Department}
   *
   * @param serviceIds       refer to {@link List} of {@link Department} ids.
   * @param serviceIdsEntity refer to {@link List} of ids of {@link Department}
   *                         that selected from
   *                         the database.
   */
  private void validateExistingServiceId(List<Long> serviceIds, List<Long> serviceIdsEntity) {
    if (!serviceIds.isEmpty()) {
      // validate service ids after to delete the user.
      var noneMatchIds = serviceIds.stream()
          .filter(id -> !serviceIdsEntity.remove(id))
          .collect(Collectors.toList());

      if (!noneMatchIds.isEmpty()) {
        throw new DepartmentNotFoundException(noneMatchIds);
      }
    }
  }

  /**
   * To check and validate the id existing of the {@link Division}
   *
   * @param divisionIds       refer to {@link List} of {@link Division} ids.
   * @param divisionEntityIds refer to {@link List} of ids of {@link Division}
   *                          that selected from
   *                          the database.
   */
  private void validateExistingDivisionId(List<Long> divisionIds, List<Long> divisionEntityIds) {
    if (!divisionIds.isEmpty()) {
      // validate service ids after to delete the user.
      var noneMatchIds = divisionIds.stream()
          .filter(id -> !divisionEntityIds.remove(id))
          .collect(Collectors.toList());

      if (!noneMatchIds.isEmpty()) {
        throw new DivisionNotFoundException(noneMatchIds);
      }
    }
  }

  private List<Division> mappingDataRequest(ClientDto dto) {
    List<Division> divisions = new ArrayList<>();
    dto.getDivisions().stream()
        .filter(Objects::nonNull)
        .forEach(
            divisionDto -> {
              var division = this.modelMapper.map(divisionDto, Division.class);
              var services = divisionDto.getServices().stream()
                  .map(s -> this.modelMapper.map(s, Department.class))
                  .collect(Collectors.toList());
              division.addDepartments(services);
              divisions.add(division);
            });
    return divisions;
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    var entity = this.findEntity(id);
    final var refFileId = entity.getFileId();

    this.deleteProfileDataRelated(id);

    List<Long> serviceIds = this.getServiceIds(entity, List.of());

    final List<String> deletedUserIds = new ArrayList<>();
    if (!serviceIds.isEmpty()) {
      // Remove data from other microservices.
      final List<UserEntity> users = this.getUsersByClientId(id);
      final var usernames = users.stream().map(UserEntity::getUsername).collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(usernames)) {
        try {
          Executors.newSingleThreadScheduledExecutor()
              .execute(
                  () -> {
                    this.campaignFeignClient.removeCampaignHandler(
                        usernames, serviceIds, this.getAuthTokenWithPrefix());
                    this.templateFeignClient.removeTemplateHandler(
                        usernames, serviceIds, this.getAuthTokenWithPrefix());
                    this.flowFeignClient.removeFlowHandler(
                        usernames, serviceIds, this.getAuthTokenWithPrefix());
                  });
        } catch (Exception ex) {
          log.info(ex.getMessage());
        }
      }

      // delete all user from the database and Keycloak server.
      deletedUserIds.addAll(this.deleteUsersInServices(serviceIds));
    }

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            final var adminToken = ClientService.this.getAuthTokenWithPrefix();
            if (!deletedUserIds.isEmpty()) {
              ClientService.this.deleteKeyCloakUsers(deletedUserIds);
            }
            if (StringUtils.hasText(refFileId)) {
              ClientService.this.deleteFile(refFileId, adminToken);
            }
            ClientService.this.processControlFeignClient.deleteClientUnloads(id, adminToken);
          }
        });
    // Delete returnAddress of client.
    this.returnAddressService.deleteReturnAddressByClient(entity.getId());
    clientRepository.delete(entity);
  }

  private List<Long> getServiceIds(ClientDto dto) {
    var divisions = dto.getDivisions().stream().filter(Objects::nonNull).collect(Collectors.toList());

    return divisions.stream()
        .map(
            divisionDto -> divisionDto.getServices().stream()
                .filter(Objects::nonNull)
                .map(DepartmentDto::getId)
                .filter(id -> id != 0)
                .collect(Collectors.toList()))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private List<Long> getServiceIds(Client client, List<Long> ignoreDivisionIds) {
    var divisions = client.getDivisions().stream().filter(Objects::nonNull).collect(Collectors.toList());

    return divisions.stream()
        .map(
            division -> division.getDepartments().stream()
                .filter(
                    department -> Objects.nonNull(department)
                        && !ignoreDivisionIds.contains(department.getDivision().getId()))
                .map(Department::getId)
                .collect(Collectors.toList()))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  /**
   * To retrieve all users in client by client id.
   *
   * @param clientId refer id of {@link Client}
   * @return list of user's username into client or organization.
   */
  public List<String> getAllUsersInClient(long clientId) {
    var users = this.userRepository.getAllUsersInClient(clientId, true);
    if (users.isEmpty()) {
      throw new ClientNotFoundException(clientId);
    }
    return users.stream().map(LoadUserPrivilegeDetails::getUsername).collect(Collectors.toList());
  }

  /**
   * Check this the client of current user has at least one active profile
   * available.
   *
   * @return True is at least one profile is existed, otherwise false.
   */
  public boolean isClientProfileNotEmpty(Optional<Long> clientId) {
    long checkingClientId = clientId.orElseGet(
        () -> {
          var userOrganization = this.userRepository.loadOrganizationUser(this.getUserId());
          return userOrganization
              .orElseThrow(() -> new NotRegisteredServiceUserException(this.getUserId()))
              .getClientId();
        });
    return this.clientRepository.getProfileCount(checkingClientId) > 0;
  }

  @Override
  public String getConfiguredUserAdminId() {
    return this.adminUserId;
  }

  /**
   * To validate and check the name of {@link Client}
   *
   * @param id   refer to id of {@link Client}.
   *             <code>On the action create, Id must equal 0.</code>
   * @param name refer to name of {@link Client}
   * @return true if duplicate name.
   */
  public boolean validateDuplicateName(long id, String name) {
    Specification<Client> specification = Specification.where(null);
    if (id != 0) {
      var entity = this.findEntity(id);
      if (entity.getName().equalsIgnoreCase(name)) {
        return false;
      }
      specification = specification.and(ClientSpecification.notEqualId(id));
    }
    specification = specification.and(ClientSpecification.equalName(name));
    return this.clientRepository.findOne(specification).isPresent();
  }

  /**
   * To validate duplicate name of division and service.
   *
   * @param divisions refer to {@link List} of {@link DivisionDto}
   */
  private void validateDuplicate(List<DivisionDto> divisions) {
    var unique = new HashSet<String>();
    var uniqueService = new HashSet<String>();
    divisions.stream()
        .filter(Objects::nonNull)
        .forEach(
            division -> {
              if (!unique.add(division.getName().trim())) {
                throw new DivisionNameConflictException(division.getName());
              }

              division
                  .getServices()
                  .forEach(
                      s -> {
                        if (!uniqueService.add(s.getName().trim())) {
                          throw new DepartmentConflictNameException(s.getName());
                        }
                      });
              uniqueService.clear();
            });
  }

  /**
   * Delete a file from cxm-file-manager by file's id without throwing an
   * exception when file is not
   * found by file's id.
   *
   * @param fileId File's id
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteFile(String fileId, String refToken) {
    try {
      this.fileManagerResource.deleteMetadataFile(fileId, null, null, refToken);
      log.debug("File is already deleted: {}.", fileId);
    } catch (FeignClientException feignClientException) {
      if (feignClientException.status() != HttpStatus.NOT_FOUND.value()) {
        throw feignClientException;
      }
      log.info("Failed to delete a file as it is not found: {}.", fileId);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public List<String> deleteUsersInServices(List<Long> serviceIds) {
    var users = userRepository.findAll(UserSpecification.serviceIn(serviceIds));

    if (users.isEmpty()) {
      return List.of();
    }
    final var deletedUserIds = users.stream().map(UserEntity::getTechnicalRef)
        .collect(Collectors.toList());
    this.userRepository.deleteAll(users);
    return deletedUserIds;
  }

  public List<UserEntity> getUsersByClientId(Long clientIds) {
    final var specification = UserSpecification.clientEqual(clientIds);
    return this.userRepository.findAll(specification);
  }

  /**
   * To mapping object of client with their dto class.
   *
   * @param client refer to object of {@link Client}
   * @param dto    refer to object of {@link ClientDto}
   * @return object that mapped.
   */
  protected ClientDto mapping(@NotNull Client client, ClientDto dto) {
    modelMapper.map(client, dto);
    IntStream.range(0, client.getDivisions().size())
        .forEach(
            idx -> {
              var division = dto.getDivisions().get(idx);
              division.setServices(
                  modelMapper.map(
                      this.getDepartmentsByDivisionId(division.getId(), client),
                      new TypeToken<List<DepartmentDto>>() {
                      }.getType()));
            });
    return dto;
  }

  private List<Department> getDepartmentsByDivisionId(Long divisionId, Client client) {
    return new ArrayList<>(
        client.getDivisions().stream()
            .filter(division -> division.getId() == divisionId)
            .findFirst()
            .map(Division::getDepartments)
            .orElse(new ArrayList<>()));
  }

  /**
   * To retrieve client entity object by id and by checking with privileges.
   *
   * @param id refer to id of {@link Client}
   * @return - object of {@link Client}
   */
  private Client getClient(long id) {
    if (this.isAdmin()) {
      return this.findEntity(id);
    } else {
      return this.clientRepository
          .findOne(
              Specification.where(
                  ClientSpecification.byUserTechnicalRefAndDeletedFalse(
                      this.getPrincipalIdentifier())
                      .and(ClientSpecification.equalId(id))))
          .orElseThrow(UserAccessDeniedExceptionHandler::new);
    }
  }

  /**
   * To clear data of profile that related with profile and client.
   *
   * @param clientId refer to id of {@link Client}
   */
  private void deleteProfileDataRelated(Long clientId) {
    var profiles = this.profileService.getAllProfilesByClientId(clientId);

    if (!profiles.isEmpty()) {
      profiles.forEach(p -> p.removeProfileDetails(p.getProfileDetails()));
      // To delete the user profiles if existing the profile.
      this.userProfileRepository.deleteByProfileIn(profiles);
    }

    this.profileService.deleteByClient(clientId);
  }

  private List<Functionalities> checkAndGetFunctionalities(List<String> funcKeys) {
    List<Functionalities> functionalities = getFunctionalities(funcKeys);
    final List<String> funcKeysIsNotCompatible = funcKeys.stream()
        .filter(
            funcId -> functionalities.stream()
                .noneMatch(func -> Objects.equals(func.getKey(), funcId)))
        .collect(Collectors.toList());
    if (!funcKeysIsNotCompatible.isEmpty()) {
      throw new FunctionalitiesNotFound(
          String.format("Functionalities keys not compatible %s", funcKeysIsNotCompatible));
    }
    return functionalities;
  }

  private List<ClientFunctionalitiesDetails> getClientFunctionalitiesDetails(
      List<String> funcKeys) {
    if (CollectionUtils.isEmpty(funcKeys)) {
      return new ArrayList<>();
    }
    List<Functionalities> functionalities = checkAndGetFunctionalities(funcKeys);
    return functionalities.stream()
        .map(fuc -> new ClientFunctionalitiesDetails(0, null, fuc))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Functionalities> getFunctionalities(List<String> funcKeys) {
    return this.functionalitiesRepository.findAllByKeyIn(funcKeys);
  }

  public void createDefaultProfileOfClient(Client client, List<String> funcKeys) {
    ProfileDto profileDto = new ProfileDto();
    // set up a profile for client
    profileDto.setName(client.getName() + "_Admin");
    profileDto.setDisplayName("Client Administrator");
    // set up a profileDetails for client
    final List<ProfileDetailDto> profileDetails = funcKeys.stream()
        .map(
            funcKey -> new ProfileDetailDto(0, funcKey, getPrivileges(funcKey), CLIENT, CLIENT))
        .collect(Collectors.toList());
    profileDto.setFunctionalities(profileDetails);
    this.profileService.createProfile(profileDto, client);
  }

  private List<com.tessi.cxm.pfl.ms5.entity.Privilege> getPrivileges(String funcKey) {
    final FunctionalitiesReference functionalitiesReference = this.objectMapper.convertValue(
        this.profileService.getFunctionalityPrivilege(funcKey), FunctionalitiesReference.class);
    final Optional<SubFunctionalitiesReference> subFunction = functionalitiesReference.getFunctionalities().stream()
        .findFirst();
    if (subFunction.isEmpty()) {
      return new ArrayList<>();
    }
    return subFunction.get().getSubValue().stream()
        .map(
            sub -> new com.tessi.cxm.pfl.ms5.entity.Privilege(
                (String) sub.get("key"), CLIENT, false, CLIENT, false))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Functionalities> getFunctionalitiesByClientId(long clientId) {
    return this.functionalitiesRepository.findAllByClientFunctionalitiesDetailsClientId(clientId);
  }

  /**
   * Load all functionalities by the client ID. Client ID required if invoked by
   * Super-Admin. If the
   * invoking user is not a Super-Admin, the client ID is the ID of client of that
   * user.
   *
   * @param clientId Client ID
   * @return List of functionalities
   */
  public List<String> getFunctionalitiesByCurrentInvokedUser(long clientId) {
    long refClientId = clientId;
    if (!this.isAdmin()) {
      refClientId = this.userRepository
          .loadOrganizationUser(this.getUserId())
          .orElseThrow(() -> new NotRegisteredServiceUserException(this.getUserId()))
          .getClientId();
    }
    return refClientId == 0
        ? this.functionalitiesRepository.findAll().stream()
            .map(Functionalities::getKey)
            .collect(Collectors.toList())
        : this.loadFunctionalitiesByClientId(refClientId);
  }

  @Transactional(readOnly = true)
  public List<PublicHolidayDto> getAllNationalHolidayEvents() {
    var holidays = HolidayCalculator.instance().getHoliday(Calendar.getInstance().get(Calendar.YEAR));
    AtomicInteger index = new AtomicInteger();

    return Arrays.stream(holidays)
        .map(date -> index.getAndIncrement())
        .map(i -> PublicHolidayDto.from(holidays[i], HolidayCalculator.HOLIDAY_LABELS[i], i))
        .collect(Collectors.toList());
  }

  /**
   * To retrieve all configure of client fillers.
   *
   * @return refer to object {@link List} of {@link SharedClientFillersDTO}.
   */
  @Transactional(readOnly = true)
  public List<SharedClientFillersDTO> getAllClientFillers(Long clientId) {
    if (this.isSuperAdmin()) {
      if (clientId == null || clientId == 0) {
        throw new ClientNotFoundException(0);
      }
    } else {
      clientId = this.getClientId();
    }
    return clientFillersRepository.findByClientIdAndEnabledTrue(clientId).stream()
        .map(filler -> this.modelMapper.map(filler, SharedClientFillersDTO.class))
        .sorted(Comparator.comparing(SharedClientFillersDTO::getKey))
        .collect(Collectors.toList());
  }

  public List<SharedClientFillersDTO> getAllClientFillers(Long clientId, boolean isResolveValue) {
    List<SharedClientFillersDTO> fillers = this.getAllClientFillers(clientId);

    if (isResolveValue) {
      fillers.forEach(
          filler -> {
            if (StringUtils.hasText(filler.getKey()) && !StringUtils.hasText(filler.getValue())) {
              String fillerNumber = filler.getKey().substring(filler.getKey().length() - 1);
              filler.setValue("Filler " + fillerNumber);
            }
          });
    }

    return fillers;
  }

  /**
   * To check the functionalities' field have value of not.
   *
   * @param functionalities refer to {@link Set} of {@link String}
   */
  private void functionalitiesRequired(Set<String> functionalities) {
    if (CollectionUtils.isEmpty(functionalities)) {
      throw new FunctionalityRequiredException();
    }
  }

  /**
   * To load all functionalities of the client.
   *
   * @param clientId refer to id of {@link Client}
   * @return object as {@link List} of {@link String}
   */
  private List<String> loadFunctionalitiesByClientId(long clientId) {
    return this.getFunctionalitiesByClientId(clientId).stream()
        .map(Functionalities::getKey)
        .collect(Collectors.toList());
  }

  /**
   * Method used to get clientId.
   *
   * @return value of {@link Long}
   */
  @Transactional(readOnly = true)
  public Long getClientId() {
    var userId = this.keycloakService.getUserInfo(this.getUsername()).getId();
    return this.clientRepository
        .getClientIdByUserId(userId)
        .orElseThrow(() -> new ProfileNotBelongToServiceException(this.getUsername()));
  }

  /**
   * Get all client criteria.
   *
   * @return a list of {@link ClientCriteriaDto}
   * @throws UserAccessDeniedExceptionHandler if user is not super admin.
   */
  public List<ClientCriteriaDto> getClientCriteria(String sortDirection) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    Direction direction = sortDirection.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
    Sort defaultSort = Sort.by(direction, "name");
    return this.clientRepository.findAll(defaultSort).stream()
        .map(c -> ClientCriteriaDto.builder()
            .id(c.getId())
            .name(c.getName())
            .build())
        .collect(Collectors.toList());
  }

  /**
   * Get all service details of a client by client's id.
   *
   * @param clientId Client's id.
   * @return {@link ClientServiceDetailsDTO}.
   */
  public ClientServiceDetailsDTO getServices(Optional<Long> clientId) {
    var builder = ClientServiceDetailsDTO.builder();
    var clientResponses = new ArrayList<ClientResponseDTO>();

    if (!this.isAdmin()) {
      clientResponses.add(this.getServicesOfUserClient(clientId));
    } else if (clientId.isPresent()) {
      clientResponses.add(this.getServicesOfClient(clientId.get()));
    } else {
      clientResponses.addAll(
          this.clientRepository.findAll().stream()
              .map(clientEntity -> this.modelMapper.map(clientEntity, ClientResponseDTO.class))
              .collect(Collectors.toList()));
    }

    builder.clients(clientResponses);
    return builder.build();
  }

  /**
   * Get all services details defined in {@link ClientResponseDTO} of a client of
   * current invoking
   * user.
   *
   * @param clientId Client's id of current invoking user. If empty, the client id
   *                 will be getting
   *                 from the current invoking user.
   * @return {@link ClientResponseDTO}
   */
  private ClientResponseDTO getServicesOfUserClient(Optional<Long> clientId) {
    long targetingClientId;
    var userClientId = this.userRepository
        .loadOrganizationUser(this.getUserId())
        .orElseThrow(() -> new NotRegisteredServiceUserException(this.getUserId()))
        .getClientId();
    if (clientId.isPresent()) {
      if (clientId.get() != userClientId) {
        throw new UserAccessDeniedExceptionHandler(
            "User is not belong to client " + clientId.get() + ".");
      }
      targetingClientId = clientId.get();
    } else {
      targetingClientId = userClientId;
    }

    final var clientEntity = this.clientRepository
        .findById(targetingClientId)
        .orElseThrow(() -> new ClientNotFoundException(targetingClientId));

    return this.modelMapper.map(clientEntity, ClientResponseDTO.class);
  }

  /**
   * Get all services details defined in {@link ClientResponseDTO} of a client by
   * id. This must be
   * used conditionally with {@link AdminService#isAdmin()}.
   *
   * @param clientId Client's id.
   * @return {@link ClientResponseDTO}
   */
  private ClientResponseDTO getServicesOfClient(Long clientId) {

    final var clientEntity = this.clientRepository
        .findById(clientId)
        .orElseThrow(() -> new ClientNotFoundException(clientId));

    return this.modelMapper.map(clientEntity, ClientResponseDTO.class);
  }

  public PortalSettingConfigStatusDto modifiedPortalSettingConfig(
      PortalSettingConfigStatusDto dto) {
    try {
      return this.settingFeignClient.modifiedPortalSettingConfig(
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()), dto);
    } catch (FeignClientException e) {
      log.error(e.getMessage(), e);
      throw new PortalSettingConfigFailureException("Fail to modified portal setting configuration");
    }
  }

  public PostalConfigurationDto getPostalConfiguration(String clientName) {
    try {
      return this.settingFeignClient.getPostalConfiguration(clientName,
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    } catch (FeignClientException e) {
      if (e.status() == HttpStatus.NOT_FOUND.value()) {
        throw new PortalConfigurationFailureException("File not exist", e);
      }

      throw new PortalConfigurationFailureException("Fail to get portal configuration", e);
    }
  }

  public PostalConfigurationDto modifiedINIConfiguration(PostalConfigurationDto dto) {
    try {
      return this.settingFeignClient.modifiedINIConfiguration(dto,
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    } catch (FeignClientException e) {
      throw new INIConfigurationFileNotAcceptable("Fail to modified INI configuration file", e);
    }
  }

  public List<PostalConfigurationVersion> getPostalConfigurationVersions(String clientName) {
    try {
      return this.settingFeignClient.getCustomerConfigurationVersions(clientName,
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    } catch (FeignClientException e) {
      log.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  public PostalConfigurationVersionDto getPostalConfigurationVersion(String clientName,
      int version) {
    try {
      return this.settingFeignClient.getCustomerConfigurationVersion(clientName, version,
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    } catch (FeignClientException e) {
      log.error(e.getMessage(), e);
      throw new PortalConfigurationFailureException("Fail to get portal configuration", e);
    }
  }

  public PostalConfigurationVersion revertPostalConfiguration(String clientName,
      int referenceVersion) {
    try {
      return this.settingFeignClient.revertPostalConfiguration(clientName, referenceVersion,
          BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    } catch (FeignClientException e) {
      log.error(e.getMessage(), e);
      throw new PortalConfigurationFailureException("Fail to revert postal INI configuration", e);
    }
  }

  /**
   * Get criteria distributions.
   *
   * @param clientName Client name
   * @return {@link CriteriaDistributionsResponse}
   */
  private CriteriaDistributionsResponse getCriteriaDistributions(String clientName) {
    try {
      return this.settingFeignClient.getCriteriaDistributions(
          clientName, BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    } catch (FeignClientException e) {
      throw new ClientSettingJDBCException("Fail to get client portal setting configuration");
    }
  }

  // add new
  ClientDto getDivisionByClient2(Long id) {
    boolean isAdmin = true;
    if (!this.isAdmin()) {
      if (profileService.notContainsPrivilege(ProfileConstants.CXM_CLIENT_MANAGEMENT,
          ProfileConstants.CXM_CLIENT_MANAGEMENT.concat("_").concat(Privilege.MODIFY))) {
        throw new UserAccessDeniedExceptionHandler();
      }
      isAdmin = false;
    }
    final var clientEntity = this.clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    return this.modelMapper.map(clientEntity, ClientDto.class);
  }
}
