package com.tessi.cxm.pfl.ms5.service;

import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.MOCK_PROFILE_CREATED_BY_SUPER_ADMIN;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.MOCK_SAVED_USER_ENTITY;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.MOCK_USER_ADMIN_INFO;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER_ENTITY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.UserRequestResetPasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserResetPasswordDto;
import com.tessi.cxm.pfl.ms5.entity.UserRequestResetPassword;
import com.tessi.cxm.pfl.ms5.exception.KeycloakUserNotFound;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRequestResetPasswordRepository;
import com.tessi.cxm.pfl.ms5.service.specification.PasswordArchiveService;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.mail.MailHandlerService;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthResponse;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserServiceTest {

  private static final String TEST_USERNAME = "tessi_testing";
  private static final String adminId = "1";
  private final ModelMapper modelMapper = new ModelMapper();
  @Mock ProfileService profileService;
  @Mock KeycloakSpringBootProperties keycloakSpringBootProperties;
  @Mock private UserRepository userRepository;
  @Mock private KeycloakService keycloakService;
  @Mock private DepartmentService departmentService;
  @Mock private DepartmentRepository departmentRepository;
  @Mock private ProfileRepository profileRepository;
  @Mock private UserProfileRepository userProfileRepository;
  private MockUserService userService;
  @Mock private MailHandlerService mailSender;
  @Mock private ClientRepository clientRepository;
  @Mock private UserHubService userHubService;
  @Mock private UserRequestResetPasswordRepository userRequestResetPasswordRepository;
  @Mock private HubDigitalFlow hubDigitalFlow;
  @Mock private PasswordArchiveService passwordArchiveService;

  @BeforeEach
  void beforeEach() {
    this.userService =
        new MockUserService(
            departmentRepository,
            keycloakService,
            modelMapper,
            userRepository,
            departmentService,
            profileRepository,
            userProfileRepository,
            userRequestResetPasswordRepository,
            passwordArchiveService);

    ReflectionTestUtils.setField(this.userService, "defaultGroup", "tessi");
    ReflectionTestUtils.setField(
        this.userService, "requestResetPasswordBaseUrl", "http://localhost:5000/forgot-password");
    ReflectionTestUtils.setField(this.userService, "durationTokenExpired", Duration.ofMinutes(15));
    ReflectionTestUtils.setField(this.userService, "adminUserId", adminId);
    TransactionSynchronizationManager.initSynchronization();
    this.userService.setUserHubService(userHubService);
    this.userService.setHubDigitalFlowFeignClient(hubDigitalFlow);
    this.userService.setClientRepository(clientRepository);
    this.userService.setProfileService(profileService);
    this.userService.setKeycloakSpringBootProperties(keycloakSpringBootProperties);
    this.userService.setUserId(TEST_USERNAME);
  }

        @AfterEach
        public void clear() {
                TransactionSynchronizationManager.clear();
        }

        @InjectMocks
        private User user;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        @Order(1)
        void testCreateUser() {
                try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
                        // Mock value
                        User user = new User();
                        user.setId("1");
                        user.setFirstName("Tessi");
                        user.setLastName("Post");
                        user.setUsername("tessi@gmail.com");
                        user.setEmail("tessi@gmail.com");

                        utils
                                        .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
                                        .thenReturn(TEST_USERNAME);
                        utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);
                        when(this.userRepository.loadOrganizationUser(anyString()))
                                        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_USER_ORGANIZATION));
                        when(this.clientRepository.findClientByServiceId(anyLong()))
                                        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));
                        when(this.keycloakService.createUser(any(User.class), anyString(), anyList()))
                                        .thenReturn(user);
                        when(this.profileRepository.findById(anyLong()))
                                        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_PROFILE));
                        when(this.profileService.getAllProfilesCriteria(anyLong()))
                                        .thenReturn(
                                                        java.util.List.of(
                                                                        ProfileUnitTestConstants.SAMPLE_PROFILE_FILTER_CRITERIA_1,
                                                                        ProfileUnitTestConstants.SAMPLE_PROFILE_FILTER_CRITERIA_2,
                                                                        ProfileUnitTestConstants.SAMPLE_PROFILE_FILTER_CRITERIA_3));
                        lenient()
                                        .when(this.profileRepository.findByClientIdAndCreatedBy(anyLong(), anyString()))
                                        .thenReturn(Optional.of(MOCK_PROFILE_CREATED_BY_SUPER_ADMIN));
                        lenient()
                                        .when(departmentService.findEntity(anyLong()))
                                        .thenReturn(ProfileUnitTestConstants.SAMPLE_DEPARTMENT);
                        lenient().when(this.keycloakService.getUserInfo(any())).thenReturn(MOCK_USER_ADMIN_INFO);
                        lenient().when(this.clientRepository.getClientIdByUserId(any())).thenReturn(Optional.of(1L));
                        lenient()
                                        .when(this.keycloakService.findUserById(any()))
                                        .thenReturn(Optional.of(MOCK_USER_ADMIN_INFO));
                        when(this.userRepository.saveAndFlush(any())).thenReturn(MOCK_SAVED_USER_ENTITY);

                        // call to the service.
                        var result = userService.createUser(ProfileUnitTestConstants.SAMPLE_CREATE_USER_REQUEST_DTO);
                        Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
                }
        }

        @Test
        @Order(2)
        void requestForResetPasswordTest() {

                UserRequestResetPassword userRequestResetPassword = new UserRequestResetPassword();
                userRequestResetPassword.setEmail(getUserRepresentation().getEmail());
                userRequestResetPassword.setToken("IamToken");
                userRequestResetPassword.setUsername("");
                UserRequestResetPasswordDto userRequestResetPasswordDto = new UserRequestResetPasswordDto(
                                "pisey@gmail.com");
                when(this.userHubService.getUserHub(anyString()))
                                .thenReturn(new UserHubAccount("Hello", "Hello"));
                when(this.hubDigitalFlow.getAuthToken(any())).thenReturn(new AuthResponse("sss"));
                ReflectionTestUtils.invokeMethod(
                                this.userService, this.userService.getClass(), "getDurationOfTokenExpired");
                ReflectionTestUtils.invokeMethod(
                                this.userService,
                                this.userService.getClass(),
                                "buildResetPasswordLink",
                                userRequestResetPassword);
                ReflectionTestUtils.invokeMethod(
                                this.userService,
                                this.userService.getClass(),
                                "sendResetPasswordByEmailForm",
                                userRequestResetPassword);
                when(this.keycloakService.getUserRepresentationByEmail(anyString()))
                                .thenReturn(Optional.of(getUserRepresentation()));
                when(this.userRequestResetPasswordRepository.existsByEmail("pisey@gmail.com"))
                                .thenReturn(false);
                when(this.userRequestResetPasswordRepository.save(any())).thenReturn(userRequestResetPassword);
                final UserRequestResetPasswordDto userRequestResetPasswordDto1 = this.userService
                                .requestForResetPassword(userRequestResetPasswordDto);
                Assertions.assertNotNull(userRequestResetPasswordDto1, ProfileUnitTestConstants.SMG);
        }

        @Test
        @Order(3)
        void resetPasswordTest() {

                UserRequestResetPassword userRequestResetPassword = new UserRequestResetPassword();
                userRequestResetPassword.setEmail(getUserRepresentation().getEmail());

                userRequestResetPassword.setUsername("pisey");
                Date currentDateTime = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDateTime);
                calendar.add(Calendar.MINUTE, 1);
                currentDateTime = calendar.getTime();
                userRequestResetPassword.setExpiredDate(currentDateTime);
                UserResetPasswordDto userResetPasswordDto = new UserResetPasswordDto(UUID.randomUUID().toString(),
                                "123");
                when(this.userRequestResetPasswordRepository.findByToken(anyString()))
                                .thenReturn(Optional.of(userRequestResetPassword));
                when(this.keycloakService.getUserInfo(userRequestResetPassword.getUsername()))
                                .thenReturn(new User());
                doNothing().when(this.keycloakService).resetPassword(any());
                doNothing().when(this.userRequestResetPasswordRepository).deleteByEmail(anyString());
                final UserResetPasswordDto result = this.userService.resetPassword(userResetPasswordDto);
                Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
        }

        @Test
        @Order(4)
        void loadNormalUserInfoSuccess() {
                try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
                        utils
                                        .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
                                        .thenReturn(TEST_USERNAME);

                        utils
                                        .when(() -> AuthenticationUtils
                                                        .getPrincipalIdentifier(any(Authentication.class)))
                                        .thenReturn(TEST_USERNAME);

                        utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);

                        when(this.keycloakService.findUserById(anyString()))
                                        .thenReturn(Optional.of(ProfileUnitTestConstants.USER));

                        when(this.userRepository.findByUsernameAndIsActiveTrue(anyString()))
                                        .thenReturn(Optional.ofNullable(USER_ENTITY));

                        this.userService.setSupperAdmin(false);

                        final QueryUserResponseDTO responseDTO = this.userService.getUserInfoByToken();

                        Assertions.assertNotNull(responseDTO, ProfileUnitTestConstants.SMG);
                        log.info("response: {}", responseDTO);
                }
        }

        @Test
        @Order(5)
        void loadNormalUserInfoFail() {
                this.userService.setSupperAdmin(false);
                var result = Assertions.assertThrows(
                                KeycloakUserNotFound.class, () -> this.userService.getUserInfoByToken());

                log.info("Actual result: {0}", result);
        }

        @Test
        @Order(6)
        void loadUserAdminInfoSuccess() {
                try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
                        utils
                                        .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
                                        .thenReturn(TEST_USERNAME);

                        this.userService.setSupperAdmin(true);
                        when(userRepository.findByUsernameAndIsActiveTrue(anyString()))
                                        .thenReturn(Optional.of(USER_ENTITY));
                        Mockito.lenient()
                                        .when(keycloakService.findUserById(USER_ENTITY.getTechnicalRef()))
                                        .thenReturn(Optional.of(MOCK_USER_ADMIN_INFO));
                        final QueryUserResponseDTO responseDTO = this.userService.getUserInfoByToken();

                        Assertions.assertNotNull(responseDTO, ProfileUnitTestConstants.SMG);
                        log.info("response: {}", responseDTO);
                }
        }

        @Test
        @Order(7)
        void loadUserAdminInfoFail() {
                this.userService.setSupperAdmin(true);
                Assertions.assertThrows(
                                KeycloakUserNotFound.class, () -> this.userService.getUserInfoByToken());
        }

  @Test
  @Order(8)
  void updateNormalUserPasswordSuccess() {

    when(this.keycloakService.getUserInfo()).thenReturn(ProfileUnitTestConstants.USER);

    when(keycloakSpringBootProperties.getResource()).thenReturn("cxm-profile");

    when(this.keycloakSpringBootProperties.getCredentials())
        .thenReturn(Map.of("secret", "1212121212"));

    when(this.keycloakService.isUserPasswordCredentialValid(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(true);

    when(this.userRepository.findByTechnicalRefAndIsActiveTrue(anyString()))
        .thenReturn(Optional.of(USER_ENTITY));
    this.userService.setSupperAdmin(false);
    var result =
        this.userService.updateUserPassword(
            ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO);

    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
    log.info("Result: {}", result);
  }

  @Test
  @Order(9)
  void updateNormalUserPasswordFail() {

    when(this.keycloakService.getUserInfo()).thenReturn(ProfileUnitTestConstants.USER);

    when(keycloakSpringBootProperties.getResource()).thenReturn("cxm-profile");

    when(this.keycloakSpringBootProperties.getCredentials())
        .thenReturn(Map.of("secret", "1212121212"));

    when(this.keycloakService.isUserPasswordCredentialValid(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(true);

    this.userService.setSupperAdmin(false);

    Assertions.assertThrows(
        UserNotFoundException.class,
        () ->
            this.userService.updateUserPassword(
                ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO));
  }

  @Test
  @Order(10)
  void updateUserAdminPasswordSuccess() {
    when(this.keycloakService.getUserInfo()).thenReturn(ProfileUnitTestConstants.USER);

    when(keycloakSpringBootProperties.getResource()).thenReturn("cxm-profile");

    when(this.keycloakSpringBootProperties.getCredentials())
        .thenReturn(Map.of("secret", "1212121212"));

    when(this.keycloakService.isUserPasswordCredentialValid(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(true);

    this.userService.setSupperAdmin(true);
    var result =
        this.userService.updateUserPassword(
            ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO);

    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
    log.info("Result: {}", result);
  }

  @Test
  @Order(11)
  void updateUserAdminPasswordFail() {
    when(this.keycloakService.getUserInfo())
        .thenThrow(new KeycloakUserNotFound("Keycloak user not found"));
    this.userService.setSupperAdmin(true);
    Assertions.assertThrows(
        KeycloakUserNotFound.class,
        () ->
            this.userService.updateUserPassword(
                ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO));
  }

        private UserRepresentation getUserRepresentation() {
                UserRepresentation userRepresentation = new UserRepresentation();
                userRepresentation.setEmail("pisey@gmail.com");
                userRepresentation.setUsername("pisey");
                userRepresentation.setFirstName("chorn");
                userRepresentation.setLastName("pisey");
                userRepresentation.setOrigin("pisey");
                userRepresentation.setEnabled(true);
                userRepresentation.setEmailVerified(true);
                userRepresentation.setCreatedTimestamp(1L);
                return userRepresentation;
        }
}
