package com.innovationandtrust.process.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.model.OtpInfo;
import com.innovationandtrust.process.model.SignInfo;
import com.innovationandtrust.process.service.SigningProcessingService;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.Participant.ValidPhone;
import com.innovationandtrust.utils.eid.model.RequestSignViaSmsResponse;
import com.innovationandtrust.utils.eid.model.VideoIDAuthorizationDto;
import com.innovationandtrust.utils.eid.model.VideoIDVerificationDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class ExternalSigningProcessControllerTests {
  @InjectMocks private ExternalSigningProcessController controller;
  @Mock private SigningProcessingService signingProcessingService;
  @Mock private FileResponse fileResponse;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    fileResponse = new FileResponse();
    fileResponse.setFilename("abc.pdf");
    fileResponse.setSize(1200);
    fileResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    fileResponse.setResource("123".getBytes());
  }

  @Test
  @DisplayName("[Public] Sign info")
  void testFindDocumentsExternalController() throws Exception {
    when(signingProcessingService.getSignInfoExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN))
        .thenReturn(new SignInfo());
    mockMvc
        .perform(
            get("/sign-info/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .getSignInfoExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] View the document")
  void testViewTheDocumentExternalController() throws Exception {
    when(signingProcessingService.viewDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.DOC_ID, UnitTestConstant.TOKEN))
        .thenReturn("base64 string");
    mockMvc
        .perform(
            get("/documents/view/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("docId", UnitTestConstant.DOC_ID)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .viewDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.DOC_ID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Validate document")
  void testValidateTheDocumentExternalController() throws Exception {
    final MultipartFile docFront =
        new MockMultipartFile(
            "frontDoc", "frontDoc.jpg", MediaType.IMAGE_PNG_VALUE, "backDoc.png".getBytes());
    final MultipartFile docBack =
        new MockMultipartFile(
            "backDoc", "backDoc.jpg", MediaType.IMAGE_PNG_VALUE, "backDoc.png".getBytes());
    final var request = new DocumentVerificationRequest();
    request.setDocumentFront(docFront);
    request.setDocumentBack(docBack);
    request.setDocumentCountry("fr");
    request.setDocumentRotation(0);
    request.setDocumentType("id_card");

    when(signingProcessingService.validateDocumentExternal(
            eq(UnitTestConstant.COMPANY_UUID), eq(UnitTestConstant.TOKEN), any(DocumentVerificationRequest.class)))
        .thenReturn(new VerificationDocumentResponse());
    mockMvc
        .perform(
            multipart("/validate/document/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .file("documentFront", request.getDocumentFront().getBytes())
                .file("documentBack", request.getDocumentBack().getBytes())
                .param("documentCountry", request.getDocumentCountry().toString())
                .param("documentRotation", request.getDocumentRotation().toString())
                .param("documentType", request.getDocumentType().toString())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .validateDocumentExternal(eq(UnitTestConstant.COMPANY_UUID), eq(UnitTestConstant.TOKEN), any(DocumentVerificationRequest.class));
  }

  @Test
  @DisplayName("[Public] Validate phone number")
  void testValidatePhoneNumberExternalController() throws Exception {
    when(signingProcessingService.validatePhoneNumberExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.PHONE_NUMBER, UnitTestConstant.TOKEN))
        .thenReturn(new ValidPhone());
    mockMvc
        .perform(
            post("/otp/validate/phone-number/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN)
                .param("phone", UnitTestConstant.PHONE_NUMBER))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .validatePhoneNumberExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.PHONE_NUMBER, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Generate OTP")
  void testGenerateOtpExternalController() throws Exception {
    signingProcessingService.generateOtpExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            post("/otp/generate/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(2))
        .generateOtpExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Validate OTP")
  void testValidateOtpExternalController() throws Exception {
    when(signingProcessingService.validateOtpExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.OTP_CODE))
        .thenReturn(new OtpInfo());
    mockMvc
        .perform(
            post("/otp/validate/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN)
                .param("otp", UnitTestConstant.OTP_CODE))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .validateOtpExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.OTP_CODE);
  }

  @Test
  @DisplayName("Signing the document")
  void testSigningTheDocumentExternalController() throws Exception {
    signingProcessingService.signDocumentsExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            post("/sign/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(2))
        .signDocumentsExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Download document after signed")
  void testDownloadDocumentAfterSignedExternalController() throws Exception {
    when(signingProcessingService.downloadSignedDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.DOC_ID))
        .thenReturn(fileResponse);
    mockMvc
        .perform(
            get("/sign/download/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("docId", UnitTestConstant.DOC_ID)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .downloadSignedDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.DOC_ID);
  }

  @Test
  @DisplayName("[Public] Upload document for signing after modified")
  void testUploadModifiedDocumentExternalController() throws Exception {
    MultipartFile file =
        new MockMultipartFile("file1", "file1.pdf", "application/pdf", "Hello, World!".getBytes());
    signingProcessingService.uploadModifiedDocumentExternal(
        UnitTestConstant.COMPANY_UUID, file, UnitTestConstant.DOC_ID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            multipart("/sign/upload/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .file("file", file.getBytes())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("docId", UnitTestConstant.DOC_ID)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .uploadModifiedDocumentExternal(
            UnitTestConstant.COMPANY_UUID, file, UnitTestConstant.DOC_ID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Setup signing flow for individual")
  void testSetupSigningFlowForIndividualExternalController() throws Exception {
    signingProcessingService.setupIndividualSignProcessExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            post("/sign/individual/setup/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(2))
        .setupIndividualSignProcessExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Generate otp value")
  void testGenerateOtpValueExternalController() throws Exception {
    when(signingProcessingService.generateAndResponseOtp(
            UnitTestConstant.FLOW_ID, UnitTestConstant.UUID))
        .thenReturn(Optional.empty());
    mockMvc
        .perform(
            post("/otp/generate/{flowId}/value", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("uuid", UnitTestConstant.UUID))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .generateAndResponseOtp(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[Public] Setup individual process otp value")
  void testSetUpProcessIndividualExternalController() throws Exception {
    when(signingProcessingService.setupIndividualProcessOTPValue(
            UnitTestConstant.FLOW_ID, UnitTestConstant.UUID))
        .thenReturn(Optional.empty());
    mockMvc
        .perform(
            post("/sign/individual/setup/{flowId}/value", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("uuid", UnitTestConstant.UUID))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .setupIndividualProcessOTPValue(UnitTestConstant.FLOW_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[Public] Upload signature document external")
  void testUploadSignatureFileExternalController() throws Exception {
    var mode = SignatureMode.WRITE;
    var file = new MockMultipartFile("backDoc", "backDoc.jpg", null, "backDoc.png".getBytes());
    signingProcessingService.uploadSignatureFileExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, mode, file);
    mockMvc
        .perform(
            multipart("/sign/upload/{companyUuid}/signature", UnitTestConstant.COMPANY_UUID)
                .file("file", file.getBytes())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("token", UnitTestConstant.TOKEN)
                .param("file", String.valueOf(file))
                .param("mode", "WRITE"))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .uploadSignatureFileExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, mode, file);
  }

  @Test
  @DisplayName("[Public] View signature document external")
  void testViewSignatureFileExternalController() throws Exception {
    when(signingProcessingService.viewSignatureFileExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN))
        .thenReturn("base64 string");
    mockMvc
        .perform(
            get("/sign/view/{companyUuid}/signature", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(1))
        .viewSignatureFileExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Remove signature external")
  void testRemoveSignatureFileExternalController() throws Exception {
    signingProcessingService.removeSignatureFileExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            delete("/sign/remove/{companyUuid}/signature", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(signingProcessingService, times(2))
        .removeSignatureFileExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Request video authorization")
  void testRequestVideoAuthorizationExternalController() throws Exception {
    when(signingProcessingService.requestVideoIDAuthenticationExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN))
        .thenReturn(new VideoIDAuthorizationDto());
    this.mockMvc
        .perform(
            get("/sign/authorization/{companyUuid}/video-id", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    verify(signingProcessingService, times(1))
        .requestVideoIDAuthenticationExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Request video verification")
  void testRequestVideoVerificationExternalController() throws Exception {
    when(signingProcessingService.requestVerificationVideoIDExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.VIDEO_ID))
        .thenReturn(new VideoIDVerificationDto());
    this.mockMvc
        .perform(
            post("/sign/verification/{companyUuid}/video-id", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN)
                .param("videoId", UnitTestConstant.VIDEO_ID))
        .andExpect(status().isOk());

    verify(signingProcessingService, times(1))
        .requestVerificationVideoIDExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.VIDEO_ID);
  }

  @Test
  @DisplayName("[Public] Request to sign a document")
  void testRequestToSignDocument() throws Exception {
    when(signingProcessingService.requestToSignDocumentsExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN))
        .thenReturn(new RequestSignViaSmsResponse());
    this.mockMvc
        .perform(
            post("/sign/request-sign/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    verify(signingProcessingService, times(1))
        .requestToSignDocumentsExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @Test
  @DisplayName("[Public] Signing documents")
  void testSignDocument() throws Exception {
    when(signingProcessingService.signDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.OTP_CODE))
        .thenReturn(Boolean.TRUE);
    this.mockMvc
        .perform(
            post("/sign/sign-document/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN)
                .param("otpCode", UnitTestConstant.OTP_CODE))
        .andExpect(status().isOk());

    verify(signingProcessingService, times(1))
        .signDocumentExternal(
            UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN, UnitTestConstant.OTP_CODE);
  }
}
