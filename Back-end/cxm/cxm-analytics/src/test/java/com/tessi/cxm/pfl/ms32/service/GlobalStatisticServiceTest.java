package com.tessi.cxm.pfl.ms32.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.exception.ChannelNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.DateInvalidException;
import com.tessi.cxm.pfl.ms32.exception.FillerNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.SubChannelNotFoundException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class GlobalStatisticServiceTest {

  private static final String CHANNEL_NOT_SUPPORT = "Channel not support:";
  private static final String CATEGORY_NOT_SUPPORT = "Category not support:";
  private GlobalStatisticService globalStatisticService;
  @Mock private FlowTraceabilityReportRepository flowTraceabilityReportRepository;

  @Mock private SettingFeignClient settingFeignClient;

  @Mock private ProfileFeignClient profileFeignClient;

  @BeforeEach()
  void setUp() {
    this.globalStatisticService =
        new GlobalStatisticService(
            flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  @Test
  @Order(1)
  void getVolumeReceivedSuccess() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      List<String> categories = ConstantProperties.CATEGORIES;
      GlobalStatisticRequestFilter mockRequestFilter =
          ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
      mockRequestFilter.setCategories(categories);

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any())).thenReturn(
            new CriteriaDistributionsResponse());

        when(this.flowTraceabilityReportRepository.reportDocument(any()))
            .thenReturn(List.of(ConstantProperties.DOCUMENT_TOTAL_DTO));

        var result = this.globalStatisticService.getVolumeReceived(mockRequestFilter);
        log.info("Result: {}", result);
        Assertions.assertNotNull(result, "Result must be not null");
      }
    }
  }

  @Test
  @Order(2)
  void getVolumeReceivedSendingChannelNotFound() {

    List<String> channel = ConstantProperties.CATEGORIES;
    GlobalStatisticRequestFilter mockRequestFilter =
        GlobalStatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    mockRequestFilter.setChannels(channel);

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        try (MockedStatic<org.apache.commons.lang3.StringUtils> validateUtil =
            mockStatic(org.apache.commons.lang3.StringUtils.class)) {

          validateUtil.when(() -> StringUtils.isEmpty(anyString())).thenReturn(true);

          RuntimeException exception =
              Assertions.assertThrows(
                  ChannelNotFoundException.class,
                  () -> this.globalStatisticService.getVolumeReceived(mockRequestFilter));
          Assertions.assertNotNull(exception, CHANNEL_NOT_SUPPORT);
          log.error("Exception :{}", exception.getMessage());
        }
      }
    }
  }

  @Test
  @Order(3)
  void getVolumeReceivedSendingDateInvalidException() {

    GlobalStatisticRequestFilter mockRequestFilter =
        GlobalStatisticRequestFilter.builder().requestedAt(new Date()).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-20"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-19"));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        RuntimeException exception =
            Assertions.assertThrows(
                DateInvalidException.class,
                () -> this.globalStatisticService.getVolumeReceived(mockRequestFilter));
        Assertions.assertNotNull(exception, CHANNEL_NOT_SUPPORT);
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(4)
  void getVolumeReceived_ThenThrowUserAccessDeniedException() {

    GlobalStatisticRequestFilter mockRequestFilter = new GlobalStatisticRequestFilter();

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());

      RuntimeException exception =
          Assertions.assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.globalStatisticService.getVolumeReceived(mockRequestFilter));

      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(5)
  void getGlobalProductionDetailsSuccess() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      GlobalStatisticRequestFilter mockRequestFilter =
          ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
      mockRequestFilter.setEndDate(new Date());

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        when(this.flowTraceabilityReportRepository.reportDocument(any()))
            .thenReturn(List.of(ConstantProperties.DOCUMENT_TOTAL_DTO));

        var result = this.globalStatisticService.getGlobalProductionDetails(mockRequestFilter);
        log.info("Result: {}", result);
        Assertions.assertNotNull(result, "Result must be not null");
      }
    }
  }

  @Test
  @Order(6)
  void getGlobalProductionDetailsSendingSubChannelNotFound() {

    List<String> categories = new ArrayList<>();
    categories.add("CSE");
    GlobalStatisticRequestFilter mockRequestFilter =
        GlobalStatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    mockRequestFilter.setCategories(categories);

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        try (MockedStatic<org.apache.commons.lang3.StringUtils> validateUtil =
            mockStatic(org.apache.commons.lang3.StringUtils.class)) {

          validateUtil.when(() -> StringUtils.isEmpty(anyString())).thenReturn(false);

          validateUtil.when(() -> StringUtils.isEmpty(anyString())).thenReturn(true);

          RuntimeException exception =
              Assertions.assertThrows(
                  SubChannelNotFoundException.class,
                  () -> this.globalStatisticService.getGlobalProductionDetails(mockRequestFilter));
          Assertions.assertNotNull(exception, CATEGORY_NOT_SUPPORT);
          log.error("Exception :{}", exception.getMessage());
        }
      }
    }
  }

  @Test
  @Order(7)
  void getGlobalProductionDetails_ThenThrowUserAccessDeniedException() {

    GlobalStatisticRequestFilter mockRequestFilter = new GlobalStatisticRequestFilter();

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());

      RuntimeException exception =
          Assertions.assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.globalStatisticService.getGlobalProductionDetails(mockRequestFilter));

      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(8)
  void getGlobalProductionDetailsSendingFillerNotFound() {

    List<String> fillers = new ArrayList<>();
    fillers.add("filler 1");
    GlobalStatisticRequestFilter mockRequestFilter =
        GlobalStatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    mockRequestFilter.setFillers(fillers);
    mockRequestFilter.setSearchByFiller("search");

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        RuntimeException exception =
            Assertions.assertThrows(
                FillerNotFoundException.class,
                () -> this.globalStatisticService.getGlobalProductionDetails(mockRequestFilter));
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(9)
  void getProductionProgressSuccess() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      GlobalStatisticRequestFilter mockRequestFilter =
          ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
      mockRequestFilter.setEndDate(new Date());

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        when(this.flowTraceabilityReportRepository.reportDocument(any()))
            .thenReturn(List.of(ConstantProperties.DOCUMENT_TOTAL_DTO));

        var result = this.globalStatisticService.getProductionProgress(mockRequestFilter);
        log.info("Result: {}", result);
        Assertions.assertNotNull(result, "Result must be not null");
      }
    }
  }

  @Test
  @Order(10)
  void getProductionProgress_ThenThrowUserAccessDeniedException() {

    GlobalStatisticRequestFilter mockRequestFilter = new GlobalStatisticRequestFilter();

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());

      RuntimeException exception =
          Assertions.assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.globalStatisticService.getProductionProgress(mockRequestFilter));

      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(11)
  void getProductionProgressSendingSubChannelNotFound() {

    List<String> categories = new ArrayList<>();
    categories.add("CSE");
    GlobalStatisticRequestFilter mockRequestFilter =
        GlobalStatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    mockRequestFilter.setCategories(categories);

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(new CriteriaDistributionsResponse());

        try (MockedStatic<org.apache.commons.lang3.StringUtils> validateUtil =
            mockStatic(org.apache.commons.lang3.StringUtils.class)) {

          validateUtil.when(() -> StringUtils.isEmpty(anyString())).thenReturn(false);

          validateUtil.when(() -> StringUtils.isEmpty(anyString())).thenReturn(true);

          RuntimeException exception =
              Assertions.assertThrows(
                  SubChannelNotFoundException.class,
                  () -> this.globalStatisticService.getProductionProgress(mockRequestFilter));
          Assertions.assertNotNull(exception, CATEGORY_NOT_SUPPORT);
          log.error("Exception :{}", exception.getMessage());
        }
      }
    }
  }
}
