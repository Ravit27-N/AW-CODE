package com.tessi.cxm.pfl.ms5.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.exception.UserRepresentationNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class StorageFileServiceTest {

  @Mock private FileManagerResource fileManagerResource;
  @Mock private ProfileService profileService;
  @Mock private ClientRepository clientRepository;

  @Mock private UserRepository userRepository;

  private MockStorageFileService storageFileService;
  @Mock private KeycloakService keycloakService;
  @Mock SettingFeignClient settingFeignClient;

  private File client1PdfFile;

  private MockMultipartFile mockPdfMultipartFile;
  private MockMultipartFile mockPlainTextMultipartFile;
  private FilePropertiesHandling pdfFileProperties;

  private FileMetadata pdfFileMetadata;

  @BeforeEach
  void setUp() throws IOException {
    this.storageFileService =
        new MockStorageFileService(
            this.fileManagerResource,
            this.profileService,
            this.clientRepository,
            this.userRepository,
            keycloakService,
            settingFeignClient);
    this.storageFileService.setUserId("001");
    final var requestParamName = "file";

    final String resourcePath = "src/test/resources/client_1.pdf";
    this. client1PdfFile = new File(resourcePath);

    mockPdfMultipartFile =
        new MockMultipartFile(
            requestParamName,
            client1PdfFile.getName(),
            MediaType.APPLICATION_PDF_VALUE,
            new FileInputStream(client1PdfFile));
    pdfFileProperties =
        FilePropertiesHandling.builder()
            .fileId(UUID.randomUUID().toString())
            .fileName(mockPdfMultipartFile.getName())
            .originalName(mockPdfMultipartFile.getOriginalFilename())
            .fileSize(mockPdfMultipartFile.getSize())
            .extension("pdf")
            .build();
    pdfFileMetadata =
        FileMetadata.builder()
            .uuid(UUID.randomUUID().toString())
            .fileName(mockPdfMultipartFile.getName())
            .content(IOUtils.toString(new FileInputStream(client1PdfFile), StandardCharsets.UTF_8))
            .refFile("")
            .size(mockPdfMultipartFile.getSize())
            .contentType("application/pdf")
            .build();

    mockPlainTextMultipartFile =
        new MockMultipartFile(
            requestParamName, "text.txt", MediaType.TEXT_PLAIN_VALUE, "Plain text".getBytes());
  }

  @Test
  @Order(1)
  void successUploadSingleFile() {
    Optional<UserEntity> userEntity = Optional.of(UserEntity.builder().id(1L).build());
    this.storageFileService.setSupperAdmin(true);

    when(this.userRepository.findByTechnicalRefAndIsActiveTrue(anyString())).thenReturn(userEntity);
    when(this.fileManagerResource.uploadFile(
            any(MultipartFile.class),
            any(Long.class),
            anyString(),
            anyString(),
            anyString(),
            anyInt(),
            anyBoolean(),
            anyString(),
            anyString(),
            anyString()))
        .thenReturn(this.pdfFileMetadata);

    var fileProperties = this.storageFileService.uploadSingleFile(this.mockPdfMultipartFile, "");

    Assertions.assertEquals(this.pdfFileMetadata.getUuid(), fileProperties.getFileId());
  }

  @Test
  @Order(2)
  void noPermissionToUploadSingleFile() {
    this.storageFileService.setSupperAdmin(false);
    when(this.profileService.notContainsPrivilege(anyString(), anyString())).thenReturn(true);

    Assertions.assertThrows(
        UserAccessDeniedExceptionHandler.class,
        () -> this.storageFileService.uploadSingleFile(this.mockPdfMultipartFile, ""));
  }

  @Test
  @Order(3)
  void failedWhenUploadNonePdfFile() {
    this.storageFileService.setSupperAdmin(true);

    Assertions.assertThrows(
        FileErrorException.class,
        () -> this.storageFileService.uploadSingleFile(this.mockPlainTextMultipartFile, ""));
  }

  @Test
  @Order(4)
  void successDeleteFileByFileId() {
    final var refFileId = this.pdfFileProperties.getFileId();
    this.storageFileService.setSupperAdmin(true);

    Mockito.doNothing()
        .when(this.fileManagerResource)
        .deleteMetadataFile(eq(refFileId), anyString(), anyString(), anyString());

    Assertions.assertDoesNotThrow(() -> this.storageFileService.deleteFile(refFileId, ""));
  }

  @Test
  @Order(5)
  void noPermissionToDeleteFileByFileId() {
    final var refFileId = this.pdfFileProperties.getFileId();
    this.storageFileService.setSupperAdmin(false);
    when(this.profileService.notContainsPrivilege(anyString(), anyString())).thenReturn(true);

    Assertions.assertThrows(
        UserAccessDeniedExceptionHandler.class,
        () -> this.storageFileService.deleteFile(refFileId, ""));
  }

  @Test
  @Order(6)
  void successDeleteFileByClientAdmin() {
    this.storageFileService.setSupperAdmin(false);
    final var refFileId = this.pdfFileProperties.getFileId();
    when(clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));

    Mockito.doNothing()
        .when(this.fileManagerResource)
        .deleteMetadataFile(any(), anyString(), anyString(), anyString());

    Assertions.assertDoesNotThrow(() -> this.storageFileService.deleteFile(refFileId, ""));
  }

  @Test
  @Order(7)
  void testDeleteFileByClientAdmin_ThenThrowUserAccessDeniedException() {
    this.storageFileService.setSupperAdmin(false);
    final var refFileId = this.pdfFileProperties.getFileId();

    when(this.profileService.notContainsPrivilege(anyString(), anyString())).thenReturn(true);

    Assertions.assertThrows(
        UserAccessDeniedExceptionHandler.class,
        () -> this.storageFileService.deleteFile(refFileId, ""));
  }

  @Test
  @Order(8)
  void successGetFileOfCurrentClient() {
    when(this.clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_CLIENT_1));
    when(this.fileManagerResource.getFile(any(), anyString(), anyString(), anyString()))
        .thenReturn(this.pdfFileMetadata);
    when(this.keycloakService.getToken()).thenReturn("");

    var fileContent = this.storageFileService.getFileContent();

    Assertions.assertEquals(
        ProfileUnitTestConstants.SAMPLE_CLIENT_1.getFilename(), fileContent.getFilename());
    Assertions.assertEquals(
        ProfileUnitTestConstants.SAMPLE_CLIENT_1.getFileSize(), fileContent.getFileSize());
  }

  @Test
  @Order(9)
  void testDownloadWithNoPDFFileOfCurrentClient() {
    var sampleClient1 =
        new ModelMapper().map(ProfileUnitTestConstants.SAMPLE_CLIENT_1, Client.class);
    sampleClient1.setFileId(null);
    this.storageFileService.setUserId("user_001");

    when(this.clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.of(sampleClient1));

    var fileContent = this.storageFileService.getFileContent();

    Assertions.assertNull(fileContent);
  }

  @Test
  @Order(10)
  void failedToGetFileOfCurrentClient() {
    when(this.clientRepository.findOne(ArgumentMatchers.<Specification<Client>>any()))
        .thenReturn(Optional.empty());

    Assertions.assertThrows(
        UserRepresentationNotFoundException.class, () -> this.storageFileService.getFileContent());
  }
}
