package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.dto.CreateUserRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.CreateUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.dto.LoadClient;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.dto.ProfileDetailDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileFilterCriteria;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.UserAssignedProfileDTO;
import com.tessi.cxm.pfl.ms5.dto.UserAssignedServiceDTO;
import com.tessi.cxm.pfl.ms5.dto.UserInfoRequestUpdatePasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserOrganization;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.ClientFillers;
import com.tessi.cxm.pfl.ms5.entity.ClientFunctionalitiesDetails;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.Functionalities;
import com.tessi.cxm.pfl.ms5.entity.Privilege;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.ProfileDetails;
import com.tessi.cxm.pfl.ms5.entity.ReturnAddress;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserHub;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles;
import com.tessi.cxm.pfl.shared.model.Configuration;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthResponse;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerServiceProviderDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerServiceProvidersDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.ServiceProviderResponse;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.service.restclient.VisibilityLevel;
import com.tessi.cxm.pfl.shared.utils.MockTuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.modelmapper.ModelMapper;

public class ProfileUnitTestConstants {

  private ProfileUnitTestConstants() {
  }

  public static final String SMG = "Must be not null";
  public static final Privilege SAMPLE_PRIVILEGE;
  public static final Profile SAMPLE_PROFILE;
  public static final Client SAMPLE_CLIENT_1;
  public static final Client SAMPLE_CLIENT_2;
  public static final ClientDto SAMPLE_CLIENT_1_DTO;
  public static final ClientDto SAMPLE_CLIENT_2_DTO;
  public static final ProfileDetails SAMPLE_PROFILE_DETAILS;
  public static final ProfileDetailDto SAMPLE_PROFILE_DETAILS_DTO;
  private static final ModelMapper MODEL_MAPPER = new ModelMapper();
  public static final ProfileDto SAMPLE_PROFILE_DTO;
  public static final Division SAMPLE_DIVISION;
  public static final DivisionDto SAMPLE_DIVISION_DTO;
  public static final Department SAMPLE_DEPARTMENT;
  public static final DepartmentDto SAMPLE_DEPARTMENT_DTO;
  public static final CreateUserRequestDTO SAMPLE_CREATE_USER_REQUEST_DTO;
  public static final CreateUserResponseDTO SAMPLE_CREATE_USER_REQUEST;
  public static final LoadClient LOAD_CLIENT;
  public static final ClientDto MOCK_UPDATE_CLIENT_DTO;

  public static final Client MOCK_UPDATE_CLIENT;

  public static final Client MOCK_FIND_ONE_CLIENT;

  public static final Division MOCK_REMOVED_DIVISION;

  public static final List<Functionalities> MOCK_FUNCTIONALITY_LIST;

  public static final ClientDto MOCK_CLIENT_CUSTOMER_ADMIN;

  public static final ClientDto MOCK_CLIENT_RES_AFTER_SAVE;

  public static final List<Map<String, Object>> MOCK_FUNCTIONALITY_PRIVILEGES;

  public static final Profile MOCK_PROFILE_CREATED_BY_SUPER_ADMIN;

  public static final User MOCK_USER_ADMIN_INFO;

  public static final UserEntity MOCK_SAVED_USER_ENTITY;

  public static final ClientDto MOCK_CLIENT_SUPER_ADMIN;

  public static final QueryUserResponseDTO QUERY_USER_RESPONSE_DTO;

  public static final UserAssignedServiceDTO USER_ASSIGNED_SERVICE_DTO;

  public static final UserAssignedProfileDTO USER_ASSIGNED_PROFILE_DTO;

  public static final UserEntity USER_ENTITY;

  public static final User USER;

  public static final User USER_INVALID;

  public static final UserProfiles USER_PROFILES;

  public static final UserInfoRequestUpdatePasswordDto USER_INFO_REQ_UPDATE_PASSWORD_DTO;

