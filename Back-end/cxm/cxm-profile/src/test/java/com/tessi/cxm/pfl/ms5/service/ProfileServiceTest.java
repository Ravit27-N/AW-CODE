package com.tessi.cxm.pfl.ms5.service;

import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.MOCK_USER_ADMIN_INFO;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.SAMPLE_PROFILE_DETAILS;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER_ENTITY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.ProfileDetailDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.entity.LoadUserPrivilegeDetailsImp;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserDetails;
import com.tessi.cxm.pfl.ms5.exception.PrivilegeKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileDetailsRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;

/**
 * To testing functionality of {@link ProfileService}
 *
 * @author Sakal TUM
 * @author Sokhour LACH
 * @see 16/12/2021
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
class ProfileServiceTest {

  private static final long PROFILE_ID = 1L;
  @Mock private ProfileRepository profileRepository;
  @Mock private ProfileDetailsRepository profileDetailsRepository;
  @Mock private ClientRepository clientRepository;
  @Mock private UserProfileRepository userProfileRepository;
  @Mock private ClientService clientService;
  @Mock private UserRepository userRepository;
  @Mock private KeycloakService keycloakService;
  @Mock private DepartmentRepository departmentRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final ModelMapper modelMapper = new ModelMapper();
  private MockProfileService profileService;
  private static final String TEST_USERNAME = "user_testing";
  private static final String TEST_USERNAME_ADMIN_ID = "user_admin";
  private final ProfileDto profileDto = ProfileUnitTestConstants.SAMPLE_PROFILE_DTO;
  private final Profile profile = ProfileUnitTestConstants.SAMPLE_PROFILE;
  private static final String EXPECTED_MESSAGE = "Result should be not null.";

  @BeforeEach
  void setup() {
    profileService =
        new MockProfileService(
            this.modelMapper,
            this.profileRepository,
            this.profileDetailsRepository,
            this.clientRepository,
            this.userProfileRepository,
            this.clientService,
            this.userRepository,
            this.keycloakService,
            this.objectMapper,
            this.departmentRepository);
    profileService.setAdminUserId(TEST_USERNAME_ADMIN_ID);
  }

  @Test
  @Order(1)
  void successGetProfileDetailsById() {
    // Stub
    Mockito.when(this.profileDetailsRepository.findAllByProfileId(PROFILE_ID))
        .thenReturn(Collections.singletonList(SAMPLE_PROFILE_DETAILS));
    // Executing
    List<ProfileDetailDto> profileDetailsList =
        this.profileService.getProfileDetailsByProfileId(PROFILE_ID);
    // Verify
    Assertions.assertNotNull(profileDetailsList);
    Assertions.assertFalse(profileDetailsList.isEmpty());
    Assertions.assertEquals(PROFILE_ID, profileDetailsList.get(0).getId());
  }

  @Test
  @Order(2)
  void successGetProfileDetailsByIDReturnEmptyArrayWhenProfileIsNotFound() {
    // Stub
    Mockito.when(this.profileDetailsRepository.findAllByProfileId(PROFILE_ID))
        .thenReturn(Collections.emptyList());
    // Executing
    List<ProfileDetailDto> profileDetailsList =
        this.profileService.getProfileDetailsByProfileId(PROFILE_ID);
    // Verify
    Assertions.assertNotNull(profileDetailsList);
    Assertions.assertTrue(profileDetailsList.isEmpty());
  }

  @Test
  @Order(3)
  @DisplayName("Test create a profile successfully")
  void testCreateProfile() {
    var user = new User();
    user.setId("1");
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // Set up
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      utils
          .when(() -> AuthenticationUtils.getPrincipalIdentifier(any(Authentication.class)))
          .thenReturn(TEST_USERNAME_ADMIN_ID);
      utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME_ADMIN_ID);
      when(this.keycloakService.getUserInfo()).thenReturn(MOCK_USER_ADMIN_INFO);

      when(profileRepository.save(any(Profile.class))).thenReturn(profile);
      // call with service
      var result = this.profileService.createProfile(profileDto);
      // expected
      Assertions.assertNotNull(result, EXPECTED_MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Test
  @Order(4)
  @DisplayName("Test update a profile successfully")
  void testUpdateProfile() {
    var user = new User();
    user.setId("1");
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // mock object
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(BearerAuthentication.FAKE_USERNAME);
      utils
          .when(() -> AuthenticationUtils.getPrincipalIdentifier(any(Authentication.class)))
          .thenReturn(TEST_USERNAME_ADMIN_ID);
      utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME_ADMIN_ID);
      profile.setProfileDetails(new ArrayList<>(Collections.singletonList(SAMPLE_PROFILE_DETAILS)));
      when(profileRepository.findById(anyLong())).thenReturn(Optional.of(profile));
      when(profileRepository.save(any(Profile.class))).thenReturn(profile);

      // call with service
      var result = profileService.update(profileDto);

      // expected
      Assertions.assertNotNull(result, EXPECTED_MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Order(5)
  @DisplayName("Test get privilege and related owner by function and privilege key")
  @Test
  void testGetPrivilegeDetail() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // mock object
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(BearerAuthentication.FAKE_USERNAME);
      utils
          .when(() -> AuthenticationUtils.getPrincipalIdentifier(any(Authentication.class)))
          .thenReturn(TEST_USERNAME_ADMIN_ID);
      utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME_ADMIN_ID);
      when(this.keycloakService.getUserInfo(anyString())).thenReturn(USER);
      when(this.userProfileRepository.findAllProfileByUser(anyString())).thenReturn(List.of(1L));
      when(this.profileRepository.getAllPrivilegesOfProfile(any(), anyString()))
          .thenReturn(new ArrayList<>(Collections.singletonList(SAMPLE_PROFILE_DETAILS)));
      LoadUserPrivilegeDetailsImp loadUserPrivilegeDetailsImp = new LoadUserPrivilegeDetailsImp();
      loadUserPrivilegeDetailsImp.setId(1L);
      loadUserPrivilegeDetailsImp.setUsername("Test");
      when(this.userRepository.getAllUsersIdInClient(anyLong()))
          .thenReturn(List.of(loadUserPrivilegeDetailsImp));
      final UserPrivilegeDetails result =
          profileService.getUserPrivilegeDetails(true, FUNCTION_NAME, PRIVILEGE_KEY, true);

      // expected
      Assertions.assertNotNull(result, EXPECTED_MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  private static final String FUNCTION_NAME = "cxm_template";
  private static final String PRIVILEGE_KEY = "cxm_template_create_from_scratch";

  @Order(6)
  @DisplayName("Test get none privilege and related owner by function and privilege key")
  @Test
  void testGetNonePrivilegeDetail() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // mock object
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(BearerAuthentication.FAKE_USERNAME);
      utils
          .when(() -> AuthenticationUtils.getPrincipalIdentifier(any(Authentication.class)))
          .thenReturn(TEST_USERNAME_ADMIN_ID);
      utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME_ADMIN_ID);
      when(this.keycloakService.getUserInfo(anyString())).thenReturn(USER);
      when(this.userProfileRepository.findAllProfileByUser(anyString())).thenReturn(List.of(1L));
      var result =
          Assertions.assertThrows(
              PrivilegeKeyNotFoundException.class,
              () ->
                  profileService.getUserPrivilegeDetails(true, FUNCTION_NAME, PRIVILEGE_KEY, true),
              EXPECTED_MESSAGE);
      log.info("Result expected => {}", result.getMessage());
    }
  }

  @Order(7)
  @DisplayName("Test get privilege and related owner details by function and privilege key")
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testGetPrivilegeUserDetail(boolean isGettingRelatedOwners) {
    LoadUserDetails userDetails = mock(LoadUserDetails.class);
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // mock object
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(BearerAuthentication.FAKE_USERNAME);
      utils
          .when(() -> AuthenticationUtils.getPrincipalIdentifier(any(Authentication.class)))
          .thenReturn(TEST_USERNAME_ADMIN_ID);
      utils.when(AuthenticationUtils::getPrincipalIdentifier).thenReturn(TEST_USERNAME_ADMIN_ID);

      this.profileService.setUsername(USER_ENTITY.getUsername());
      when(this.userProfileRepository.findAllProfileByUser(anyString())).thenReturn(List.of(1L));
      when(this.keycloakService.getUserInfo(anyString())).thenReturn(USER);
      when(this.profileRepository.getAllPrivilegesOfProfile(any(), anyString()))
          .thenReturn(new ArrayList<>(Collections.singletonList(SAMPLE_PROFILE_DETAILS)));
      when(this.userRepository.findByTechnicalRefAndIsActiveTrue(anyString()))
          .thenReturn(Optional.of(USER_ENTITY));
      if (isGettingRelatedOwners) {
        when(this.userRepository.loadAllUsersDetailsIdInClient(anyLong()))
            .thenReturn(List.of(userDetails));
      }

      final var result =
          profileService.getUserPrivilegeDetailsOwner(
              true, FUNCTION_NAME, PRIVILEGE_KEY, isGettingRelatedOwners);

      // expected
      Assertions.assertNotNull(result, EXPECTED_MESSAGE);
      log.info(result.getUserDetailsOwners().size() + "");
      if (isGettingRelatedOwners) {
        Assertions.assertTrue(result.getUserDetailsOwners().size() > 1);
      } else {
        Assertions.assertEquals(1, result.getUserDetailsOwners().size());
      }
    }
  }
}
