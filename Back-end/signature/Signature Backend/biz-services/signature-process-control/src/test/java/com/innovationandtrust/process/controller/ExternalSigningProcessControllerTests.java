package com.innovationandtrust.process.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.service.SigningProcessingService;
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
  @InjectMocks
  ExternalSigningProcessController controller;
  @Mock SigningProcessingService service;
  @Mock
  FileResponse fileResponse;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    fileResponse = new FileResponse();
    fileResponse.setFilename("abc.pdf");
    fileResponse.setSize(1200);
    fileResponse.setContentType("application/json");
    fileResponse.setResource("123".getBytes());
  }

  @DisplayName("Sign info")
  @Test
  void find_documents() throws Exception {
    service.getSignInfo("123", "123");
    mockMvc
        .perform(
            get("/sign-info/{flowId}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .param("uuid", "123"))
        .andExpect(status().isOk());

    // then
    verify(service, times(2)).getSignInfo("123", "123");
  }

  @DisplayName("View the document")
  @Test
  void view_the_document() throws Exception {
    service.viewDocument("123", "123");
    mockMvc
        .perform(
            get("/documents/view/{flowId}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .param("docId", "123"))
        .andExpect(status().isOk());

    // then
    verify(service, times(2)).viewDocument("123", "123");
  }

  @DisplayName("Validate phone number")
  @Test
  void validate_phone_number() throws Exception {
    service.validatePhoneNumber("123", "123", "123");
    mockMvc
        .perform(
            post("/otp/validate/phone-number/{flowId}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .param("uuid", "123")
                .param("phone", "085777868"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).validatePhoneNumber("123", "123", "123");
  }

  @DisplayName("Generate OTP")
  @Test
  void generate_otp() throws Exception {
    service.generateOtp("123", "123");
    mockMvc
        .perform(
            post("/otp/generate/{flowId}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .param("uuid", "123"))
        .andExpect(status().isOk());

    // then
    verify(service, times(2)).generateOtp("123", "123");
  }

  @DisplayName("Validate OTP")
  @Test
  void validate_otp() throws Exception {
    service.validateOtp("123", "123", "123");
    mockMvc
        .perform(
            post("/otp/generate/{flowId}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .param("uuid", "123")
                .param("otp", "123"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).validateOtp("123", "123", "123");
  }
  @DisplayName("Signing the document")
  @Test
  void signing_the_document() throws Exception {
    service.signDocuments("123", "123");
    mockMvc
            .perform(
                    post("/sign/{flowId}", "123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("uuid", "123"))
            .andExpect(status().isOk());

    // then
    verify(service, times(2)).signDocuments("123", "123");
  }
  @DisplayName("Download document after signed")
  @Test
  void download_document_after_signed() throws Exception {
    when(service.downloadSignedDocument("123", "123")).thenReturn(fileResponse);
    mockMvc
            .perform(
                    get("/sign/download/{flowId}", "123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("docId", "123"))
            .andExpect(status().isOk());

    // then
    verify(service, times(1)).downloadSignedDocument("123", "123");
  }
  @DisplayName("Upload document for signing after modified")
  @Test
  void upload_modified_document() throws Exception {
    MultipartFile file =
                    new MockMultipartFile("file1", "file1.pdf", "application/pdf", "Hello, World!".getBytes());
    service.uploadModifiedDocument(file, "123", "123");
    mockMvc
            .perform(
                    multipart("/sign/upload/{flowId}", "123")
                            .file("file", file.getBytes())
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .param("docId", "123"))
            .andExpect(status().isOk());

    // then
    verify(service, times(1)).uploadModifiedDocument(file, "123", "123");
  }
  @DisplayName("Setup signing flow for individual")
  @Test
  void setup_signing_flow_for_individual() throws Exception {
    service.setupIndividualSignProcess("123", "123");
    mockMvc
            .perform(
                    post("/sign/individual/setup/{flowId}", "123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("uuid", "123"))
            .andExpect(status().isOk());

    // then
    verify(service, times(2)).setupIndividualSignProcess("123", "123");
  }
}