  public static final SharedClientFillersDTO SHARED_CLIENT_FILLERS_DTO;

  public static final ClientFillers CLIENT_FILLER;

  public static final ProfileFilterCriteria SAMPLE_PROFILE_FILTER_CRITERIA_1;
  public static final ProfileFilterCriteria SAMPLE_PROFILE_FILTER_CRITERIA_2;
  public static final ProfileFilterCriteria SAMPLE_PROFILE_FILTER_CRITERIA_3;

  public static final LoadOrganization SAMPLE_USER_ORGANIZATION;

  static {
    CLIENT_FILLER =
        ClientFillers.builder().id(1L).key("Filler1").value("Filler1").enabled(true).build();

    SHARED_CLIENT_FILLERS_DTO = SharedClientFillersDTO.builder()
        .id(1L)
        .key("Filler1")
        .value("Filter 1")
        .enabled(true)
        .build();

    SAMPLE_DIVISION_DTO =
        DivisionDto.builder()
            .id(1)
            .name("Division 1")
            .clientId(1)
            .services(
                List.of(DepartmentDto.builder().id(1).name("Department1").divisionId(1).build()))
            .build();
    SAMPLE_CLIENT_1 =
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
                new ArrayList<>(
                    List.of(
                        Division.builder()
                            .id(1)
                            .name("Division 1")
                            .client(Client.builder().id(1).build())
                            .departments(
                                List.of(
                                    Department.builder()
                                        .id(1)
                                        .name("Department1")
                                        .division(Division.builder().id(1).build())
                                        .build()))
                            .build())))
            .clientFunctionalitiesDetails(
                new ArrayList<>(
                    List.of(
                        new ClientFunctionalitiesDetails(
                            1,
                            Client.builder().id(1).build(),
                            new Functionalities(1, "cxm_flow_traceability", new ArrayList<>())))))
            .clientUnloads(new ArrayList<>())
            .clientAllowUnloads(new ArrayList<>())
            .fillers(new ArrayList<>(List.of(CLIENT_FILLER)))
            .build();
    SAMPLE_CLIENT_2 =
        Client.builder()
            .id(2L)
            .name("Client2")
            .contactFirstName("Client2_Firstname")
            .contactLastname("Client2_Last")
            .email("client.2@gmail.com")
            .fileId("002")
            .filename("client1_privacy.pdf")
            .fileSize(1024)
            .build();
    SAMPLE_CLIENT_1_DTO =
        ClientDto.builder()
            .id(1L)
            .name("Client1")
            .contactFirstName("Client1_Firstname")
            .contactLastname("Client1_Last")
            .email("client.1@gmail.com")
            .fileId("001")
            .filename("client1_privacy.pdf")
            .fileSize(1024)
            .divisions(List.of(SAMPLE_DIVISION_DTO))
            .functionalities(new HashSet<>(Arrays.asList("cxm_flow_traceability", "cxm_template")))
            .unloads(new ArrayList<>())
            .fillers(new ArrayList<>(List.of(SHARED_CLIENT_FILLERS_DTO)))
            .build();
    SAMPLE_CLIENT_2_DTO =
        ClientDto.builder()
            .id(2L)
            .name("Client2")
            .contactFirstName("Client2_Firstname")
            .contactLastname("Client2_Last")
            .email("client.2@gmail.com")
            .fileId("002")
            .filename("client2_privacy.pdf")
            .fileSize(2024)
            .build();
    SAMPLE_DIVISION = Division.builder().id(1).name("Division 1").client(SAMPLE_CLIENT_1).build();

    SAMPLE_DEPARTMENT =
        Department.builder().id(1).name("Department1").division(SAMPLE_DIVISION).build();

