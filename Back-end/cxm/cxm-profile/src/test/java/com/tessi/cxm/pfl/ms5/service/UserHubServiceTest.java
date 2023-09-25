package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.config.HubAccountEncryptionProperties;
import com.tessi.cxm.pfl.ms5.entity.UserHub;
import com.tessi.cxm.pfl.ms5.exception.UserAPIFailureException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.UserHubRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthResponse;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserAPIRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubRequestDto;
import com.tessi.cxm.pfl.shared.service.encryption.AccountEncryption;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.SAMPLE_CLIENT_1;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.SMG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserHubServiceTest {
  private static final String TEST_USERNAME = "tessi.test@gmail.com";

  private UserHubService userHubService;
  @Mock private UserHubRepository userHubRepository;
  private final ModelMapper modelMapper = new ModelMapper();
  @Mock private ClientRepository clientRepository;
  @Mock private HubDigitalFlow hubDigitalFlow;

  @Mock private UserRepository userRepository;
  @Mock private UserService userService;
  @Mock private AESHelper aesHelper;
  @Mock private AccountEncryption accountEncryption;
  @Mock private HubAccountEncryptionProperties accountEncryptionProperties;
  @BeforeEach
  void setUp() {

    this.userHubService =
        new UserHubService(
            userHubRepository,
            modelMapper,
            clientRepository,
            hubDigitalFlow,
            userRepository,
            accountEncryption,
            aesHelper,
            userService,
            accountEncryptionProperties);

    ReflectionTestUtils.setField(this.userHubService, "defaultUsername", TEST_USERNAME);
    ReflectionTestUtils.setField(this.userHubService, "defaultPassword", TEST_USERNAME);
    ReflectionTestUtils.setField(this.userHubService, "adminUserId", TEST_USERNAME);
  }

  @Test
  void testGetHubAccount() {
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);
      UserHubAccount userHub = this.userHubService.getUserHubByTechnicalRef();
      Assertions.assertNotNull(userHub, SMG);
      log.info("UserHub :{}", userHub);
    }
  }

  @Test
  void testGetHubAccountByUsername() {
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);
      UserHubAccount userHub = this.userHubService.getUserHub(TEST_USERNAME);
      Assertions.assertNotNull(userHub, SMG);
      log.info("UserHub :{}", userHub);
    }
  }

  @Test
  void testRegisterUserHub() {
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);
      UserHubRequestDto userHubRequestDto =
          new UserHubRequestDto("tessi", TEST_USERNAME, TEST_USERNAME);
      UserHub userHub = this.modelMapper.map(userHubRequestDto, UserHub.class);
      when(this.clientRepository.findByName(anyString())).thenReturn(Optional.of(SAMPLE_CLIENT_1));
      when(this.userHubRepository.findByClientId(anyLong())).thenReturn(Optional.of(userHub));
      when(this.userHubRepository.save(userHub)).thenReturn(userHub);
      when(this.hubDigitalFlow.getAuthToken(any()))
          .thenReturn(new AuthResponse(UUID.randomUUID().toString()));
      when(this.hubDigitalFlow.registerUserAPI(any(), anyString()))
          .thenReturn(new UserAPIRequest("tessi", TEST_USERNAME, TEST_USERNAME));
      userHubRequestDto = this.userHubService.registerUserHub(userHubRequestDto);
      Assertions.assertNotNull(userHubRequestDto, SMG);
      log.info("UserHub :{}", userHubRequestDto);
    }
  }

  @Test
  void testFailRegisterUserHub() {
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME);
      UserHubRequestDto userHubRequestDto =
          new UserHubRequestDto("tessi", TEST_USERNAME, TEST_USERNAME);
      UserHub userHub = this.modelMapper.map(userHubRequestDto, UserHub.class);
      when(this.clientRepository.findByName(anyString())).thenReturn(Optional.of(SAMPLE_CLIENT_1));
      when(this.userHubRepository.findByClientId(anyLong())).thenReturn(Optional.of(userHub));
      when(this.hubDigitalFlow.getAuthToken(any()))
          .thenReturn(new AuthResponse(UUID.randomUUID().toString()));
      doThrow(new UserAPIFailureException("Fail to create user API in hub-digitalflow"))
          .when(this.hubDigitalFlow)
          .registerUserAPI(any(), anyString());
      UserAPIFailureException userAPIFailureException =
          Assertions.assertThrows(
              UserAPIFailureException.class,
              () -> this.userHubService.registerUserHub(userHubRequestDto));
      log.info("Error found :{}", userAPIFailureException.getMessage());
    }
  }
}
