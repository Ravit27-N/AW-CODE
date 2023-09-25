package com.tessi.cxm.pfl.ms8.service;

import static com.tessi.cxm.pfl.ms8.constant.ConstantProperties.USER_PRIVILEGE_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms8.constant.ConstantProperties;
import com.tessi.cxm.pfl.ms8.entity.Watermark;
import com.tessi.cxm.pfl.ms8.exception.WatermarkDuplicatedException;
import com.tessi.cxm.pfl.ms8.exception.WatermarkNotFoundException;
import com.tessi.cxm.pfl.ms8.repository.WatermarkRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
@Slf4j
class WatermarkServiceTest {

  @Mock private WatermarkService watermarkService;
  @Mock private WatermarkRepository watermarkRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private static ProfileFeignClient profileFeignClient;
  private static final String TEST_USERNAME = "tessi";

  @BeforeEach
  void setUp() {
    this.watermarkService =
        new WatermarkService(watermarkRepository, profileFeignClient, modelMapper);
  }

  @Test
  @Order(1)
  void testSaveWatermark_thenSuccess() {
    var watermarkRequest = ConstantProperties.WATERMARK_DTO;
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class); ) {
      utils
          .when(() -> AuthenticationUtils.getAuthToken(any(Authentication.class)))
          .thenReturn("token");
      UserDetail userDetail = UserDetail.builder().ownerId(47L).username("test").build();
      when(watermarkService.getUserDetail()).thenReturn(userDetail);
      when(this.modelMapper.map(ConstantProperties.WATERMARK_DTO, Watermark.class))
          .thenReturn(ConstantProperties.WATERMARK);

      when(this.watermarkRepository.save(any())).thenReturn(ConstantProperties.WATERMARK);

      var response = this.watermarkService.saveWatermark(watermarkRequest);
      log.info("Response: {}", response);

      Assertions.assertNotNull(response, "Response must be not null");
    }
  }

  @Test
  @Order(2)
  void testSaveWatermark_returnWatermarkDuplicate() {
    var watermarkRequest = ConstantProperties.WATERMARK_DTO;
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getAuthToken(any(Authentication.class)))
          .thenReturn("token");
      when(this.watermarkRepository.findByFlowId(anyString()))
          .thenReturn(Optional.of(ConstantProperties.WATERMARK));
      RuntimeException exception =
          Assertions.assertThrows(
              WatermarkDuplicatedException.class,
              () -> this.watermarkService.saveWatermark(watermarkRequest));
      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(3)
  void testSaveWatermark_returnUnauthorized() {
    var watermarkRequest = ConstantProperties.WATERMARK_DTO;

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class); ) {
      utils
          .when(() -> AuthenticationUtils.getAuthToken(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      when(watermarkService
              .getProfileFeignClient()
              .getUserPrivilegeRelatedOwner(
                  anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());
      var result =
          Assertions.assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.watermarkService.saveWatermark(watermarkRequest));
      log.error("Exception :{}", result.getMessage());
    }
  }

  @Test
  @Order(4)
  void testUpdateWatermark_thenSuccess() {
    var watermarkRequest = ConstantProperties.WATERMARK_DTO;
    try (MockedStatic<AuthenticationUtils> authenticationUtils =
        mockStatic(AuthenticationUtils.class)) {
      authenticationUtils.when(AuthenticationUtils::getPreferredUsername).thenReturn("test");
      when(this.watermarkRepository.findByFlowId(any()))
          .thenReturn(Optional.ofNullable(ConstantProperties.WATERMARK));
      when(watermarkService
              .getProfileFeignClient()
              .getUserPrivilegeRelatedOwner(
                      anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
              .thenReturn(USER_PRIVILEGE_DETAILS);
      var response = this.watermarkService.updateWatermark(watermarkRequest);
      log.info("Response: {}", response);

      Assertions.assertNotNull(response, "Response must be not null");
    }
  }

  @Test
  @Order(5)
  void testUpdateWatermark_returnWatermarkNotFound() {
    var watermarkRequest = ConstantProperties.WATERMARK_DTO;

    try (MockedStatic<AuthenticationUtils> authenticationUtils =
        mockStatic(AuthenticationUtils.class)) {
      authenticationUtils.when(AuthenticationUtils::getPreferredUsername).thenReturn("test");

      when(this.watermarkRepository.findByFlowId(anyString())).thenReturn(Optional.empty());

      RuntimeException exception =
          Assertions.assertThrows(
              WatermarkNotFoundException.class,
              () -> this.watermarkService.updateWatermark(watermarkRequest));
      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(6)
  void testUpdateWatermark_returnUnauthorized() {
    var watermarkRequest = ConstantProperties.WATERMARK_DTO;

    when(this.watermarkRepository.findByFlowId(any()))
        .thenReturn(Optional.ofNullable(Watermark.builder().text("test").build()));

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getAuthToken(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      when(watermarkService
              .getProfileFeignClient()
              .getUserPrivilegeRelatedOwner(
                  anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());
      var result =
          Assertions.assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.watermarkService.updateWatermark(watermarkRequest));
      log.error("Exception :{}", result.getMessage());
    }
  }

  @Test
  @Order(7)
  void testDeleteWatermark_thenSuccess() {
    String flowId = "8cd0e247-ceef-42b8-9a40-570e20c83d76";
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);

      this.watermarkService.deleteWatermark(flowId);
      log.info("{}", "Delete success.");
    }
  }

  @Test
  @Order(8)
  void testDeleteWatermark_returnUnauthorized() {
    String flowId = "8cd0e247-ceef-42b8-9a40-570e20c83d76";

    when(this.watermarkRepository.findByFlowId(any()))
        .thenReturn(Optional.ofNullable(Watermark.builder().text("test").build()));

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getAuthToken(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      when(watermarkService
              .getProfileFeignClient()
              .getUserPrivilegeRelatedOwner(
                  anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());
      var result =
          Assertions.assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.watermarkService.deleteWatermark(flowId));
      log.error("Exception :{}", result.getMessage());
    }
  }
}