    SAMPLE_DEPARTMENT_DTO =
        DepartmentDto.builder()
            .id(1)
            .name("Department1")
            .divisionId(SAMPLE_DIVISION.getId())
            .build();
    SAMPLE_CREATE_USER_REQUEST_DTO =
        CreateUserRequestDTO.builder()
            .firstName("tessi")
            .lastName("post")
            .email("tessi@gmail.com")
            .password("123")
            .confirmedPassword("123")
            .profiles(List.of(1L, 2L, 3L))
            .admin(true)
            .serviceId(1L)
            .build();
    SAMPLE_CREATE_USER_REQUEST =
        CreateUserResponseDTO.builder().username("tessi").email("tessi@gmail.com").build();
    SAMPLE_PROFILE =
        Profile.builder()
            .client(SAMPLE_CLIENT_1)
            .id(1L)
            .name("Profile1")
            .displayName("Profile 1")
            .build();

    SAMPLE_PRIVILEGE =
        new Privilege(
            EmailingTemplatePrivilege.FROM_SCRATCH.getKey(),
            VisibilityLevel.CLIENT.getKey(),
            true,
            ModificationLevel.CLIENT.getKey(),
            true);

    SAMPLE_PROFILE_DETAILS =
        ProfileDetails.builder()
            .id(1L)
            .privileges(List.of(SAMPLE_PRIVILEGE))
            .functionalityKey(Functionality.DESIGN_EMAILING_TEMPLATE.getKey())
            .visibilityLevel(VisibilityLevel.CLIENT.getKey())
            .modificationLevel(ModificationLevel.CLIENT.getKey())
            .profile(SAMPLE_PROFILE)
            .build();

    SAMPLE_PROFILE_DETAILS_DTO = MODEL_MAPPER.map(SAMPLE_PROFILE_DETAILS, ProfileDetailDto.class);
    SAMPLE_PROFILE_DTO =
        new ProfileDto(
            1L,
            "Concepteur",
            "Concepteur MOA",
            new ArrayList<>(),
            new Date(),
            "",
            new Date(),
            1L,
            "Tessi", 1L);
    LOAD_CLIENT = LoadClient.builder().id(1).name("Tessi").email("tessi@gmail.com").build();

