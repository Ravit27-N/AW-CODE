package com.tessi.cxm.pfl.ms8.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms8.constant.ConstantProperties;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.ms8.dto.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.ms8.util.BackgroundFileUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.ResourceFileType;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ResourceFileServiceTest {

  @Mock private FileManagerResource fileManagerResource;
  @Mock private ResourceFileRepository resourceFileRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private SettingFeignClient settingFeignClient;
  @Mock private ResourceFileService resourceFileService;

  @BeforeEach
  void setup() {
    this.resourceFileService =
        spy(
            new ResourceFileService(
                fileManagerResource,
                resourceFileRepository,
                modelMapper,
                ConstantProperties.LOCAL_FILE_CONFIG,
                profileFeignClient,
                settingFeignClient));

    this.resourceFileService.setPrivilegeValidationUtil(profileFeignClient);
  }

  @BeforeAll
  static void initTransactionSynchronizationManager() {
    TransactionSynchronizationManager.initSynchronization();
  }

  @AfterAll
  static void clear() {
    TransactionSynchronizationManager.clear();
  }

  @Test
  @Order(1)
  void whenSaveBackgroundFileOfType_ONE_TIME_UPLOAD_ThenReturnSuccess() {
    var responseEntity = ConstantProperties.RESOURCE_FILE;
    responseEntity.setId(1L);
    responseEntity.setPosition(BackgroundPosition.FIRST_PAGE.value);
    responseEntity.setType(ResourceFileType.ONE_TIME_UPLOAD.getValue());

    var requestDto = ConstantProperties.RESOURCE_FILE_META_DATA_REQUEST_DTO;
    requestDto.setPosition(BackgroundPosition.NEXT_PAGES.value);
    requestDto.setType(ResourceType.BACKGROUND.getValue());
    requestDto.setSource(ResourceFileType.ONE_TIME_UPLOAD.getValue());
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");

      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");

      try (MockedStatic<BackgroundFileUtil> backgroundFileUtilMockedStatic =
          mockStatic(BackgroundFileUtil.class)) {
        backgroundFileUtilMockedStatic
            .when(() -> BackgroundFileUtil.findMultipartFileWithPrefix(any(), anyString()))
            .thenReturn(ConstantProperties.MOCK_MULTIPART_FILE);

        try (MockedStatic<BackgroundFileValidatorUtil> backgroundFileValidatorUtilMockedStatic =
            mockStatic(BackgroundFileValidatorUtil.class)) {
          backgroundFileValidatorUtilMockedStatic
              .when(() -> BackgroundFileValidatorUtil.getNumberOfPages((MultipartFile) any()))
              .thenReturn(1);

          when(this.profileFeignClient.getUserDetail(anyString()))
              .thenReturn(ConstantProperties.USER_DETAIL);

          when(this.resourceFileRepository.findAllByFlowId(anyString()))
              .thenReturn(List.of(responseEntity));

          when(this.resourceFileRepository.save(any(ResourceFile.class)))
              .thenReturn(responseEntity);

          var response = this.resourceFileService.save(requestDto);
          log.info("Response: {}", response);

          Assertions.assertNotNull(response, "Response must be not null");
        }
      }
    }
  }

  @Test
  @Order(2)
  void whenSaveBackgroundFileOfType_LIBRARY_ThenReturnSuccess() {
    var responseEntity = ConstantProperties.RESOURCE_FILE;
    responseEntity.setId(1L);
    responseEntity.setPosition(BackgroundPosition.FIRST_PAGE.value);
    responseEntity.setSource(ResourceFileType.LIBRARY.getValue());
    responseEntity.setType(ResourceType.BACKGROUND.getValue());

    var requestDto = ConstantProperties.RESOURCE_FILE_META_DATA_REQUEST_DTO;
    requestDto.setPosition(BackgroundPosition.NEXT_PAGES.value);
    requestDto.setSource(ResourceFileType.LIBRARY.getValue());
    requestDto.setType(ResourceType.BACKGROUND.getValue());

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");

      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");

      try (MockedStatic<BackgroundFileUtil> backgroundFileUtilMockedStatic =
          mockStatic(BackgroundFileUtil.class)) {
        backgroundFileUtilMockedStatic
            .when(() -> BackgroundFileUtil.findMultipartFileWithPrefix(any(), anyString()))
            .thenReturn(ConstantProperties.MOCK_MULTIPART_FILE);

        try (MockedStatic<BackgroundFileValidatorUtil> backgroundFileValidatorUtilMockedStatic =
            mockStatic(BackgroundFileValidatorUtil.class)) {
          backgroundFileValidatorUtilMockedStatic
              .when(() -> BackgroundFileValidatorUtil.getNumberOfPages((MultipartFile) any()))
              .thenReturn(1);

          when(settingFeignClient.getResource(anyString(), anyString()))
              .thenReturn(
                  ResourceLibraryDto.builder().label("devgmail").fileName("devgmail.pdf").build());
          when(this.profileFeignClient.getUserDetail(anyString()))
              .thenReturn(ConstantProperties.USER_DETAIL);

          when(this.resourceFileRepository.findAllByFlowId(anyString()))
              .thenReturn(List.of(responseEntity));

          when(this.resourceFileRepository.save(any(ResourceFile.class)))
              .thenReturn(responseEntity);
          //
          //          when(this.fileManagerResource.getFile(anyString(), anyString(), anyString(),
          // anyString()))
          //              .thenReturn(ConstantProperties.FILE_METADATA);

          var response = this.resourceFileService.save(requestDto);
          log.info("Response: {}", response);

          Assertions.assertNotNull(response, "Response must be not null");
        }
      }
    }
  }

  @Test
  @Order(3)
  void whenGetBackgroundFileByFileId_ThenReturnSuccess() {

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(UserPrivilegeDetails.builder().relatedOwners(List.of(1L)).build());
      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");
      when(this.resourceFileRepository.findById(anyLong()))
          .thenReturn(Optional.of(ConstantProperties.RESOURCE_FILE));

      when(this.fileManagerResource.getFile(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(ConstantProperties.FILE_METADATA);

      when(this.modelMapper.map(
              ConstantProperties.RESOURCE_FILE, ResourceFileMetaDataResponseDto.class))
          .thenReturn(ConstantProperties.BACKGROUND_FILE_META_DATA_RESPONSE_DTO);

      var response = this.resourceFileService.getResourceFile(1);
      log.info("Response: {}", response);

      Assertions.assertNotNull(response, "Response must be not null");
    }
  }

  @Test
  @Order(4)
  void whenGetAllBackgroundFileByFlowId_ThenReturnSuccess() {
    String flowId = "1";

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");

      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");

      when(this.resourceFileRepository.findAllByFlowId(anyString()))
          .thenReturn(List.of(ConstantProperties.RESOURCE_FILE));

      when(this.fileManagerResource.checkFileExists(any()))
          .thenReturn(List.of(ConstantProperties.FILE_STATUS_DTO));

      when(this.modelMapper.map(
              ConstantProperties.RESOURCE_FILE, ResourceFileMetaDataResponseDto.class))
          .thenReturn(ConstantProperties.BACKGROUND_FILE_META_DATA_RESPONSE_DTO);

      var response = this.resourceFileService.getResourceFilesByFlowId(flowId);
      log.info("Response: {}", response);

      Assertions.assertNotNull(response, "Response must be not null");
    }
  }

  @Test
  @Order(5)
  void whenDeleteBackgroundFileByFileId_ThenReturnSuccess() {
    String fileId = "8cd0e247-ceef-42b8-9a40-570e20c83d76";

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");

      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");

      lenient()
          .when(this.resourceFileRepository.findAllByFileIdIn(anyList()))
          .thenReturn(Optional.of(List.of(ConstantProperties.RESOURCE_FILE)));

      this.resourceFileService.deleteResourceFile(1);
    }
  }
}
