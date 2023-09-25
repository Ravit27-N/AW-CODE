package com.tessi.cxm.pfl.ms5.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tessi.cxm.pfl.ms5.dto.FileMetadata;
import com.tessi.cxm.pfl.ms5.service.MockStorageFileService;
import com.tessi.cxm.pfl.ms5.service.StorageFileService;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(
    value = FileStorageController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {FileStorageController.class, ProfileGlobalExceptionHandler.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class FileStorageControllerTest {

  private final String BASE_URL = "/v1/storage";
  @Autowired private MockMvc mockMvc;

  @MockBean private StorageFileService storageFileService;

  // @Value("classpath:/client_1.pdf")
  private File client1PdfFile;
  private MockMultipartFile mockPdfMultipartFile;
  private FilePropertiesHandling fileProperties;
  private FileMetadata fileMetadata;
  private final UserAccessDeniedExceptionHandler userAccessDeniedException =
      new UserAccessDeniedExceptionHandler();

  @BeforeEach
  void setUp() throws Exception {
    final var requestParamName = "file";
    final String resourcePath = "src/test/resources/client_1.pdf";
    this.client1PdfFile = new File(resourcePath);
    mockPdfMultipartFile =
        new MockMultipartFile(
            requestParamName,
            client1PdfFile.getName(),
            MediaType.APPLICATION_PDF_VALUE,
            new FileInputStream(client1PdfFile));

    fileProperties =
        FilePropertiesHandling.builder()
            .fileId(UUID.randomUUID().toString())
            .fileName(mockPdfMultipartFile.getName())
            .originalName(mockPdfMultipartFile.getOriginalFilename())
            .fileSize(mockPdfMultipartFile.getSize())
            .extension("pdf")
            .build();
    fileMetadata =
        FileMetadata.builder()
            .filename(mockPdfMultipartFile.getOriginalFilename())
            .content(
                IOUtils.toString(mockPdfMultipartFile.getInputStream(), StandardCharsets.UTF_8))
            .fileSize(mockPdfMultipartFile.getSize())
            .build();
  }

  @Test
  @Order(1)
  void successUploadSingleFile() throws Exception {
    // Stub
    when(this.storageFileService.uploadSingleFile(
            ArgumentMatchers.any(MultipartFile.class), anyString()))
        .thenReturn(this.fileProperties);

    // Call API and assert
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(BASE_URL + "/store")
                    .file(this.mockPdfMultipartFile)
                    .characterEncoding(StandardCharsets.UTF_8))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void noPermissionToUploadSingleFile() throws Exception {
    // Stub
    when(this.storageFileService.isAdmin()).thenReturn(true);
    when(storageFileService.uploadSingleFile(
            ArgumentMatchers.any(MultipartFile.class), anyString()))
        .thenThrow(userAccessDeniedException);

    // Call API and assert
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.multipart(BASE_URL + "/store")
                    .file(this.mockPdfMultipartFile)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isForbidden())
            .andExpect(
                jsonPath("$.apierrorhandler.message").value(userAccessDeniedException.getMessage()))
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void successDeleteFileByFileId() throws Exception {
    final var fileId = this.fileProperties.getFileId();
    // Stub
    when(this.storageFileService.isAdmin()).thenReturn(true);
    Mockito.doNothing().when(this.storageFileService).deleteFile(eq(fileId), anyString());

    // Call API and assert
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/delete/{fileId}", fileId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(4)
  void noPermissionDeleteFileByFileId() throws Exception {
    final var fileId = this.fileProperties.getFileId();
    // Stub
    when(this.storageFileService.isAdmin()).thenReturn(false);
    Mockito.doThrow(userAccessDeniedException)
        .when(this.storageFileService)
        .deleteFile(eq(fileId), anyString());

    // Call API and assert
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/delete/{fileId}", fileId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isForbidden())
            .andExpect(
                jsonPath("$.apierrorhandler.message").value(userAccessDeniedException.getMessage()))
            .andReturn();

    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  void successGetFileOfCurrentClient() throws Exception {
    final var fileId = this.fileProperties.getFileId();
    // Stub
    when(this.storageFileService.getFileContent()).thenReturn(this.fileMetadata);

    // Call API and assert
    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(BASE_URL + "/download", fileId))
            .andExpect(status().isOk())
            .andReturn();
  }

  @Test
  @Order(6)
  void successGetFileOfCurrentClientWithNoContent() throws Exception {
    final var fileId = this.fileProperties.getFileId();
    // Stub
    when(this.storageFileService.getFileContent()).thenReturn(null);

    // Call API and assert
    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(BASE_URL + "/download", fileId))
            .andExpect(status().isNoContent())
            .andReturn();
  }
}
