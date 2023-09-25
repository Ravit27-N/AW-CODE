package com.tessi.cxm.pfl.ms15.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms15.model.DocumentInstructions;
import com.tessi.cxm.pfl.ms15.model.DocumentInstructions.DocumentInstructionData;
import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.filectrl.model.Document;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.meta.When;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PreProcessingServiceTest {

  @Mock private FileService fileService;
  @Mock private FileManagerResource fileManagerResource;
  @Mock private SettingFeignClient settingFeignClient;
  @Mock private KeycloakService keycloakService;
  @Mock private Go2pdfResource go2pdfFeignClient;

  @InjectMocks private PreProcessingService preProcessingService;

  @Mock private DocumentExtractorService documentExtractorService;
  Resource resource = Mockito.mock(Resource.class);

  @BeforeEach
  void setUp() {
    this.preProcessingService =
        new PreProcessingService(
            fileService,
            fileManagerResource,
            settingFeignClient,
            keycloakService,
            go2pdfFeignClient);
    this.preProcessingService.setResource(resource);
  }

  @Test
  @DisplayName("Should return document object when retrieved the request.")
  void shouldReturnDocument_whenRetrievedRequest() throws FileNotFoundException {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      var mockInstruction =
          DocumentInstructionData.builder()
              .emailObject("Column 7")
              .recipientId("Column 4")
              .emailRecipient("Column 6")
              .address("18 to 21")
              .pjs("17 and 11 to 14")
              .idBreakingPage("/XXX/")
              .data("26 to 27")
              .build();
      var fileMetadataMock = Mockito.mock(FileMetadata.class);
      // Get file from response.
      File mockFile =
          ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "file_testing.csv");
      Assert.notNull(mockFile, "File must not be null");

      List<FilePropertiesHandling> mockFileInfo =
          List.of(
              FilePropertiesHandling.builder()
                  .fileId("FILE_ID")
                  .fileName("file_testing.csv")
                  .filePath(mockFile.getAbsolutePath())
                  .extension("zip")
                  .fileSize(mockFile.length())
                  .build());

      var mockResponseInstruction =
          new DocumentInstructions(
              "Finished", 200, Timestamp.valueOf(LocalDateTime.now()), mockInstruction);

      when(this.keycloakService.getToken()).thenReturn("token");
      when(this.settingFeignClient.getDocumentInstructions(
          anyString(), anyString(), anyLong(), anyString()))
          .thenReturn(mockResponseInstruction);
      //      when(this.fileManagerResource.getBase64(anyString())).thenReturn("BASE_64_FILE");
      when(this.fileService.getPath(any())).thenReturn(mockFile.toPath());
      doNothing().when(this.fileService).unZipCommand(any(Path.class), any(Path.class));
      when(this.fileService.getFileInfo(anyString(), any())).thenReturn(mockFileInfo);
      doNothing().when(this.fileService).decodeBase64ToFile(anyString(),anyString());
      when(this.fileManagerResource.getFile(anyString(),anyString(),anyString(),anyString())).thenReturn(fileMetadataMock);
      when(fileMetadataMock.getContent()).thenReturn("test");
      var result =
          this.preProcessingService.getDocuments(
              "MODEL_NAME", "ENI/Batch/C1/zip", "FILE_ID", 1L, "Digital", "Email", "123", "123");

      // expected actual result.
      var actualResponse = (Document) result.getData();
      assertNotNull(result.getData(), "Object data must be not null.");
      assertEquals(200, result.getStatus());
      assertNotEquals(new ArrayList<>(), actualResponse.getFlowDocuments());

      log.info("Actual response: {}", result.getData());
    }
  }
}
