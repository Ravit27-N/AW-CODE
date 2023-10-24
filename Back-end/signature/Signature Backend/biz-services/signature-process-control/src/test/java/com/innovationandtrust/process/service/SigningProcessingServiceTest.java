package com.innovationandtrust.process.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.CompleteSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.DocumentProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.GenerateOTPExecutionManager;
import com.innovationandtrust.process.chain.execution.SetupIndividualSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.SigningInfoExecutionManager;
import com.innovationandtrust.process.chain.execution.UploadModifiedDocumentExecutionHandler;
import com.innovationandtrust.process.chain.execution.VerifyDocumentExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.SigningProcessExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.utils.DateUtil;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.enums.SignatureFormat;
import com.innovationandtrust.share.enums.SignatureLevel;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectDetail;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SigningProcessingServiceTest {
  private SigningProcessingService signingProcessingService;
  @Mock private GenerateOTPExecutionManager otpExecutionManager;
  @Mock private SigningProcessExecutionManager signingProcessExecutionManager;
  @Mock private SigningInfoExecutionManager signingInfoExecutionManager;
  @Mock private CompleteSignProcessExecutionManager completeSignProcessExecutionManager;
  @Mock private UploadModifiedDocumentExecutionHandler uploadModifiedDocumentExecutionHandler;
  @Mock private DocumentProcessExecutionManager documentProcessExecutionManager;
  @Mock private SetupIndividualSignProcessExecutionManager individualSignProcessExecutionManager;
  @Mock private ImpersonateTokenService impersonateTokenService;
  @Mock private VerifyDocumentExecutionManager verifyDocumentExecutionManager;

  private ExecutionContext context;
  private CorporateInfo corporateInfo;
  private Project project;
  private TokenParam param;
  private String companyUuid;
  private String flowId;
  private String uuid;
  private String token;
  private String docId;
  private static final String PDF = ".pdf";

  @BeforeEach
  public void setup() {
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
                verifyDocumentExecutionManager));

    flowId = "022e2923-924b-4745-a2a8-250077141b83";
    uuid = "faf46aef-f9a5-4222-bfc4-a52fbc2991d1";
    companyUuid = "244eb546-2343-41d4-8c47-9c5d1ec947e0";
    docId = "244eb546-2343-41d4-8c47-9c5d1ec947e0";
    token =
        "DUCBtmFRpgN0Ya8McWP7K0C6jSqxC9UITjL-vS-pW_qCNb3vYqBI0hngHZGs_nxgygRrDNQZJn_hlNm772p73_aAjjAuEPXzfxivUJktqEtCZGVzieZyvMGgWJHR18XrIZkPNLyZlyV_4tQmiN1fJstgW5qxVeR6xVWhkpCXe6c";

    corporateInfo =
        CorporateInfo.builder().companyUuid(companyUuid).companyName("Certigna").build();

    context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);

    Template template =
        Template.builder()
            .name("Template")
            .signProcess(ScenarioStep.COUNTER_SIGN)
            .approvalProcess(ScenarioStep.COUNTER_SIGN)
            .level(SignatureLevel.LT)
            .format(SignatureFormat.CA_DES)
            .build();

    param =
        TokenParam.builder()
            .companyUuid(companyUuid)
            .flowId(flowId)
            .uuid(uuid)
            .token(token)
            .build();

    project = ProcessControlUtils.getProject(context);
    project.setFlowId(flowId);
    project.setCorporateInfo(corporateInfo);
    project.setTemplate(template);
    project.setDetail(
        new ProjectDetail(Collections.emptyList(), DateUtil.plushDays(new Date(), 2), 1L, 1L));
    project.setParticipants(Collections.singletonList(new Participant(uuid)));

    context.put(SignProcessConstant.PROJECT_KEY, project);
  }

  @Test
  @DisplayName("Get project sign info test")
  void get_sign_info_test() {
    // when
    this.signingInfoExecutionManager.execute(context);
    when(this.signingProcessingService.getSignInfo(flowId, uuid)).thenReturn(this.signInfo());
    var result = this.signingProcessingService.getSignInfo(flowId, uuid);
    verify(signingProcessingService, times(1)).getSignInfo(flowId, uuid);
    assertEquals(result.getProjectStatus(), ProjectStatus.IN_PROGRESS.name());
  }

  @Test
  @DisplayName("[Public] Get project sign info test")
  void get_sign_info_external_test() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessingService.getSignInfoExternal(companyUuid, token);
    var signinfo = this.signInfo();
    verify(signingProcessingService, times(1)).getSignInfoExternal(companyUuid, token);
    assertEquals(signinfo.getUuid(), param.getUuid());
    assertEquals(signinfo.getFlowId(), param.getFlowId());
  }

  private SignInfo signInfo() {
    // given
    return SignInfo.builder()
        .flowId(flowId)
        .uuid(uuid)
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

  @Test
  @DisplayName("Validate phone number test")
  void validate_phone_number() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              project.getDetail().setExpireDate(new Date());
              this.signingProcessingService.validatePhoneNumber(flowId, uuid, "");
              verify(signingProcessingService, times(1)).validatePhoneNumber(flowId, uuid, "");
            });

    String expectedMessage = "Invalid phone number!";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

    var phoneNumber = "+85510821111";
    Participant.ValidPhone validPhone = new Participant.ValidPhone();
    validPhone.setNumber(phoneNumber);

    this.otpExecutionManager.execute(context);
    var participant = mock(Participant.class);
    // when
    when(project.getParticipantByUuid(uuid)).thenReturn(Optional.ofNullable(participant));
    when(this.signingProcessingService.validatePhoneNumber(flowId, uuid, phoneNumber))
        .thenReturn(validPhone);

    verify(signingProcessingService, times(2)).validatePhoneNumber(flowId, uuid, phoneNumber);
  }

  @Test
  @DisplayName("[Public] Validate phone number test")
  void validate_phone_number_external() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    var phoneNumber = "+85510821111";
    this.otpExecutionManager.execute(context);
    this.signingProcessingService.validatePhoneNumberExternal(companyUuid, phoneNumber, token);
    verify(signingProcessingService, times(1))
        .validatePhoneNumberExternal(flowId, uuid, phoneNumber);
  }

  @Test
  @DisplayName("Validate signatory documents test")
  void validate_signatory_document_test() {
    this.verifyDocumentExecutionManager.execute(context);
    var docFront =
        new MockMultipartFile("frontDoc", "frontDoc.jpg", null, "backDoc.png".getBytes());
    var docBack = new MockMultipartFile("backDoc", "backDoc.jpg", null, "backDoc.png".getBytes());
    var document = new DocumentVerificationRequest();
    this.signingProcessingService.validateDocument(companyUuid, uuid, document);
    verify(signingProcessingService, times(1)).validateDocument(companyUuid, uuid, document);
  }

  @Test
  @DisplayName("[Public] Validate signatory documents test")
  void validate_signatory_document_external_test() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    var phoneNumber = "+85510821111";
    this.otpExecutionManager.execute(context);
    var document = new DocumentVerificationRequest();
    this.signingProcessingService.validateDocumentExternal(companyUuid, phoneNumber, document);
    verify(signingProcessingService, times(1))
        .validateDocumentExternal(companyUuid, phoneNumber, document);
  }

  @Test
  @DisplayName("Generate OPT test")
  void generate_opt_test() {
    this.otpExecutionManager.execute(context);
    this.signingProcessingService.generateOtp(flowId, uuid);
    verify(signingProcessingService, times(1)).generateOtp(flowId, uuid);
  }

  @Test
  @DisplayName("[Public] Generate OPT test")
  void generate_opt_external_test() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.otpExecutionManager.execute(context);
    this.signingProcessingService.generateOtpExternal(companyUuid, token);
    verify(signingProcessingService, times(1)).generateOtpExternal(companyUuid, token);
  }

  @Test
  @DisplayName("Generate and response OPT test")
  void generate_and_response_opt_test() {
    this.otpExecutionManager.execute(context);
    this.signingProcessingService.generateAndResponseOtp(flowId, uuid);
    verify(signingProcessingService, times(1)).generateAndResponseOtp(flowId, uuid);
  }

  @Test
  @DisplayName("Validate OPT test")
  void validate_opt_test() {
    this.otpExecutionManager.execute(context);
    this.signingProcessingService.validateOtp(flowId, uuid, "6027");
    verify(signingProcessingService, times(1)).validateOtp(flowId, uuid, "6027");
  }

  @Test
  @DisplayName("[Public] Validate OPT test")
  void validate_opt_external_test() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessingService.validateOtpExternal(companyUuid, token, "6027");
    verify(signingProcessingService, times(1)).validateOtpExternal(companyUuid, token, "6027");
  }

  @Test
  @DisplayName("Sign document test")
  void sign_document_test() {
    this.signingProcessExecutionManager.execute(context);
    this.signingProcessingService.signDocuments(flowId, uuid);
    verify(signingProcessingService, times(1)).signDocuments(flowId, uuid);
  }

  @Test
  @DisplayName("Sign document test")
  void sign_document_external_test() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.signingProcessExecutionManager.execute(context);
    this.signingProcessingService.signDocumentsExternal(companyUuid, token);
    verify(signingProcessingService, times(1)).signDocumentsExternal(companyUuid, token);
  }

  @Test
  @DisplayName("Download signed document test")
  void download_document_signed_test() {
    var fileName = docId + PDF;
    final FileResponse fileResponse =
        new FileResponse(fileName.getBytes(), 12323L, "application/pdf", fileName);
    this.completeSignProcessExecutionManager.execute(context);
    this.signingProcessingService.downloadSignedDocument(flowId, uuid);
    verify(signingProcessingService, times(1)).downloadSignedDocument(flowId, uuid);

    when(this.signingProcessingService.downloadSignedDocument(flowId, uuid)).thenReturn(fileResponse);
    assertNotNull(fileResponse);
    assertEquals(fileResponse.getFilename(), fileName);
  }

  @Test
  @DisplayName("[Public] Download signed document test")
  void download_document_signed_external_test() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.completeSignProcessExecutionManager.execute(context);
    this.signingProcessingService.downloadSignedDocumentExternal(companyUuid, token, docId);
    verify(signingProcessingService, times(1))
        .downloadSignedDocumentExternal(companyUuid, token, docId);
  }

  @Test
  @DisplayName("View document test")
  void view_document_test() {
    String base64 = "base64";
    this.documentProcessExecutionManager.execute(context);
    this.signingProcessingService.viewDocument(flowId, uuid);
    verify(signingProcessingService, times(1)).viewDocument(flowId, uuid);

    when(this.signingProcessingService.viewDocument(flowId, uuid)).thenReturn(base64);
    assertNotNull(base64);
  }

  @Test
  @DisplayName("[Public] View document test")
  void view_document_external_test() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.documentProcessExecutionManager.execute(context);
    this.signingProcessingService.viewDocumentExternal(companyUuid, token, docId);
    verify(signingProcessingService, times(1)).viewDocumentExternal(companyUuid, token, docId);
  }

  @Test
  @DisplayName("Upload modified document test")
  void upload_modified_document_test() {
    var file = getFile();
    this.uploadModifiedDocumentExecutionHandler.execute(context);
    this.signingProcessingService.uploadModifiedDocument(file, flowId, docId);
    verify(signingProcessingService, times(1)).uploadModifiedDocument(file, flowId, docId);
  }

  @Test
  @DisplayName("[Public] Upload modified document test")
  void upload_modified_document_external_test() {
    var file = getFile();
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.uploadModifiedDocumentExecutionHandler.execute(context);
    this.signingProcessingService.uploadModifiedDocumentExternal(companyUuid, file, docId, token);
    verify(signingProcessingService, times(1))
        .uploadModifiedDocumentExternal(companyUuid, file, docId, token);
  }

  private MockMultipartFile getFile() {
    return new MockMultipartFile(
        docId, docId + PDF, MediaType.APPLICATION_PDF_VALUE, "Signature pdf document".getBytes());
  }

  @Test
  @DisplayName("Set up individual sign process test")
  void individual_set_up_sign_test() {
    this.individualSignProcessExecutionManager.execute(context);
    this.signingProcessingService.setupIndividualSignProcess(flowId, uuid);
    verify(signingProcessingService, times(1)).setupIndividualSignProcess(flowId, uuid);
  }

  @Test
  @DisplayName("[Public] Set up individual sign process test")
  void individual_set_up_sign_external_test() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.individualSignProcessExecutionManager.execute(context);
    this.signingProcessingService.setupIndividualSignProcessExternal(companyUuid, token);
    verify(signingProcessingService, times(1))
        .setupIndividualSignProcessExternal(companyUuid, token);
  }

  @Test
  @DisplayName("Set up individual sign process test")
  void individual_set_up_sign_otp_test() {
    this.individualSignProcessExecutionManager.execute(context);
    this.signingProcessingService.setupIndividualProcessOTPValue(flowId, uuid);
    verify(signingProcessingService, times(1)).setupIndividualProcessOTPValue(flowId, uuid);
  }
}
