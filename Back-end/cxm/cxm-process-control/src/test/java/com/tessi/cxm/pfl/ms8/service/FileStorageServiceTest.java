package com.tessi.cxm.pfl.ms8.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms8.constant.ConstantProperties;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Slf4j
class FileStorageServiceTest {

  @Mock
  private FileService fileService;

  @Mock
  private FileStorageService fileStorageService;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private ProfileFeignClient profileFeignClient;

  @BeforeEach
  void setup() {
    this.fileStorageService = spy(
        new FileStorageService(fileService, ConstantProperties.LOCAL_FILE_CONFIG, modelMapper));
    this.fileStorageService.setPrivilegeValidationUtil(profileFeignClient);
  }

  @Test
  @Order(1)
  void whenUploadBackgroundFile_ThenReturnSuccess() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");

      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");

      try (MockedStatic<BackgroundFileValidatorUtil> backgroundFileValidatorUtilMockedStatic = mockStatic(
          BackgroundFileValidatorUtil.class)) {
        backgroundFileValidatorUtilMockedStatic
            .when(() -> BackgroundFileValidatorUtil.getNumberOfPagesWithValidation(
                any(MultipartFile.class), anyInt()))
            .thenReturn(1);

        when(this.fileService.uploadSingleFile(any(MultipartFile.class), anyBoolean(), anyString(),
            any()))
            .thenReturn(ConstantProperties.FILE_PROPERTIES_HANDLING);

        when(this.modelMapper.map(ConstantProperties.FILE_PROPERTIES_HANDLING,
            ResourceFileMetaDataResponseDto.class))
            .thenReturn(ConstantProperties.BACKGROUND_FILE_META_DATA_RESPONSE_DTO);

        var result = this.fileStorageService.uploadResource(
            ConstantProperties.MOCK_MULTIPART_FILE, "","");

        log.info("Result: {}", result);
        Assertions.assertNotNull(result, "Result must be not null");
      }
    }
  }

  @Test
  @Order(2)
  void whenRemoveFile_ThenReturnSuccess() {
    String fileId = "8cd0e247-ceef-42b8-9a40-570e20c83d76";
    String flowId = "1";

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");

      utils.when(AuthenticationUtils::getAuthToken).thenReturn("base64");

      this.fileStorageService.removeFile(fileId, flowId);
      verify(this.fileStorageService, times(1)).removeFile(anyString(), anyString());
    }
  }
}
