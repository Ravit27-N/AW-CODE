package com.tessi.cxm.pfl.ms5.service;

import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.MOCK_FUNCTIONALITY_LIST;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.MOCK_FUNCTIONALITY_PRIVILEGES;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER_INVALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.LoadClient;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.Functionalities;
import com.tessi.cxm.pfl.ms5.entity.projection.ClientInfo;
import com.tessi.cxm.pfl.ms5.exception.ClientNameConflictException;
import com.tessi.cxm.pfl.ms5.exception.ClientNameNotModifiableException;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotBelongToServiceException;
import com.tessi.cxm.pfl.ms5.repository.ClientFillersRepository;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DivisionRepository;
import com.tessi.cxm.pfl.ms5.repository.FunctionalitiesRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.restclient.CampaignFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.FlowFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.TemplateFeignClient;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.Configuration;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersion;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersionDto;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ClientServiceTest {
  private static final String TEST_USERNAME = "User TEST";
  @Mock private ClientRepository clientRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProfileService profileService;
  @Mock private FileManagerResource fileManagerResource;
  private MockClientService clientService;
  private final ModelMapper modelMapper = new ModelMapper();

  @Mock private KeycloakService keycloakService;
  @Mock private DivisionRepository divisionRepository;

  @Mock private UserProfileRepository userProfileRepository;
  @Mock private FunctionalitiesRepository functionalitiesRepository;
  @Mock private CampaignFeignClient campaignFeignClient;
  @Mock private TemplateFeignClient templateFeignClient;
  @Mock private FlowFeignClient flowFeignClient;

  @Mock private SettingFeignClient settingFeignClient;

  @Mock private ClientFillersRepository clientFillersRepository;
  @Mock private ReturnAddressService returnAddressService;

  private Client mockClientEntity;

  @BeforeEach
  void beforeEach() {
    TransactionSynchronizationManager.initSynchronization();
    this.clientService =
        new MockClientService(
            clientRepository,
            userRepository,
            modelMapper,
            keycloakService,
            campaignFeignClient,
            templateFeignClient,
            flowFeignClient);
    this.clientService.setProfileService(this.profileService);
    this.clientService.setSupperAdmin(true);
    this.clientService.setFileManagerResource(fileManagerResource);
    this.clientService.setDivisionRepository(divisionRepository);
    this.clientService.setUserProfileRepository(userProfileRepository);
    this.clientService.setFunctionalitiesRepository(functionalitiesRepository);
    this.clientService.setObjectMapper(new ObjectMapper());
    this.clientService.setClientFillersRepository(clientFillersRepository);
    this.clientService.setSettingFeignClient(settingFeignClient);
    this.clientService.setReturnAddressService(returnAddressService);
    ReflectionTestUtils.setField(this.clientService, "adminUserId", "adminId");
    mockClientEntity =
        Client.builder()
            .id(1L)
            .name("Client1")
            .contactFirstName("Client1_Firstname")
            .contactLastname("Client1_Last")
            .email("client.1@gmail.com")
            .fileId("001")
            .filename("client1_privacy.pdf")
            .fileSize(1024)
            .divisions(
                List.of(
                    Division.builder()
                        .id(1)
                        .name("Division 1")
                        .departments(
                            List.of(Department.builder().id(1).name("Department1").build()))
                        .build()))
            .clientFunctionalitiesDetails(new ArrayList<>())
            .build();
  }

  @AfterEach
  public void clear() {
    TransactionSynchronizationManager.clear();
  }

  @Test
  @Order(1)
  void testCreateClientBySuperAdmin_withoutDefaultProfile() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      when(clientRepository.save(any(Client.class)))
          .thenReturn(ProfileUnitTestConstants.SAMPLE_CLIENT_1);
      this.mockFindAllByCollectionFuncKeys();
      //    this.mockFunctionalityPrivilege();
      var result = clientService.save(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO);
      Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
    }
  }

  @Test
  @Order(2)
  void testCreateClient_ThenThrowUserAccessDeniedException() {
    this.clientService.setSupperAdmin(false);

    var result =
        Assertions.assertThrows(
            UserAccessDeniedExceptionHandler.class,
            () -> this.clientService.save(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO));

    log.info("Actual result: {0}", result);
  }

  @Test
  @Order(3)
  void testDeleteClient() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(clientRepository.findById(anyLong()))
          .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));

      Assertions.assertDoesNotThrow(() -> clientService.delete(1L));
    }
  }

  @Test
  @Order(4)
  void testDeleteClientNotFound() {
    var clientNotFoundException =
        Assertions.assertThrows(ClientNotFoundException.class, () -> clientService.delete(1L));
    log.info("Result :{}", clientNotFoundException.getMessage());
  }

  @Test
  @Order(5)
  void testGetClientByIdBySuperAdmin() {
    //when(clientService.isAdmin()).thenReturn(true); // mocker super admin call
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {

      utils.when(() -> AuthenticationUtils.getAuthToken()).thenReturn("");

      // mock configuration true case
      var dto = new PortalSettingConfigStatusDto();
      dto.setActive(true);
      when(settingFeignClient.getPortalSettingConfig(anyString(), anyString()))
      .thenReturn(dto);

      when(clientRepository.findById(anyLong()))
      .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));
      this.mockGetFunctionalitiesByClientId();

      var result = clientService.findById(1L);
      Assertions.assertEquals(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO.getId(), result.getId());
      log.info("Result :{}", result);
    }
  }

  @Test
  @Order(6)
  void testGetClientByIdNotFound() {
    var clientNotFoundException =
        Assertions.assertThrows(ClientNotFoundException.class, () -> clientService.findById(1L));
    log.info("Result :{}", clientNotFoundException.getMessage());
  }

  @Test
  @Order(7)
  void successGetListOfClientsBySuperAdmin() {
    this.clientService.setSupperAdmin(true);
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.fromString("desc"), "lastModified");
    Page<ClientInfo> pagedClients = new PageImpl<>(List.of(this.loadMockClient()));
    var filter = "";
    // Stub
    lenient()
        .when(
            this.clientRepository.findAll(
                ArgumentMatchers.any(),
                any(Class.class),
                anyString(),
                any(EntityGraph.EntityGraphType.class),
                any(Pageable.class)))
        .thenReturn(pagedClients);
    // Call
    Page<LoadClient> clientLists = this.clientService.loadAllClients(pageable, filter);
    // Assert
    Assertions.assertEquals(pagedClients.getTotalElements(), clientLists.getTotalElements());

    log.info("Actual result: {}", clientLists);
  }

  @Test
  @Order(8)
  void successGetListOfClientsByClientAdmin() {
    this.mockPrivilegeClientAdmin();
    this.clientService.setUserId("001");
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.fromString("desc"), "lastModified");
    var filter = "";
    Page<ClientInfo> pagedClients = new PageImpl<>(List.of(this.loadMockClient()));
    // Stub
    lenient()
        .when(
            this.clientRepository.findAll(
                ArgumentMatchers.any(),
                any(Class.class),
                anyString(),
                any(EntityGraph.EntityGraphType.class),
                any(Pageable.class)))
        .thenReturn(pagedClients);

    // Call
    Page<LoadClient> clientLists = this.clientService.loadAllClients(pageable, filter);

    log.info("Actual result: {}", clientLists);
    // Assert
    Assertions.assertEquals(1, clientLists.getTotalElements());
  }

  @Test
  @Order(9)
  void failOnGetListOfClientsByClientAdmin() {
    // mock privilege
    this.mockPrivilegeNotSuperAdminAndNotClientAdmin();

    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.fromString("desc"), "lastModified");
    var filter = "";
    // Call
    Assertions.assertThrows(
        UserAccessDeniedExceptionHandler.class,
        () -> this.clientService.loadAllClients(pageable, filter));
  }

  @Test
  @Order(10)
  void testValidateDuplicateNameWithIdReturnTrue() {
    final var client = mockClientEntity;
    client.setName("example");
    when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(client));
    final var result = clientService.validateDuplicateName(1L, "example1");
    Assertions.assertTrue(result);
    log.info("Result expected => {}", true);
  }

  @Test
  @Order(11)
  void testValidateDuplicateNameWithIdReturnFail() {
    final var client = mockClientEntity;
    client.setName("example");
    when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
    final var result = clientService.validateDuplicateName(1L, "example");
    Assertions.assertFalse(result);
    log.info("Result expected => {}", false);
  }

  @Test
  @Order(12)
  void testValidateDuplicateNameWithIdAndDifferNameReturnFail() {
    final var client = mockClientEntity;
    client.setName("example");
    when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.empty());
    final var result = clientService.validateDuplicateName(1L, "example1");
    Assertions.assertFalse(result);
    log.info("Result expected => {}", false);
  }

  @Test
  @Order(13)
  void testValidateDuplicateNameWithoutIdReturnTrue() {
    final var client = mockClientEntity;
    client.setName("example");
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(client));
    final var result = clientService.validateDuplicateName(0L, "example");
    Assertions.assertTrue(result);
    log.info("Result expected => {}", true);
  }

  @Test
  @Order(14)
  void testValidateDuplicateNameWithoutIdReturnFail() {
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.empty());
    final var result = clientService.validateDuplicateName(0L, "example");
    Assertions.assertFalse(result);
    log.info("Result expected => {}", false);
  }

  @Test
  @Order(15)
  void testUpdateClientBySuperUser() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
    
      utils.when(() -> AuthenticationUtils.getAuthToken()).thenReturn("");
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      when(clientRepository.findById(anyLong()))
          .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));
      when(clientRepository.save(any(Client.class)))
          .thenReturn(ProfileUnitTestConstants.SAMPLE_CLIENT_1);
      this.mockFindAllByCollectionFuncKeys();

      var result = this.clientService.update(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO);

      Assertions.assertEquals(
          ProfileUnitTestConstants.MOCK_CLIENT_RES_AFTER_SAVE.getId(), result.getId());

      log.info("Actual result: {}", result);
    }
  }

  @Test
  @Order(16)
  void testUpdateClientByClientAdmin() {
    this.mockPrivilegeClientAdmin();
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));
    when(clientRepository.save(any(Client.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_CLIENT_1);
    var result = this.clientService.update(ProfileUnitTestConstants.MOCK_CLIENT_CUSTOMER_ADMIN);

    Assertions.assertEquals(
        ProfileUnitTestConstants.MOCK_CLIENT_CUSTOMER_ADMIN.getId(), result.getId());

    log.info("Actual result: {}", result);
  }

  @Test
  @Order(17)
  void testUpdateClientByRemoveServiceAndDivision() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils.when(() -> AuthenticationUtils.getAuthToken()).thenReturn("");

      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      when(clientRepository.findById(anyLong()))
          .thenReturn(Optional.of(ProfileUnitTestConstants.MOCK_FIND_ONE_CLIENT));
      when(clientRepository.save(any(Client.class)))
          .thenReturn(ProfileUnitTestConstants.MOCK_UPDATE_CLIENT);
      when(divisionRepository.findAll(ArgumentMatchers.<Specification<Division>>any()))
          .thenReturn(List.of(ProfileUnitTestConstants.MOCK_REMOVED_DIVISION));
      this.mockFindAllByCollectionFuncKeys();
      var result = this.clientService.update(ProfileUnitTestConstants.MOCK_UPDATE_CLIENT_DTO);

      log.info("Actual result: {}", result);

      Assertions.assertNotNull(result);
    }
  }

  @Test
  @Order(18)
  void testUpdateClient_ThenThrowUserAccessDeniedException() {
    this.mockPrivilegeNotSuperAdminAndNotClientAdmin();
    var result =
        Assertions.assertThrows(
            UserAccessDeniedExceptionHandler.class,
            () -> this.clientService.update(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO));

    log.info("Actual result: {0}", result);
  }

  @Test
  @Order(19)
  void testUpdateClient_ThenThrowClientNotFoundException() {
    when(clientRepository.findById(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO.getId()))
        .thenThrow(
            new ClientNotFoundException(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO.getId()));

    var clientNotFoundException =
        Assertions.assertThrows(
            ClientNotFoundException.class,
            () -> clientService.update(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO));
    log.info("Actual result => {}", clientNotFoundException.getMessage());
  }

  @Test
  @Order(20)
  void testGetClientByIdByClientAdmin() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils.when(AuthenticationUtils::getAuthToken).thenReturn("");

      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      utils.when(AuthenticationUtils::getAuthToken).thenReturn(TEST_USERNAME);
      utils
          .when(() -> AuthenticationUtils.getPrincipalIdentifier(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);
      this.mockPrivilegeClientAdmin();
      when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
          .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));
      this.mockGetFunctionalitiesByClientId();
      when(this.settingFeignClient.getDepositModes(anyString()))
          .thenReturn(List.of(new CustomerDepositModeDto()));
      when(this.returnAddressService.getReturnAddress(anyLong()))
          .thenReturn(ProfileUnitTestConstants.MOCK_RETURN_ADDRESSES);

      // when(this.settingFeignClient.getPortalSettingConfig(anyString(),anyString())).thenReturn(new PortalSettingConfigStatusDto());
      var result = clientService.findById(1L);
      Assertions.assertEquals(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO.getId(), result.getId());
      log.info("Actual result => {}", result);
    }
  }

  @Test
  @Order(21)
  void testGetClientByIdByClientAdmin_ThenThrowUserAccessDeniedException() {
    this.mockPrivilegeClientAdmin();
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.empty());

    var result =
        Assertions.assertThrows(
            UserAccessDeniedExceptionHandler.class, () -> clientService.findById(1L));

    log.info("Actual result: {0}", result);
  }

  @Test
  @Order(22)
  void testGetClientById_ThenThrowUserAccessDeniedException() {
    this.mockPrivilegeNotSuperAdminAndNotClientAdmin();

    var result =
        Assertions.assertThrows(
            UserAccessDeniedExceptionHandler.class, () -> clientService.findById(1L));

    log.info("Actual result: {0}", result);
  }

  @Test
  @Order(23)
  void testUpdateClient_ThenThrowClientNameNotModifiableException() {
    final var client = mockClientEntity;
    client.setId(0);
    client.setName("example");
    when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
    //    this.mockFindAllByCollectionFuncKeys();

    var result =
        Assertions.assertThrows(
            ClientNameNotModifiableException.class,
            () -> this.clientService.update(ProfileUnitTestConstants.MOCK_UPDATE_CLIENT_DTO));

    log.info("Actual result: {}", result.getMessage());
  }

  @Test
  @Order(23)
  void testSaveClient_ThenThrowClientNameConflictException() {
    final var client = mockClientEntity;
    client.setId(0);
    client.setName("example");
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(client));

    var result =
        Assertions.assertThrows(
            ClientNameConflictException.class,
            () -> this.clientService.save(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO));

    log.info("Actual result: {0}", result);
  }

  @Test
  @Order(24)
  void testDelete_ThenThrowUserAccessDeniedException() {
    this.clientService.setSupperAdmin(false);

    Assertions.assertThrows(
        UserAccessDeniedExceptionHandler.class, () -> this.clientService.delete(1L));
  }

  @Test
  void testCreateClientBySuperAdmin_withDefaultProfile() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      when(clientRepository.save(any(Client.class)))
          .thenReturn(ProfileUnitTestConstants.SAMPLE_CLIENT_1);
      this.mockFindAllByCollectionFuncKeys();
      this.mockFunctionalityPrivilege();
      var result = clientService.save(ProfileUnitTestConstants.MOCK_CLIENT_SUPER_ADMIN);
      Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
    }
  }

  @Test
  void testGetFunctionalitiesBySuperAdmin_withHashClient() {
    when(this.functionalitiesRepository.findAllByClientFunctionalitiesDetailsClientId(anyLong()))
        .thenReturn(MOCK_FUNCTIONALITY_LIST);
    var result = clientService.getFunctionalitiesByCurrentInvokedUser(1);

    Assertions.assertNotEquals(
        0, result.size(), "List all functionalities must not be null and empty.");
    log.info("result: {}", result);
  }

  @Test
  void testGetFunctionalitiesBySuperAdmin_withoutClient() {
    this.mockGetFunctionalitiesByClientId();
    var result = clientService.getFunctionalitiesByCurrentInvokedUser(1);

    Assertions.assertNotEquals(
        0, result.size(), "List all functionalities must not be null and empty.");
    log.info("result: {}", result);
  }

  @Test
  void testGetFunctionalitiesByCustomerAdmin() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      this.mockPrivilegeClientAdmin();
      when(this.userRepository.loadOrganizationUser(any()))
          .thenReturn(Optional.of(this.mockLoadUserLoadOrganization()));
      when(this.functionalitiesRepository.findAllByClientFunctionalitiesDetailsClientId(anyLong()))
          .thenReturn(MOCK_FUNCTIONALITY_LIST);
      var result = clientService.getFunctionalitiesByCurrentInvokedUser(1);

      Assertions.assertNotEquals(
          0, result.size(), "List all functionalities must not be null and empty.");
      log.info("result: {}", result);
    }
  }

  @Test
  void testGetAllClientFillers_thenReturnSuccess() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);

      when(this.keycloakService.getUserInfo(anyString())).thenReturn(USER);

      this.clientService.setSupperAdmin(false);

      when(this.clientRepository.getClientIdByUserId(anyString())).thenReturn(Optional.of(1L));
      when(this.clientFillersRepository.findByClientIdAndEnabledTrue(anyLong()))
          .thenReturn(List.of(ProfileUnitTestConstants.CLIENT_FILLER));

      var result = this.clientService.getAllClientFillers(1L);

      Assertions.assertNotNull(result, "All client fillers must not be null and empty.");
      log.info("result :{}", result);
    }
  }

  @Test
  void testGetAllClientFillers_thenThrowProfileNotBelongToServiceException() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);

      when(this.keycloakService.getUserInfo(anyString())).thenReturn(USER_INVALID);

      this.clientService.setSupperAdmin(false);

      when(this.clientRepository.getClientIdByUserId(anyString()))
          .thenThrow(new ProfileNotBelongToServiceException(TEST_USERNAME));

      Assertions.assertThrows(
          ProfileNotBelongToServiceException.class,
          () -> this.clientService.getAllClientFillers(0L));
    }
  }

  @Test
  void testGetAllClientFillersBySupperAdmin_thenReturnSuccess() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      this.clientService.setSupperAdmin(true);

      when(this.clientFillersRepository.findByClientIdAndEnabledTrue(anyLong()))
          .thenReturn(List.of(ProfileUnitTestConstants.CLIENT_FILLER));
      // when(this.keycloakService.getUserInfo(anyString())).thenReturn(USER);
      // when(this.clientRepository.getClientIdByUserId(anyString())).thenReturn(Optional.of(1L));
      var result = this.clientService.getAllClientFillers(1L);

      Assertions.assertNotNull(result, "All client fillers must not be null and empty.");
      log.info("result :{}", result);
    }
    }

  @Test
  void testGetAllClientFillersBySupperAdmin_thenReturnFail() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
      .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
      .thenReturn(TEST_USERNAME);
      
      Assertions.assertThrows(
          ClientNotFoundException.class, () -> this.clientService.getAllClientFillers(null));
    }
    }

  @ParameterizedTest
  @MethodSource({"configurationVersionsSuccessParam"})
  void testGetConfigurationVersions_ThenReturnSuccess(String clientName) {
    PostalConfigurationVersion configurationVersion = PostalConfigurationVersion.builder()
        .id(1L)
        .version(1)
        .fileId("c9eafa34-8ad2-4868-950f-2012669e4d85")
        .ownerId(1L)
        .createdAt(new Date())
        .createdBy("super_admin@gmail.com")
        .build();

    try (MockedStatic<AuthenticationUtils> mockAuthentication = mockStatic(
        AuthenticationUtils.class)) {
      mockAuthentication.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      when(this.settingFeignClient.getCustomerConfigurationVersions(anyString(), anyString()))
          .thenReturn(List.of(configurationVersion));

      var result = this.clientService.getPostalConfigurationVersions(clientName);
      Assertions.assertNotNull(result, "The postal configuration version must not be null");
      log.info("Postal configuration version: {}", result);
    }
  }

  @ParameterizedTest
  @MethodSource({"configurationVersionByVersionNumberSuccessParam"})
  @Order(24)
  void testGetConfigurationVersionByVersionNumber_ThenReturnSuccess(String clientName,
      int version) {
    List<ConfigurationEntry> configurationEntries = new ArrayList<>();
    configurationEntries.add(new ConfigurationEntry("PathIn",
        "/apps/cxm/common/logidoc/Go2PDF/acquisition/go2pdf/" + "DEFAULT" + "/in/"));

    configurationEntries.add(new ConfigurationEntry("PathTemp",
        "/apps/cxm/common/logidoc/Go2PDF/acquisition/go2pdf/" + "PORTAIL" + "/tmp/"));

    Configuration configuration = new Configuration(1, "", configurationEntries);

    PostalConfigurationVersion postalConfigurationVersion = PostalConfigurationVersion.builder()
        .id(1L)
        .version(version)
        .fileId("c9eafa34-8ad2-4868-950f-2012669e4d85")
        .ownerId(1L)
        .createdAt(new Date())
        .createdBy("super_admin@gmail.com")
        .build();

    PostalConfigurationVersionDto postalConfigurationVersionDto = new PostalConfigurationVersionDto();
    postalConfigurationVersionDto.setConfigurationVersion(postalConfigurationVersion);
    postalConfigurationVersionDto.setConfigurations(List.of(configuration));

    try (MockedStatic<AuthenticationUtils> mockAuthentication = mockStatic(
        AuthenticationUtils.class)) {
      mockAuthentication.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      when(this.settingFeignClient.getCustomerConfigurationVersion(anyString(), anyInt(),
          anyString()))
          .thenReturn(postalConfigurationVersionDto);

      var result = this.clientService.getPostalConfigurationVersion(clientName, version);
      Assertions.assertNotNull(result, "The postal configuration version must not be null");
      log.info("Postal configuration version: {}", result);
    }
  }

  private static Stream<Arguments> configurationVersionByVersionNumberSuccessParam() {
    String customer = "CUSTOMER_TEST";
    return Stream.of(Arguments.of(customer, 1));
  }

  private static Stream<Arguments> configurationVersionsSuccessParam() {
    String clientName = "CUSTOMER_TEST";
    return Stream.of(Arguments.arguments(clientName));
  }

  private void mockPrivilegeClientAdmin() {
    this.clientService.setSupperAdmin(false);
    lenient()
        .when(this.profileService.notContainsPrivilege(any(String.class), any(String.class)))
        .thenReturn(false);
  }

  private void mockPrivilegeNotSuperAdminAndNotClientAdmin() {
    this.clientService.setSupperAdmin(false);
    when(this.profileService.notContainsPrivilege(any(String.class), any(String.class)))
        .thenReturn(true);
  }

  private void mockGetFunctionalitiesByClientId() {
    when(this.functionalitiesRepository.findAllByClientFunctionalitiesDetailsClientId(anyLong()))
        .thenReturn(MOCK_FUNCTIONALITY_LIST);
  }

  private void mockFindAllByCollectionFuncKeys() {
    List<Functionalities> mockFunctions =
        new ArrayList<>(
            Arrays.asList(
                new Functionalities(1, "cxm_flow_traceability", new ArrayList<>()),
                new Functionalities(2, "cxm_template", new ArrayList<>()),
                new Functionalities(3, "cxm_client_management", new ArrayList<>()),
                new Functionalities(4, "cxm_user_management", new ArrayList<>())));
    when(functionalitiesRepository.findAllByKeyIn(any())).thenReturn(mockFunctions);
  }

  private void mockFunctionalityPrivilege() {
    var funcDetails =
        Map.of(
            "key",
            "cxm_client_management",
            "subValue",
            MOCK_FUNCTIONALITY_PRIVILEGES,
            "value",
            "Client management");
    Map<String, Object> functionalities = Map.of("functionalities", List.of(funcDetails));
    when(this.profileService.getFunctionalityPrivilege(anyString())).thenReturn(functionalities);
  }

  private LoadOrganization mockLoadUserLoadOrganization() {
    return new LoadOrganization() {
      @Override
      public long getServiceId() {
        return 1;
      }

      @Override
      public long getDivisionId() {
        return 1;
      }

      @Override
      public long getClientId() {
        return 1;
      }
    };
  }

  private ClientInfo loadMockClient() {
    return new ClientInfo() {
      @Override
      public Date getCreatedAt() {
        return new Date();
      }

      @Override
      public Date getLastModified() {
        return new Date();
      }

      @Override
      public String getCreatedBy() {
        return "super.admin@tessi.fr";
      }

      @Override
      public String getLastModifiedBy() {
        return "super.admin@tessi.fr";
      }

      @Override
      public long getId() {
        return 1;
      }

      @Override
      public String getName() {
        return "Tessi";
      }

      @Override
      public String getEmail() {
        return "tessi@gmail.com";
      }

      @Override
      public String getContactFirstName() {
        return null;
      }

      @Override
      public String getContactLastname() {
        return null;
      }

      @Override
      public String getFileId() {
        return "";
      }

      @Override
      public String getFilename() {
        return "";
      }

      @Override
      public long getFileSize() {
        return 0;
      }
    };
  }

  @Test
  void testModifiedINIConfiguration_thenReturnSuccess() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils.when(AuthenticationUtils::getAuthToken)
          .thenReturn("Bearer token");

      when(this.settingFeignClient.modifiedINIConfiguration(any(PostalConfigurationDto.class),
          anyString()))
          .thenReturn(ProfileUnitTestConstants.POSTAL_CONFIGURATION_DTO);

      var response = this.clientService.modifiedINIConfiguration(
          ProfileUnitTestConstants.POSTAL_CONFIGURATION_DTO);

      Assertions.assertNotNull(response, "Response must be not null");
    }
  }
}
