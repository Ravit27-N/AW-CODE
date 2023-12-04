package com.innovationandtrust.process.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.CompleteSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.DocumentProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.GenerateOTPExecutionManager;
import com.innovationandtrust.process.chain.execution.SetupIndividualSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.SignatureFileExecutionManager;
import com.innovationandtrust.process.chain.execution.SigningInfoExecutionManager;
import com.innovationandtrust.process.chain.execution.UploadModifiedDocumentExecutionHandler;
import com.innovationandtrust.process.chain.execution.VerifyDocumentExecutionManager;
import com.innovationandtrust.process.chain.execution.eid.EIDRequestToSignExecutionManager;
import com.innovationandtrust.process.chain.execution.eid.EIDSigningProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.eid.EIDVideoAuthorizationProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.eid.EIDVideoVerificationProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.SigningProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.GetUserInfoHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.MultipleSigningOtpProcessHandler;
import com.innovationandtrust.process.chain.handler.OtpProcessingHandler;
import com.innovationandtrust.process.chain.handler.RequestSigningHandler;
import com.innovationandtrust.process.chain.handler.sign.MultiSigningProcessHandler;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.ProjectDto;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.chain.handler.helper.OtpProcessingHelper;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.ResponseData;
import com.innovationandtrust.utils.aping.model.ValidateOtpRequest;
import com.innovationandtrust.utils.aping.signing.GeneratedOTP;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.eid.model.RequestSignViaSmsResponse;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import com.innovationandtrust.utils.exception.exceptions.FileNotSupportException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class SigningProcessingServiceTest {
  private SigningProcessingService signingProcessingService;
  private GenerateOTPExecutionManager otpExecutionManager;
  private SetupIndividualSignProcessExecutionManager individualSignProcessExecutionManager;
  @Mock private SigningProcessExecutionManager signingProcessExecutionManager;
  @Mock private SigningInfoExecutionManager signingInfoExecutionManager;
  @Mock private CompleteSignProcessExecutionManager completeSignProcessExecutionManager;
  @Mock private UploadModifiedDocumentExecutionHandler uploadModifiedDocumentExecutionHandler;
  @Mock private DocumentProcessExecutionManager documentProcessExecutionManager;
  @Mock private ImpersonateTokenService impersonateTokenService;
  @Mock private VerifyDocumentExecutionManager verifyDocumentExecutionManager;
  @Mock private SignatureFileExecutionManager signatureFileExecutionManager;

  @Mock
  private EIDVideoAuthorizationProcessExecutionManager videoAuthorizationProcessExecutionManager;

  @Mock
  private EIDVideoVerificationProcessExecutionManager videoVerificationProcessExecutionManager;

  @Mock private EIDRequestToSignExecutionManager eidRequestToSignExecutionManager;
  @Mock private EIDSigningProcessExecutionManager eIDSignDocumentExecuteManager;
  @Mock private MultiSigningProcessHandler multiSigningProcessHandler;

  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private NotificationFeignClient notificationFeignClient;
  @Mock private SignatureIdentityVerificationFeignClient verificationFeignClient;

  private ExecutionContext context;
  private Project project;
  private ProjectDto projectDto;
  private final TokenParam param = UnitTestProvider.getParam();
  private final String companyUuid = UnitTestConstant.COMPANY_UUID;
  private final String flowId = UnitTestConstant.FLOW_ID;
  private final String uuid = UnitTestConstant.UUID;
  private final String token = UnitTestConstant.TOKEN;
  private static final String PDF = ".pdf";

  @BeforeAll
  public static void init() {
    try (var ignored = mockStatic(CompanySettingUtils.class)) {
      log.info("No Implement yet!");
    }
  }

  @BeforeEach
  public void setup() {

    ModelMapper modelMapper = new ModelMapper();
    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
    projectDto = modelMapper.map(project, ProjectDto.class);
    projectDto.setCreatedByUser(UnitTestProvider.getUser());

    JsonFileProcessHandler jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    var otpProcessingHandler =
        new OtpProcessingHandler(
            mock(OtpProcessingHelper.class),
            apiNgFeignClient,
            notificationFeignClient,
            mock(MultipleSigningOtpProcessHandler.class));

    otpExecutionManager =
        spy(
            new GenerateOTPExecutionManager(
                jsonFileProcessHandler,
                new GetUserInfoHandler(projectFeignClient),
                otpProcessingHandler));
    otpExecutionManager.afterPropertiesSet();

    individualSignProcessExecutionManager =
        spy(
            new SetupIndividualSignProcessExecutionManager(
                jsonFileProcessHandler,
                new RequestSigningHandler(
                    apiNgFeignClient, UnitTestProvider.fileProvider(), profileFeignClient),
                otpProcessingHandler));
    individualSignProcessExecutionManager.afterPropertiesSet();

    signingProcessingService =
        spy(
            new SigningProcessingService(
                otpExecutionManager,
                signingProcessExecutionManager,
                signingInfoExecutionManager,
                completeSignProcessExecutionManager,
                uploadModifiedDocumentExecutionHandler,
                documentProcessExecutionManager,
                individualSignProcessExecutionManager,
                impersonateTokenService,
                verifyDocumentExecutionManager,
                signatureFileExecutionManager,
                videoAuthorizationProcessExecutionManager,
                videoVerificationProcessExecutionManager,
                eidRequestToSignExecutionManager,
                multiSigningProcessHandler,
                eIDSignDocumentExecuteManager,
                UnitTestProvider.fileProvider()));
  }

  @Test
  @DisplayName("Get project sign info test")
  void testGetSignInfo() {
    // when
    this.signingInfoExecutionManager.execute(context);
    when(this.signingProcessingService.getSignInfo(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID))
        .thenReturn(this.signInfo());
    var result =
        this.signingProcessingService.getSignInfo(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .getSignInfo(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    assertEquals(
        result.getProjectStatus(),
        ProjectStatus.IN_PROGRESS.name(),
        UnitTestConstant.ASSERT_EQUALS);
  }

  @Test
  @DisplayName("[Public] Get project sign info test")
  void testGetSignInfoExternal() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessingService.getSignInfoExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    var signinfo = this.signInfo();
    verify(signingProcessingService, times(1))
        .getSignInfoExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    assertEquals(signinfo.getUuid(), param.getUuid(), UnitTestConstant.ASSERT_EQUALS);
    assertEquals(signinfo.getFlowId(), param.getFlowId(), UnitTestConstant.ASSERT_EQUALS);
  }

  private SignInfo signInfo() {
    // given
    return SignInfo.builder()
        .flowId(UnitTestConstant.FLOW_ID)
        .uuid(UnitTestConstant.UUID)
        .projectName("Project Test")
        .projectStatus(ProjectStatus.IN_PROGRESS.name())
        .signatureLevel(SignatureSettingLevel.SIMPLE.name())
        .actor(
            SignInfo.Actor.builder()
                .firstName("Vothana")
                .lastName("CHY")
                .role(RoleConstant.ROLE_SIGNATORY)
                .processed(true)
                .build())
        .phoneNumber(
            SignInfo.PhoneNumber.builder()
                .removedNumber("+8551082")
                .missingLength(4)
                .isValidated(true)
                .build())
        .otpInfo(new OtpInfo(false, true, 2))
        .build();
  }

  private void optExecute() {
    when(this.projectFeignClient.findById(anyLong())).thenReturn(projectDto);
    when(this.apiNgFeignClient.generateOTP(anyLong(), any())).thenReturn(new GeneratedOTP());
    when(this.apiNgFeignClient.validateOTP(anyLong(), any())).thenReturn(new ValidateOtpRequest());
  }

  @Test
  @DisplayName("Validate phone number test")
  void testValidatePhoneNumber() {
    project.getDetail().setExpireDate(new Date());
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> this.signingProcessingService.validatePhoneNumber(flowId, uuid, ""),
            UnitTestConstant.ASSERT_EXCEPTION);

    String expectedMessage = "Invalid phone number!";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage), "The expected message");

    var phoneNumber = "+85510826027";
    Participant.ValidPhone validPhone = new Participant.ValidPhone();
    validPhone.setNumber(UnitTestConstant.PHONE_NUMBER);

    this.optExecute();
    this.otpExecutionManager.execute(context);
    var result = this.signingProcessingService.validatePhoneNumber(flowId, uuid, phoneNumber);

    verify(signingProcessingService, times(1)).validatePhoneNumber(flowId, uuid, phoneNumber);

    assertTrue(result.isValid(), UnitTestConstant.ASSERT_TRUE);
  }

  @Test
  @DisplayName("[Public] Validate phone number test")
  void testValidatePhoneNumberExternal() {
    // when
    this.optExecute();
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

    var phoneNumber = "+85510821111";
    this.otpExecutionManager.execute(context);
    this.signingProcessingService.validatePhoneNumberExternal(companyUuid, phoneNumber, token);
    verify(signingProcessingService, times(1))
        .validatePhoneNumberExternal(companyUuid, phoneNumber, token);
  }

  @Test
  @DisplayName("Validate signatory documents test")
  void testValidateSignatoryDocument() {
    this.verifyDocumentExecutionManager.execute(context);
    var document = new DocumentVerificationRequest();
    this.signingProcessingService.validateDocument(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.UUID, document);
    verify(signingProcessingService, times(1))
        .validateDocument(UnitTestConstant.COMPANY_UUID, UnitTestConstant.UUID, document);
  }

  @Test
  @DisplayName("[Public] Validate signatory documents test")
  void testValidateSignatoryDocumentExternal() {
    // when
    this.optExecute();
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

    var phoneNumber = "+85510821111";
    this.otpExecutionManager.execute(context);
    var document = new DocumentVerificationRequest();
    this.signingProcessingService.validateDocumentExternal(
        UnitTestConstant.COMPANY_UUID, phoneNumber, document);
    verify(signingProcessingService, times(1))
        .validateDocumentExternal(UnitTestConstant.COMPANY_UUID, phoneNumber, document);
  }

  @Test
  @DisplayName("Generate OPT test")
  void testGenerateOpt() {
    // when
    this.optExecute();

    this.otpExecutionManager.execute(context);
    this.signingProcessingService.generateOtp(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .generateOtp(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[Public] Generate OPT test")
  void testGenerateOptExternal() {
    // when
    this.optExecute();
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

    this.otpExecutionManager.execute(context);
    this.signingProcessingService.generateOtpExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    verify(signingProcessingService, times(1))
        .generateOtpExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("Generate and response OPT test")
  void testGenerateAndResponseOpt() {
    // when
    this.optExecute();

    this.otpExecutionManager.execute(context);
    this.signingProcessingService.generateAndResponseOtp(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .generateAndResponseOtp(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("Validate OPT test")
  void testValidateOpt() {
    // when
    this.optExecute();

    this.otpExecutionManager.execute(context);
    this.signingProcessingService.validateOtp(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID, "6027");
    verify(signingProcessingService, times(1))
        .validateOtp(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID, "6027");
  }

  @Test
  @DisplayName("[Public] Validate OPT test")
  void testValidateOptExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    when(this.projectFeignClient.findById(anyLong())).thenReturn(projectDto);
    when(this.apiNgFeignClient.validateOTP(any(), any())).thenReturn(new ValidateOtpRequest());
    this.signingProcessingService.validateOtpExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, "6027");
    verify(signingProcessingService, times(1))
        .validateOtpExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, "6027");
  }

  @Test
  @DisplayName("Sign document test")
  void testSignDocument() {
    this.signingProcessExecutionManager.execute(context);
    this.signingProcessingService.signDocuments(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .signDocuments(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("Sign document test")
  void testSignDocumentExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessExecutionManager.execute(context);
    this.signingProcessingService.signDocumentsExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    verify(signingProcessingService, times(1))
        .signDocumentsExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Request signing a document")
  void testRequestToSignDocumentsExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.eidRequestToSignExecutionManager.execute(context);
    when(this.signingProcessingService.requestToSignDocumentsExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN))
        .thenReturn(new RequestSignViaSmsResponse());
    verify(signingProcessingService, times(1))
        .requestToSignDocumentsExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("Request signing a document")
  void testRequestToSignDocuments() {
    this.eidRequestToSignExecutionManager.execute(context);
    this.signingProcessingService.requestToSignDocuments(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .requestToSignDocuments(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("Sign a document")
  void testSignDocumentEID() {
    this.eIDSignDocumentExecuteManager.execute(context);
    this.signingProcessingService.signDocument(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID, UnitTestConstant.OTP_CODE);
    verify(signingProcessingService, times(1))
        .signDocument(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID, UnitTestConstant.OTP_CODE);
  }

  @Test
  @DisplayName("[Public] Sign a document")
  void testSignDocumentEIDExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.eIDSignDocumentExecuteManager.execute(context);
    when(this.signingProcessingService.signDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.OTP_CODE))
        .thenReturn(Boolean.TRUE);
    verify(signingProcessingService, times(1))
        .signDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.OTP_CODE);
  }

  @Test
  @DisplayName("Download signed document test")
  void testDownloadDocumentSigned() {
    var fileName = UnitTestConstant.DOC_ID + PDF;
    final FileResponse fileResponse =
        new FileResponse(fileName.getBytes(), 12323L, "application/pdf", fileName);
    this.completeSignProcessExecutionManager.execute(context);
    this.signingProcessingService.downloadSignedDocument(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .downloadSignedDocument(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);

    when(this.signingProcessingService.downloadSignedDocument(flowId, uuid))
        .thenReturn(fileResponse);
    assertNotNull(fileResponse, UnitTestConstant.ASSERT_NOT_NULL);
    assertEquals(fileResponse.getFilename(), fileName, UnitTestConstant.ASSERT_EQUALS);
  }

  @Test
  @DisplayName("[Public] Download signed document test")
  void testDownloadDocumentSignedExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.completeSignProcessExecutionManager.execute(context);
    this.signingProcessingService.downloadSignedDocumentExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.DOC_ID);
    verify(signingProcessingService, times(1))
        .downloadSignedDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.DOC_ID);
  }

  @Test
  @DisplayName("View document test")
  void testViewDocument() {
    String base64 = "base64";
    this.documentProcessExecutionManager.execute(context);
    this.signingProcessingService.viewDocument(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .viewDocument(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);

    when(this.signingProcessingService.viewDocument(
            UnitTestConstant.FLOW_ID, UnitTestConstant.UUID))
        .thenReturn(base64);
    assertNotNull(base64, UnitTestConstant.ASSERT_NOT_NULL);
  }

  @Test
  @DisplayName("[Public] View document test")
  void testViewDocumentExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.documentProcessExecutionManager.execute(context);
    this.signingProcessingService.viewDocumentExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.DOC_ID);
    verify(signingProcessingService, times(1))
        .viewDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.DOC_ID);
  }

  @Test
  @DisplayName("Upload modified document test")
  void testUploadModifiedDocument() {
    var file = getFile();
    this.uploadModifiedDocumentExecutionHandler.execute(context);
    this.signingProcessingService.uploadModifiedDocument(
        file, UnitTestConstant.FLOW_ID, UnitTestConstant.DOC_ID);
    verify(signingProcessingService, times(1))
        .uploadModifiedDocument(file, UnitTestConstant.FLOW_ID, UnitTestConstant.DOC_ID);
  }

  @Test
  @DisplayName("[Public] Upload modified document test")
  void testUploadModifiedDocumentExternal() {
    var file = getFile();
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.uploadModifiedDocumentExecutionHandler.execute(context);
    this.signingProcessingService.uploadModifiedDocumentExternal(
        UnitTestConstant.COMPANY_UUID, file, UnitTestConstant.DOC_ID, UnitTestConstant.TOKEN);
    verify(signingProcessingService, times(1))
        .uploadModifiedDocumentExternal(
            UnitTestConstant.COMPANY_UUID, file, UnitTestConstant.DOC_ID, UnitTestConstant.TOKEN);
  }

  private MockMultipartFile getFile() {
    return new MockMultipartFile(
        UnitTestConstant.DOC_ID,
        UnitTestConstant.DOC_ID + PDF,
        MediaType.APPLICATION_PDF_VALUE,
        "Signature pdf document".getBytes());
  }

  private void requestSign() {
    // given
    var responseData = new ResponseData("/api/v1/session/4085");

    when(this.profileFeignClient.findUserById(anyLong())).thenReturn(UnitTestProvider.getUser());
    when(this.apiNgFeignClient.createSession(any())).thenReturn(responseData);
    when(this.apiNgFeignClient.createActor(anyLong(), any())).thenReturn(responseData);
    when(this.apiNgFeignClient.uploadFile(anyString(), anyString(), any()))
        .thenReturn(responseData);
    when(this.apiNgFeignClient.addDocument(anyLong(), any())).thenReturn(responseData);
    when(this.apiNgFeignClient.createScenario(anyLong(), any())).thenReturn(responseData);
    when(this.projectFeignClient.findById(anyLong())).thenReturn(projectDto);
    when(this.apiNgFeignClient.generateOTP(anyLong(), any())).thenReturn(new GeneratedOTP());
  }

  @Test
  @DisplayName("Set up individual sign process test")
  void testIndividualSetUpSign() {
    // when
    this.requestSign();

    this.individualSignProcessExecutionManager.execute(context);
    this.signingProcessingService.setupIndividualSignProcess(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .setupIndividualSignProcess(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[Public] Set up individual sign process test")
  void testIndividualSetUpSignExternal() {
    // when
    this.requestSign();
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

    this.individualSignProcessExecutionManager.execute(context);
    this.signingProcessingService.setupIndividualSignProcessExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    verify(signingProcessingService, times(1))
        .setupIndividualSignProcessExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("Set up individual sign process test")
  void testIndividualSetUpSignOtpTest() {
    // when
    this.requestSign();

    this.individualSignProcessExecutionManager.execute(context);
    this.signingProcessingService.setupIndividualProcessOTPValue(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .setupIndividualProcessOTPValue(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[Public] Request videoID authorization")
  void testRequestVideoIdAuthorizationExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessingService.requestVideoIDAuthenticationExternal(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
    verify(signingProcessingService, times(1))
        .requestVideoIDAuthenticationExternal(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("Request videoID authorization")
  void testRequestVideoIdAuthorization() {
    this.videoAuthorizationProcessExecutionManager.execute(context);
    this.signingProcessingService.requestVideoIDAuthentication(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    verify(signingProcessingService, times(1))
        .requestVideoIDAuthentication(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Request video verification")
  void testRequestVideoVerificationExternal() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessingService.requestVerificationVideoIDExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.VIDEO_ID);
    verify(signingProcessingService, times(1))
        .requestVerificationVideoIDExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.VIDEO_ID);
  }

  @Test
  @DisplayName("Request video verification")
  void testRequestVideoVerification() {
    this.videoVerificationProcessExecutionManager.execute(context);
    this.signingProcessingService.requestVerificationVideoID(
        UnitTestConstant.FLOW_ID, UnitTestConstant.UUID, UnitTestConstant.VIDEO_ID);
    verify(signingProcessingService, times(1))
        .requestVerificationVideoID(
            UnitTestConstant.FLOW_ID, UnitTestConstant.UUID, UnitTestConstant.VIDEO_ID);
  }

  @Test
  @DisplayName("Upload signature file")
  void testUploadSignatureFile() {
    try (MockedStatic<FileUtils> ignored = mockStatic(FileUtils.class)) {
      // given
      var file =
          new MockMultipartFile("signature", "signature.png", null, "signature.png".getBytes());

      this.signingProcessingService.uploadSignatureFile(flowId, uuid, file, SignatureMode.IMPORT);
      verify(signingProcessingService, times(1))
          .uploadSignatureFile(flowId, uuid, file, SignatureMode.IMPORT);
    }
  }

  @Test
  @DisplayName("Upload signature file external")
  void testUploadSignatureFileExternal() {
    try (MockedStatic<FileUtils> ignored = mockStatic(FileUtils.class)) {
      // given
      var file =
          new MockMultipartFile("signature", "signature.png", null, "signature.png".getBytes());

      // when
      when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

      this.signingProcessingService.uploadSignatureFileExternal(
          companyUuid, token, SignatureMode.IMPORT, file);
      verify(signingProcessingService, times(1))
          .uploadSignatureFileExternal(companyUuid, token, SignatureMode.IMPORT, file);
    }
  }

  @Test
  @DisplayName("Upload signature file fails")
  void testUploadSignatureFileFails() {
    // given
    var file =
        new MockMultipartFile("signature", "signature.png", null, "signature.png".getBytes());
    var exception =
        assertThrows(
            InvalidRequestException.class,
            () ->
                this.signingProcessingService.uploadSignatureFile(
                    flowId, uuid, file, SignatureMode.WRITE),
            UnitTestConstant.ASSERT_EXCEPTION);
    log.error("Error : {}", exception.getMessage());

    exception =
        assertThrows(
            InvalidRequestException.class,
            () ->
                this.signingProcessingService.uploadSignatureFile(
                    flowId, uuid, null, SignatureMode.IMPORT),
            UnitTestConstant.ASSERT_EXCEPTION);
    log.error("Error : {}", exception.getMessage());

    log.error(
        "Error : {}",
        assertThrows(
                FileNotSupportException.class,
                () ->
                    this.signingProcessingService.uploadSignatureFile(
                        flowId, uuid, file, SignatureMode.IMPORT),
                UnitTestConstant.ASSERT_EXCEPTION)
            .getMessage());
  }

  @Test
  @DisplayName("View signature file")
  void testViewSignatureFile() {
    this.signingProcessingService.viewSignatureFile(flowId, uuid);
    verify(signingProcessingService, times(1)).viewSignatureFile(flowId, uuid);

    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

    this.signingProcessingService.viewSignatureFileExternal(companyUuid, token);
    verify(signingProcessingService, times(1)).viewSignatureFileExternal(companyUuid, token);
  }

  @Test
  @DisplayName("Remove signature file")
  void testRemoveSignatureFile() {
    this.signingProcessingService.removeSignatureFile(flowId, uuid);
    verify(signingProcessingService, times(1)).removeSignatureFile(flowId, uuid);

    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);

    this.signingProcessingService.removeSignatureFileExternal(companyUuid, token);
    verify(signingProcessingService, times(1)).removeSignatureFileExternal(companyUuid, token);
  }
}
