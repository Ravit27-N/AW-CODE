package com.innovationandtrust.process.chain.handler;

import static com.innovationandtrust.process.constant.UnitTestProvider.getDate;
import static com.innovationandtrust.process.constant.UnitTestProvider.randomOtp;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.OtpProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.chain.handler.helper.OtpProcessingHelper;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.ValidateOtpRequest;
import com.innovationandtrust.utils.aping.signing.Actor;
import com.innovationandtrust.utils.aping.signing.GeneratedOTP;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@Slf4j
@ExtendWith(SpringExtension.class)
class OtpProcessingHandlerTest {
  private OtpProcessingHandler otpProcessingHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private NotificationFeignClient notificationFeignClient;
  @Mock private SignatureIdentityVerificationFeignClient verificationFeignClient;
  @Mock private TemplateEngine templateEngine;
  @Mock private ProjectFeignClient projectFeignClient;
  private Project project;
  private GeneratedOTP generatedOTP;
  private ValidateOtpRequest validateOtpRequest;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    otpProcessingHandler =
        spy(
            new OtpProcessingHandler(
                mock(OtpProcessingHelper.class),
                apiNgFeignClient,
                notificationFeignClient,
                mock(MultipleSigningOtpProcessHandler.class)));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
    generatedOTP = new GeneratedOTP(randomOtp(), getDate(0), getDate(1), new Actor());
    validateOtpRequest =
        new ValidateOtpRequest(generatedOTP.getOtp(), "Vothana", List.of(""), false);
  }

  private void after() {
    // update json file to original project for other test case
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @Order(1)
  @DisplayName("[Generate OPT]")
  void generateOtp() {
    // when
    when(this.apiNgFeignClient.generateOTP(anyLong(), any())).thenReturn(generatedOTP);

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);

    // For approval
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_APPROVAL);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(2)).execute(context);
  }

  @Test
  @Order(2)
  @DisplayName("[Generate OPT] Advance project error")
  void generateOtpAdvance() {
    // given
    project.setSignatureLevel(SignatureSettingLevel.ADVANCE.name());
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setDocumentVerified(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    // when
    when(this.apiNgFeignClient.generateOTP(anyLong(), any())).thenReturn(generatedOTP);

    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.otpProcessingHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @Order(3)
  @DisplayName("[Validate Phone Number] Success")
  void validatePhoneNumber() {
    // given
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE_PHONE_NUMBER);
    context.put(SignProcessConstant.PHONE_NUMBER, "6027");

    // when
    when(this.apiNgFeignClient.generateOTP(anyLong(), any())).thenReturn(generatedOTP);

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler).execute(context);
  }

  @Test
  @Order(3)
  @DisplayName("[Validate Phone Number] Fail")
  void validatePhoneNumberFail() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.getValidPhone().setTotalAttempts(4);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE_PHONE_NUMBER);
    context.put(SignProcessConstant.PHONE_NUMBER, "60271");
    var user = UnitTestProvider.getUser();

    // when
    when(this.apiNgFeignClient.generateOTP(anyLong(), any())).thenReturn(generatedOTP);

    // project no assigned to.
    // To be log warning.
    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);

    // when
    when(this.profileFeignClient.findByIdOptional(anyLong())).thenReturn(Optional.of(user));

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(2)).execute(context);

    // project has assigned to
    project.setAssignedTo(1L);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(3)).execute(context);

    // when
    when(this.profileFeignClient.findByIdOptional(anyLong())).thenReturn(Optional.empty());

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(4)).execute(context);
  }

  @Test
  @Order(4)
  @DisplayName("[Validate OPT] Singer")
  void validateOtp() {
    // given
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    // when
    when(this.apiNgFeignClient.validateOTP(anyLong(), any())).thenReturn(validateOtpRequest);

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);
  }

  @Test
  @Order(5)
  @DisplayName("[Validate OPT] Singer (Fail)")
  void validateOtpFail() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.getOtp().setErrorValidation(2);
            });

    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    doThrow(new FeignClientRequestException()).when(apiNgFeignClient).validateOTP(anyLong(), any());

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);
  }

  @Test
  @Order(6)
  @DisplayName("[Validate OPT] Approval")
  void validateOtpApproval() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_APPROVAL);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    doThrow(new FeignClientRequestException()).when(apiNgFeignClient).validateOTP(anyLong(), any());

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);
  }

  @Test
  @Order(7)
  @DisplayName("[Validate OPT] Maxed attempts")
  void validateOtpMaxAttempts() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.getOtp().setErrorValidation(3);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    var exception =
        assertThrows(BadRequestException.class, () -> this.otpProcessingHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @Order(8)
  @DisplayName("Invalid participant UUID ")
  void testValidateOtpInvalidParticipant() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setUuid(null);
            });

    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.otpProcessingHandler.execute(context));
    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @Order(9)
  @DisplayName("[MULTIPLE PROJECTS] Validate phone number")
  void testMultipleProjectsValidatePhone() {
    // given
    UnitTestProvider.setMultipleProjects(context);
    context.put(SignProcessConstant.PHONE_NUMBER, "6027");
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE_PHONE_NUMBER);

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);
  }

  @Test
  @Order(10)
  @DisplayName("[MULTIPLE PROJECTS] Generate OTP number")
  void testMultipleProjectsGenerateOtp() {
    // given
    UnitTestProvider.setMultipleProjects(context);
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.GENERATE);

    // when
    when(this.apiNgFeignClient.generateOTP(any())).thenReturn(generatedOTP);

    this.otpProcessingHandler.execute(context);
  }

  @Test
  @Order(11)
  @DisplayName("[MULTIPLE PROJECTS] Validate OTP number")
  void testMultipleProjectsValidateOtp() {
    // given
    UnitTestProvider.setMultipleProjects(context);
    context.put(SignProcessConstant.OTP_VALUE, generatedOTP.getOtp());
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    // when
    when(this.apiNgFeignClient.validateOTP(any())).thenReturn(validateOtpRequest);

    this.otpProcessingHandler.execute(context);
  }

  @Test
  @Order(12)
  @DisplayName("[MULTIPLE PROJECTS] Validate OTP Fail")
  void testMultipleProjectsValidateOtpFail() {
    // given
    UnitTestProvider.setMultipleProjects(context);
    context.put(SignProcessConstant.OTP_VALUE, generatedOTP.getOtp());
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    // when
    doThrow(new FeignClientRequestException()).when(apiNgFeignClient).validateOTP(any());

    this.otpProcessingHandler.execute(context);
    verify(this.otpProcessingHandler, times(1)).execute(context);
  }

  @Test
  @Order(13)
  @DisplayName("[MULTIPLE PROJECTS] Validate OTP Max failed")
  void testMultipleProjectsValidateOtpMax() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(participant -> participant.getOtp().setErrorValidation(2));

    UnitTestProvider.setMultipleProjects(context);
    context.put(SignProcessConstant.OTP_VALUE, generatedOTP.getOtp());
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.VALIDATE);

    // when
    doThrow(new FeignClientRequestException()).when(apiNgFeignClient).validateOTP(any());

    this.otpProcessingHandler.execute(context);
  }

  @Test
  @Order(14)
  @DisplayName("[MULTIPLE PROJECTS] Generate OTP ")
  void testGenerateOptEndUserPhone() {
    // given
    final var secondProjectFlowId = "b17c7e52-c605-4f0d-a809-1f3449008343";

    final var signingProcessDto =
        SigningProcessDto.builder()
            .uuid(UnitTestConstant.UUID)
            .flowId(UnitTestConstant.FLOW_ID)
            .build();

    final var signingProcessDto1 =
        SigningProcessDto.builder().uuid(UnitTestConstant.UUID).flowId(secondProjectFlowId).build();

    var listProjects = new ArrayList<>(List.of(project));
    var secondProject = UnitTestProvider.project(context);
    secondProject.setFlowId(secondProjectFlowId);
    secondProject
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            person -> {
              person.setPhone(UnitTestConstant.PHONE_NUMBER);
              person.setOtp(null);
            });
    secondProject.getTemplate().setApprovalProcess(ScenarioStep.APPROVAL);

    listProjects.add(secondProject);

    context.put(
        SignProcessConstant.MULTI_SIGNING_PROJECTS, List.of(signingProcessDto, signingProcessDto1));
    context.put(SignProcessConstant.OTP_VALUE, generatedOTP.getOtp());
    context.put(SignProcessConstant.OTP_PROCESS_ACTION, OtpProcessAction.GENERATE);
    context.put(SignProcessConstant.PROJECTS, listProjects);

    // when
    when(this.apiNgFeignClient.generateOTP(any())).thenReturn(generatedOTP);
    when(this.profileFeignClient.getOwnInfo()).thenReturn(UnitTestProvider.getUser());

    this.otpProcessingHandler.execute(context);
  }
}