    MOCK_UPDATE_CLIENT_DTO =
        ClientDto.builder()
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
                    DivisionDto.builder()
                        .id(1)
                        .name("Division 1")
                        .clientId(1)
                        .services(
                            List.of(
                                DepartmentDto.builder()
                                    .id(0)
                                    .name("New department")
                                    .divisionId(1)
                                    .build(),
                                DepartmentDto.builder()
                                    .id(0)
                                    .name("New department1")
                                    .divisionId(1)
                                    .build()))
                        .build(),
                    DivisionDto.builder()
                        .id(0)
                        .name("New division")
                        .clientId(1)
                        .services(
                            List.of(
                                DepartmentDto.builder()
                                    .id(0)
                                    .name("New department")
                                    .divisionId(0)
                                    .build(),
                                DepartmentDto.builder()
                                    .id(0)
                                    .name("New department1")
                                    .divisionId(0)
                                    .build()))
                        .build()))
            .functionalities(new HashSet<>(Arrays.asList("cxm_flow_traceability", "cxm_template")))
            .unloads(new ArrayList<>())
            .fillers(new ArrayList<>(List.of(SHARED_CLIENT_FILLERS_DTO)))
            .build();

    MOCK_UPDATE_CLIENT =
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
                new ArrayList<>(
                    Arrays.asList(
                        Division.builder()
                            .id(1)
                            .name("Division 1")
                            .client(Client.builder().id(1).build())
                            .departments(
                                List.of(
                                    Department.builder()
                                        .id(0)
                                        .name("New department")
                                        .division(Division.builder().id(1L).build())
                                        .build(),
                                    Department.builder()
                                        .id(0)
                                        .name("New department1")
                                        .division(Division.builder().id(1L).build())
                                        .build()))
                            .build(),
                        Division.builder()
                            .id(0)
                            .name("New division")
                            .client(Client.builder().id(1).build())
                            .departments(
                                List.of(
                                    Department.builder()
                                        .id(0)
                                        .name("New department")
                                        .division(Division.builder().id(1L).build())
                                        .build(),
                                    Department.builder()
                                        .id(0)
                                        .name("New department1")
                                        .division(Division.builder().id(1L).build())
                                        .build()))
                            .build())))
            .clientFunctionalitiesDetails(
                new ArrayList<>(
                    List.of(
                        new ClientFunctionalitiesDetails(
                            1,
                            Client.builder().id(1).build(),
                            new Functionalities(1, "cxm_flow_traceability", new ArrayList<>())))))
            .clientAllowUnloads(new ArrayList<>())
            .clientUnloads(new ArrayList<>())
            .fillers(new ArrayList<>(List.of(CLIENT_FILLER)))
            .build();

    MOCK_FIND_ONE_CLIENT =
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
                new ArrayList<>(
                    Arrays.asList(
                        Division.builder()
                            .id(1)
                            .name("Division 1")
                            .client(Client.builder().id(1).build())
                            .departments(
                                List.of(
                                    Department.builder()
                                        .id(1)
                                        .name("Department1")
                                        .division(Division.builder().id(1).build())
                                        .build()))
                            .build(),
                        Division.builder()
                            .id(2)
                            .name("Division 2")
                            .client(Client.builder().id(1).build())
                            .departments(
                                List.of(
                                    Department.builder()
                                        .id(1)
                                        .name("Department1")
                                        .division(Division.builder().id(1).build())
                                        .build()))
                            .build())))
            .clientFunctionalitiesDetails(new ArrayList<>())
            .clientAllowUnloads(new ArrayList<>())
            .clientUnloads(new ArrayList<>())
            .fillers(new ArrayList<>(List.of(CLIENT_FILLER)))
            .build();

    MOCK_REMOVED_DIVISION =
        Division.builder()
            .id(2)
            .name("Division 2")
            .client(Client.builder().id(1).build())
            .departments(
                List.of(
                    Department.builder()
                        .id(1)
                        .name("Department1")
                        .division(Division.builder().id(1).build())
                        .build()))
            .build();

    MOCK_FUNCTIONALITY_LIST =
        new ArrayList<>(
            Arrays.asList(
                new Functionalities(1, "cxm_flow_traceability", List.of()),
                new Functionalities(2, "cxm_template", List.of())));

    MOCK_CLIENT_CUSTOMER_ADMIN =
        ClientDto.builder()
            .id(1L)
            .name("Client1")
            .contactFirstName("Client1_Firstname")
            .contactLastname("Client1_Last")
            .email("client.1@gmail.com")
            .fileId("001")
            .filename("client1_privacy.pdf")
            .fileSize(1024)
            .divisions(new ArrayList<>(List.of(SAMPLE_DIVISION_DTO)))
            .functionalities(new LinkedHashSet<>())
            .unloads(new ArrayList<>())
            .fillers(new ArrayList<>(List.of(SHARED_CLIENT_FILLERS_DTO)))
            .build();

    MOCK_CLIENT_RES_AFTER_SAVE =
        ClientDto.builder()
            .id(1L)
            .name("Client1")
            .contactFirstName("Client1_Firstname")
            .contactLastname("Client1_Last")
            .email("client.1@gmail.com")
            .fileId("001")
            .filename("client1_privacy.pdf")
            .fileSize(1024)
            .divisions(List.of(SAMPLE_DIVISION_DTO))
            .functionalities(new LinkedHashSet<>())
            .build();

    MOCK_FUNCTIONALITY_PRIVILEGES =
        List.of(
            Map.of(
                "key",
                "cxm_client_management_list",
                "value",
                "List all clients.",
                "isVisibility",
                false,
                "isModification",
                false),
            Map.of(
                "key",
                "cxm_client_management_modify",
                "value",
                "Modify a client.",
                "isVisibility",
                false,
                "isModification",
                false));

    MOCK_PROFILE_CREATED_BY_SUPER_ADMIN =
        Profile.builder()
            .client(SAMPLE_CLIENT_1)
            .id(1L)
            .name("Profile1")
            .displayName("Profile 1")
            .createdBy("super.admin@gmail.com")
            .build();

    MOCK_USER_ADMIN_INFO =
        new User(
            "uuid",
            "super.admin@tessi.com",
            "Super admin",
            "Admin",
            "super.admin@tessi.com",
            new Date(),
            "123");

    MOCK_SAVED_USER_ENTITY =
        UserEntity.builder()
            .id(1L)
            .email("tessi@gmail.com")
            .username("tessi@gmail.com")
            .isActive(true)
            .firstName("Tessi")
            .lastName("Post")
            .build();

    MOCK_CLIENT_SUPER_ADMIN =
        ClientDto.builder()
            .id(1L)
            .name("Client1")
            .contactFirstName("Client1_Firstname")
            .contactLastname("Client1_Last")
            .email("client.1@gmail.com")
            .fileId("001")
            .filename("client1_privacy.pdf")
            .fileSize(1024)
            .divisions(new ArrayList<>(List.of(SAMPLE_DIVISION_DTO)))
            .functionalities(
                new HashSet<>(Arrays.asList("cxm_flow_traceability", "cxm_client_management")))
            .build();

    USER_ASSIGNED_SERVICE_DTO = UserAssignedServiceDTO.builder()
        .id(1L)
        .name("Service TEST")
        .build();

    USER_ASSIGNED_PROFILE_DTO = UserAssignedProfileDTO.builder()
        .id(1L)
        .name("Profile TEST")
        .build();

    USER_PROFILES = new UserProfiles(UserEntity
        .builder()
        .id(1L)
        .email("tessi@gmail.com")
        .username("tessi@gmail.com")
        .isActive(true)
        .firstName("Tessi")
        .lastName("Post")
        .department(SAMPLE_DEPARTMENT)
        .build(), SAMPLE_PROFILE);

    USER_ENTITY =
        UserEntity.builder()
            .id(1L)
            .email("tessi@gmail.com")
            .username("tessi@gmail.com")
            .isActive(true)
            .firstName("Tessi")
            .lastName("Post")
            .technicalRef(UUID.randomUUID().toString())
            .department(SAMPLE_DEPARTMENT)
            .userProfiles(List.of(USER_PROFILES))
            .build();

    USER = new User("1",
        "Client TEST",
        "Client",
        "TEST",
        "client.test@gmail.com",
        new Date(),
        "123");

    USER_INVALID = new User("0",
        "",
        "",
        "",
        "",
        null,
        "");

    QUERY_USER_RESPONSE_DTO = QueryUserResponseDTO.builder()
        .id(1)
        .firstName("Client")
        .lastName("TEST")
        .email("client.test@gmail.com")
        .service(USER_ASSIGNED_SERVICE_DTO)
        .profiles(List.of(USER_ASSIGNED_PROFILE_DTO))
        .build();

    USER_INFO_REQ_UPDATE_PASSWORD_DTO = UserInfoRequestUpdatePasswordDto.builder()
        .currentPassword("123")
        .newPassword("123")
        .confirmPassword("123")
        .build();

    SAMPLE_PROFILE_FILTER_CRITERIA_1 = new ProfileFilterCriteria(1L, "Service-1");
    SAMPLE_PROFILE_FILTER_CRITERIA_2 = new ProfileFilterCriteria(2L, "Service-2");
    SAMPLE_PROFILE_FILTER_CRITERIA_3 = new ProfileFilterCriteria(3L, "Service-3");

    SAMPLE_USER_ORGANIZATION =
        new LoadOrganization() {
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

  public static final Configuration DEFAULT_CONFIG = new Configuration(1, "DEFAULT",
      List.of(
          new ConfigurationEntry("ServerName", "DOCKER_TESI-POST"),
          new ConfigurationEntry("ComputerName", "DOCKER_TESSI-POST")
      ));

  public static final Configuration PORTAIL_CONFIG = new Configuration(2, "PORTAIL",
      List.of(
          new ConfigurationEntry("Modele", "portail_gc_default"),
          new ConfigurationEntry("PathChargement",
              "/preprocessing/appli/reception_seul/reception_seul.pl")
      ));

  public static final Configuration PORTAIL_ANALYSE_CONFIG = new Configuration(3, "PORTAIL_ANALYSE",
      List.of(
          new ConfigurationEntry("Modele", "PORTAIL_ANALYSE"),
          new ConfigurationEntry("PathChargement",
              "/preprocessing/appli/reception_seul/reception_seul.pl")
      ));

  public static final Configuration PORTAIL_PREVIEW_CONFIG = new Configuration(3, "PORTAIL_ANALYSE",
      List.of(
          new ConfigurationEntry("Modele", "PORTAIL_PREVIEW"),
          new ConfigurationEntry("PathChargement",
              "/preprocessing/appli/reception_seul/reception_seul.pl")));

  public static final List<Configuration> CONFIGURATION_LIST = List.of(
      DEFAULT_CONFIG,
      PORTAIL_CONFIG,
      PORTAIL_ANALYSE_CONFIG,
      PORTAIL_PREVIEW_CONFIG
  );

  public static final PostalConfigurationDto POSTAL_CONFIGURATION_DTO = PostalConfigurationDto.builder()
      .client("client_test")
      .configurations(CONFIGURATION_LIST)
      .build();

    public static final CustomerServiceProvidersDto CUSTOMER_SERVICE_PROVIDERS_DTO =
            CustomerServiceProvidersDto.builder()
                    .customer("example_client")
                    .mail(List.of(new CustomerServiceProviderDto(
                        1L, 1, "SMTP", false)))
                    .sms(List.of(new CustomerServiceProviderDto(
                        1L, 1, "Retarus", false)))
                    .build();
    public static final UserHub USER_HUB =
            UserHub.builder()
                    .id(1L)
                    .username("example_username")
                    .password("example_password")
                    .client(SAMPLE_CLIENT_1)
                    .build();

    public static final AuthResponse AUTH_RESPONSE = AuthResponse.builder()
        .token("eyJhbGciOiJSUzI1NiJ9.eyJ1c2VybmFtZSI6ImFwaV9zaWduX3Rlc3NpQGNhYS5m")
        .build();

    public static final ServiceProviderResponse SERVICE_PROVIDER_RESPONSE =
        ServiceProviderResponse.builder()
            .channel("SMS")
            .build();

  public static List<MockTuple> getMockUserOrganization(String level) {
    final MockTuple mockQueryResponse = new MockTuple();
    mockQueryResponse.setTuple(0, 1L);
    mockQueryResponse.setTuple(1, "Service_TEST");
    mockQueryResponse.setTuple(2, 1L);
    mockQueryResponse.setTuple(3, "Division_TEST");
    mockQueryResponse.setTuple(4, 1L);
    mockQueryResponse.setTuple(5, "Client_TEST");

    List<MockTuple> mockUserOrganizations = new ArrayList<>();
    if (ModificationLevel.CLIENT.getKey().equals(level)) {
      mockUserOrganizations.add(mockQueryResponse);
      mockUserOrganizations.add(mockQueryResponse);
      mockUserOrganizations.add(mockQueryResponse);
    } else if (ModificationLevel.DIVISION.getKey().equals(level)) {
      mockUserOrganizations.add(mockQueryResponse);
      mockUserOrganizations.add(mockQueryResponse);
    } else {
      mockUserOrganizations.add(mockQueryResponse);
    }

    return mockUserOrganizations;
  }

  public static final UserPrivilegeDetails MOCK_USER_PRIVILEGE_DETAILS = UserPrivilegeDetails.builder()
      .privilegeType("modificationLevel")
      .level("owner")
      .nonLevelPrivilege(false)
      .relatedOwners(List.of(1L))
      .build();

  public static final LoadOrganization MOCK_LOAD_ORGANIZATION = new LoadOrganization() {
    @Override
    public long getServiceId() {
      return 1L;
    }

    @Override
    public long getDivisionId() {
      return 1L;
    }

    @Override
    public long getClientId() {
      return 1L;
    }
  };

  public static final Client CLIENT = Client.builder()
      .id(1L)
      .name("Client TEST")
      .build();
  public static final Division DIVISION = Division.builder()
      .id(1L)
      .name("Division TEST")
      .client(CLIENT)
      .build();

  public static final Department DEPARTMENT = Department.builder()
      .id(1L)
      .name("Service TEST")
      .division(DIVISION)
      .build();

  public static final Profile PROFILE_TEST = Profile.builder()
      .id(1L)
      .name("Profile TEST")
      .client(CLIENT)
      .ownerId(1L)
      .build();

  public static final Profile PROFILE_DEV_TEST = Profile.builder()
      .id(1L)
      .name("DEV TEST")
      .client(CLIENT)
      .ownerId(1L)
      .build();

  public static final UserOrganization USER_ORGANIZATION =
      UserOrganization.builder()
          .clientId(1L)
          .clientName("CLIENT_TEST")
          .divisionId(1L)
          .divisionName("DIV A")
          .serviceId(1L)
          .serviceName("SER A")
          .build();

  public static final UserRecord USER_RECORD =
      UserRecord.builder()
          .client("CLIENT_TEST")
          .firstName("Dev")
          .lastName("TEST")
          .email("dev.test@gmail.com")
          .division("DIV A")
          .service("SER A")
          .profiles(List.of("Profile TEST"))
          .build();

  public static final ReturnAddress MOCK_RETURN_ADDRESS_CLIENT =
      ReturnAddress.builder()
          .id(1L)
          .refId(1L)
          .client(CLIENT)
          .type(AddressType.CLIENT)
          .line1("DEST_ADR1")
          .line2("DEST_ADR2")
          .line3("DEST_ADR3")
          .line4("DEST_ADR4")
          .build();

  public static final ReturnAddress MOCK_RETURN_ADDRESS_DIVISION =
      ReturnAddress.builder()
          .id(2L)
          .refId(2L)
          .client(CLIENT)
          .type(AddressType.DIVISION)
          .line1("DEST_ADR1")
          .line2("DEST_ADR2")
          .line3("DEST_ADR3")
          .line4("DEST_ADR4")
          .build();

  public static final ReturnAddress MOCK_RETURN_ADDRESS_SERVICE =
      ReturnAddress.builder()
          .id(3L)
          .refId(3L)
          .client(CLIENT)
          .type(AddressType.SERVICE)
          .line1("DEST_ADR1")
          .line2("DEST_ADR2")
          .line3("DEST_ADR3")
          .line4("DEST_ADR4")
          .build();

  public static final ReturnAddress MOCK_RETURN_ADDRESS_USER =
      ReturnAddress.builder()
          .id(4L)
          .refId(4L)
          .client(CLIENT)
          .type(AddressType.USER)
          .line1("DEST_ADR1")
          .line2("DEST_ADR2")
          .line3("DEST_ADR3")
          .line4("DEST_ADR4")
          .build();
  public static final List<ReturnAddress> MOCK_RETURN_ADDRESSES =
      List.of(
          MOCK_RETURN_ADDRESS_CLIENT,
          MOCK_RETURN_ADDRESS_DIVISION,
          MOCK_RETURN_ADDRESS_SERVICE,
          MOCK_RETURN_ADDRESS_USER);
}
