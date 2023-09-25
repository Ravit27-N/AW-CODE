package com.tessi.cxm.pfl.ms32.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.exception.DateInvalidException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
class DigitalStatisticServiceTest {

  private DigitalStatisticService digitalStatisticService;
  @Mock private FlowTraceabilityReportRepository flowTraceabilityReportRepository;
  @Mock private SettingFeignClient settingFeignClient;
  @Mock private ProfileFeignClient profileFeignClient;

  @BeforeEach()
  void setUp() {
    this.digitalStatisticService =
        new DigitalStatisticService(
            flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  @Test
  @Order(1)
  void testGetDistributionByEmailSubStatus_ThenReturnSuccess() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("Email"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);
        criteriaDistributionsResponse.getCriteria("Email").setEnabled(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        var result = this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter);

        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  @Test
  @Order(2)
  void testGetDistributionByEmailSubStatus_ThenReturnFail_InvalidDate() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("Email"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);
        criteriaDistributionsResponse.getCriteria("Email").setEnabled(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        RuntimeException exception =
            Assertions.assertThrows(
                DateInvalidException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "endDate cannot be less than startDate");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(3)
  void testGetDistributionByEmailSubStatus_ThenReturnFail_DigitalCategoryDoesNotProvide() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("Email"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        mockRequestFilter.setCategories(Collections.emptyList());

        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "Digital category is not provided");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(4)
  void testGetDistributionByEmailSubStatus_ThenReturnFail_DigitalOnlyOneSubChannelIsSupport() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("Email"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        mockRequestFilter.setCategories(List.of("SMSs", "Emails"));

        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "Only one sub-channel is supported");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(5)
  void testGetDistributionByEmailSubStatus_ThenReturnFail_DigitalSubChannelDoesNotSupport() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("Email"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        mockRequestFilter.setCategories(List.of("Emails"));

        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "Either Email or SMS sub-channel is supported");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(6)
  void testGetDistributionBySmsSubStatus_ThenReturnSuccess() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("SMS"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);
        criteriaDistributionsResponse.getCriteria("Sms").setEnabled(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        var result = this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter);

        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  @Test
  @Order(7)
  void testGetDistributionBySmsSubStatus_ThenReturnFail() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("SMS"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);
        criteriaDistributionsResponse.getCriteria("Sms").setEnabled(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        var result = this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter);

        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  @Test
  @Order(8)
  void testGetDistributionBySmsSubStatus_ThenReturnFail_InvalidDate() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("SMS"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);
        criteriaDistributionsResponse.getCriteria("Sms").setEnabled(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        RuntimeException exception =
            Assertions.assertThrows(
                DateInvalidException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "endDate cannot be less than startDate");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(9)
  void testGetDistributionBySmsSubStatus_ThenReturnFail_DigitalCategoryDoesNotProvide() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("SMS"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        mockRequestFilter.setCategories(Collections.emptyList());

        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "Digital category is not provided");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(10)
  void testGetDistributionBySmsSubStatus_ThenReturnFail_DigitalOnlyOneSubChannelIsSupport() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("SMS"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        mockRequestFilter.setCategories(List.of("SMSs", "Emails"));

        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "Only one sub-channel is supported");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(11)
  void testGetDistributionBySmsSubStatus_ThenReturnFail_DigitalSubChannelDoesNotSupport() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Digital")).build();
    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setCategories(List.of("Sms"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
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

        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Digital").setActive(true);

        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);

        mockRequestFilter.setCategories(List.of("SMSs"));

        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.digitalStatisticService.getDistributionBySubStatus(mockRequestFilter));
        Assertions.assertNotNull(exception, "Either Email or SMS sub-channel is supported");
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }
}
