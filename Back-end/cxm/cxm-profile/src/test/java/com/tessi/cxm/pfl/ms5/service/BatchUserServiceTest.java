package com.tessi.cxm.pfl.ms5.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.exception.CSVNotAcceptableException;
import com.tessi.cxm.pfl.ms5.exception.DataReaderHeaderNotAcceptable;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.core.flow.UserCreationManager;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.utils.MockTuple;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BatchUserServiceTest {

  private static final String TEST_USERNAME_ADMIN_ID = "user_admin";
  @Mock
  UserRepository userRepository;
  @Mock
  DepartmentRepository departmentRepository;
  @Mock
  private UserCreationManager creationManager;
  @Mock
  private ProfileService profileService;
  @Mock
  private ProfileRepository profileRepository;

  private MockBatchUserService batchUserService;

  @BeforeEach
  void beforeEach() {
    batchUserService = new MockBatchUserService(creationManager,
        userRepository, departmentRepository,
        profileService, profileRepository);

    batchUserService.setUserId(TEST_USERNAME_ADMIN_ID);
  }

  @ParameterizedTest
  @MethodSource("multipleParamWithLevel")
  @Order(1)
  void test_GetOrganizationLevel(String level, LoadOrganization loadOrganization,
      long expectedTotal) {
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier)
          .thenReturn(TEST_USERNAME_ADMIN_ID);

      when(this.departmentRepository.findAll(ArgumentMatchers.any(),
          ArgumentMatchers.any(), ArgumentMatchers.<Class<MockTuple>>any()))
          .thenReturn((ProfileUnitTestConstants.getMockUserOrganization(level)));

      var response = this.batchUserService.getOrgByLevel(level, loadOrganization);
      Assertions.assertEquals(expectedTotal, response.size());
    }
  }

  @ParameterizedTest
  @MethodSource("batchUserParameters")
  @Order(2)
  void createBatchUsers_ThenReturnSuccess(MultipartFile multipartFile, boolean isAdmin,
      long totalExpected,
      long successCountExpected, long errorCountExpected) {
    this.batchUserService.setSupperAdmin(isAdmin);

    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier)
          .thenReturn(TEST_USERNAME_ADMIN_ID);

      if (!isAdmin) {
        when(this.profileService.notContainsPrivilege(anyString(), anyString())).thenReturn(false);

        when(this.profileService.getUserPrivilegeDetails(anyBoolean(), anyString(), anyString(),
            anyBoolean()))
            .thenReturn(ProfileUnitTestConstants.MOCK_USER_PRIVILEGE_DETAILS);

        when(this.userRepository.loadOrganizationUser(anyString()))
            .thenReturn(Optional.of(ProfileUnitTestConstants.MOCK_LOAD_ORGANIZATION));
      }

      doNothing().when(this.creationManager)
          .create(any(UserRecord.class), any(ExecutionContext.class));

      var summary = batchUserService.create(multipartFile);
      Assertions.assertEquals(totalExpected, summary.getTotal());
      Assertions.assertEquals(successCountExpected, summary.getSuccessCount());
      Assertions.assertEquals(errorCountExpected, summary.getErrorCount());
      log.info("Summary: {}", summary);
    }
  }

  @ParameterizedTest
  @MethodSource("batchUserParametersWithException")
  @Order(3)
  void createBatchUsers_ThenThrowException(MultipartFile multipartFile, boolean isAdmin,
      Class exceptionExpected) {
    this.batchUserService.setSupperAdmin(isAdmin);

    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getPrincipalIdentifier)
          .thenReturn(TEST_USERNAME_ADMIN_ID);

      if (!isAdmin) {
        when(this.profileService.notContainsPrivilege(anyString(), anyString())).thenReturn(false);

        when(this.profileService.getUserPrivilegeDetails(anyBoolean(), anyString(), anyString(),
            anyBoolean()))
            .thenReturn(ProfileUnitTestConstants.MOCK_USER_PRIVILEGE_DETAILS);

        when(this.userRepository.loadOrganizationUser(anyString()))
            .thenReturn(Optional.of(ProfileUnitTestConstants.MOCK_LOAD_ORGANIZATION));
      }

      Throwable assertThrows = Assertions.assertThrows(exceptionExpected,
          () -> this.batchUserService.create(multipartFile));
      log.info("AssertThrows: {}", assertThrows.getMessage());
    }
  }

  private static Stream<Arguments> batchUserParameters() {
    String content1 = "Client;Prenom;Nom;Email;Division;Service;Profil\n"
        + "Pisey Dev;PISEY DEV;Pisey Dev;pisey.devx8@gmail.com;Div a;Ser a;Pisey Dev Privillege";

    MockMultipartFile mockMultipartFile1 =
        new MockMultipartFile("file", "batch-user.csv",
            "text/csv",
            content1.getBytes(StandardCharsets.UTF_8));

    String content2 = "Client;Prenom;Nom;Email;Division;Service;Profil\n"
        + "Pisey Dev;PISEY DEV;Pisey Dev;pisey.devx8@gmail.com;Div a;Ser a;Pisey Dev Privillege\n"
        + "CLIENT_TEST;Dev;TEST;dev.test@gmail.com;Div a;Ser a;Dev TEST Privillege";

    MockMultipartFile mockMultipartFile2 =
        new MockMultipartFile("file", "batch-user.csv",
            "text/csv",
            content2.getBytes(StandardCharsets.UTF_8));

    return Stream.of(
        Arguments.arguments(mockMultipartFile1, true, 1, 1, 0),
        Arguments.arguments(mockMultipartFile1, false, 1, 1, 0),
        Arguments.arguments(mockMultipartFile2, true, 2, 2, 0),
        Arguments.arguments(mockMultipartFile2, false, 2, 2, 0)
    );
  }

  private static Stream<Arguments> batchUserParametersWithException() {
    MockMultipartFile mockMultipartFile3 =
        new MockMultipartFile("file", "batch-user.pdf",
            "application/pdf",
            "".getBytes(StandardCharsets.UTF_8));

    String content4 = "Client,Prenom,Nom,Email,Division,Service,Profil\n"
        + "Pisey Dev,PISEY DEV,Pisey Dev,pisey.devx8@gmail.com,Div a,Ser a,Pisey Dev Privillege";

    MockMultipartFile mockMultipartFile4 =
        new MockMultipartFile("file", "batch-user.csv",
            "text/csv",
            content4.getBytes(StandardCharsets.UTF_8));

    String content5 = "Pisey Dev;PISEY DEV;Pisey Dev;pisey.devx8@gmail.com;Div a;Ser a;Pisey Dev Privillege";

    MockMultipartFile mockMultipartFile5 =
        new MockMultipartFile("file", "batch-user.csv",
            "text/csv",
            content5.getBytes(StandardCharsets.UTF_8));

    return Stream.of(
        Arguments.arguments(mockMultipartFile3, true, CSVNotAcceptableException.class),
        Arguments.arguments(mockMultipartFile3, false, CSVNotAcceptableException.class),
        Arguments.arguments(mockMultipartFile4, true, CSVNotAcceptableException.class),
        Arguments.arguments(mockMultipartFile4, false, CSVNotAcceptableException.class),
        Arguments.arguments(mockMultipartFile5, true, DataReaderHeaderNotAcceptable.class),
        Arguments.arguments(mockMultipartFile5, false, DataReaderHeaderNotAcceptable.class)
    );
  }

  private static Stream<Arguments> multipleParamWithLevel() {
    long clientExpected = 3;
    long divisionExpected = 2;
    long serviceOrOwnerExpected = 1;

    return Stream.of(
        Arguments.arguments(ModificationLevel.CLIENT.getKey(),
            ProfileUnitTestConstants.MOCK_LOAD_ORGANIZATION, clientExpected),
        Arguments.arguments(ModificationLevel.DIVISION.getKey(),
            ProfileUnitTestConstants.MOCK_LOAD_ORGANIZATION,
            divisionExpected),
        Arguments.arguments(ModificationLevel.SERVICE.getKey(),
            ProfileUnitTestConstants.MOCK_LOAD_ORGANIZATION,
            serviceOrOwnerExpected),
        Arguments.arguments(ModificationLevel.OWNER.getKey(),
            ProfileUnitTestConstants.MOCK_LOAD_ORGANIZATION,
            serviceOrOwnerExpected)
    );
  }
}
