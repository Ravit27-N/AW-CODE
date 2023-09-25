package com.tessi.cxm.pfl.ms32.service;

import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.BASE_FILTER_DOCUMENT_REPORT_POSTAL;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.CATEGORIES;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.DOCUMENT_DETAIL_SUMMARY_PND;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.SHARED_CLIENT_FILLERS_DTOS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetailsFillers;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.exception.ChannelNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.DateInvalidException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.ms32.utils.MockTuple;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.TupleUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class StatisticServiceTest {

  private static final String CHANNEL_NOT_SUPPORT = "Channel not support:";
  private StatisticService statisticService;
  @Mock private FlowTraceabilityReportRepository flowTraceabilityReportRepository;

  @Mock private SettingFeignClient settingFeignClient;

  @Mock private ProfileFeignClient profileFeignClient;

  @BeforeEach()
  void setUp() {
    this.statisticService =
        new StatisticService(
            flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  @Test
  @Order(1)
  void testGetCalculateProductionDetailsSuccess() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
      mockRequestFilter.setChannels(List.of("Postal"));
      mockRequestFilter.setCategories(List.of("Lettre"));
      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        var result = this.statisticService.calculateProductionDetails(mockRequestFilter);
        log.info("Result: {}", result);
        Assertions.assertNotNull(result, "Result must be not null");
      }
    }
  }

  @Test
  @Order(2)
  void testGetCalculateProductionDetailChannelNotFound() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
      mockRequestFilter.setChannels(List.of("Postal1"));
      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        RuntimeException exception =
            Assertions.assertThrows(
                ChannelNotFoundException.class,
                () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
        Assertions.assertNotNull(exception, CHANNEL_NOT_SUPPORT);
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(3)
  void testGetCalculateProductionDetailCategoriesNotSupport() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
      mockRequestFilter.setChannels(List.of("Postal"));
      mockRequestFilter.setCategories(CATEGORIES);
      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
        Assertions.assertNotNull(exception, CHANNEL_NOT_SUPPORT);
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(4)
  void testGetCalculateProductionDetailThenThrowUserAccessDeniedException() {
    StatisticRequestFilter mockRequestFilter = new StatisticRequestFilter();
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
              () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(5)
  void testGetCalculateProductionDetailFailedWhenFillerNotOrder() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
      mockRequestFilter.setChannels(List.of("Postal"));
      mockRequestFilter.setCategories(List.of("Lettre"));
      mockRequestFilter.setSecondFillerKey("Filler2");
      mockRequestFilter.setSecondFillerKey("Filler3");
      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(6)
  void testGetCalculateProductionDetailFailedWhenFillersNotSupport() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
      mockRequestFilter.setChannels(List.of("Postal"));
      mockRequestFilter.setCategories(List.of("Lettre"));
      mockRequestFilter.setFillers(List.of("Filler1"));
      mockRequestFilter.setSecondFillerKey("Filler2");
      mockRequestFilter.setThirdFillerKey("Filler3");
      mockRequestFilter.setThirdFillerKey("Filler4");
      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(7)
  void testGetCalculateProductionDetailFailedWhenFillersDuplicate() {
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
        mockStatic(PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
      mockRequestFilter.setChannels(List.of("Postal"));
      mockRequestFilter.setCategories(List.of("Lettre"));
      mockRequestFilter.setFillers(List.of("Filler1"));
      mockRequestFilter.setSecondFillerKey("Filler1");
      try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
        utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
        CriteriaDistributionsResponse criteriaDistributionsResponse =
            new CriteriaDistributionsResponse();
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        RuntimeException exception =
            Assertions.assertThrows(
                BadRequestException.class,
                () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(8)
  void testGetCalculateProductionDetailDateInvalidException() {
    StatisticRequestFilter mockRequestFilter = BASE_FILTER_DOCUMENT_REPORT_POSTAL;
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
                () -> this.statisticService.calculateProductionDetails(mockRequestFilter));
        log.error("Exception :{}", exception.getMessage());
      }
    }
  }

  @Test
  @Order(9)
  void calculateTheNumberOfDocumentReceivedWithoutFillersThenReturnSuccess() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder()
            .channels(List.of("Postal"))
            .categories(List.of("Lettre"))
            .build();

    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-18 07:00:00"));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils.when(AuthenticationUtils::getAuthToken).thenReturn("token");

      CriteriaDistributionsResponse criteriaDistributionsResponse =
          new CriteriaDistributionsResponse();
      criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
      criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);

      when(this.flowTraceabilityReportRepository.findAll(
              ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.<Class<Tuple>>any()))
          .thenReturn(ConstantProperties.TUPLES);

      var result = this.statisticService.summaryDocsByFillerGrouping(mockRequestFilter);
      Assertions.assertNotNull(result, "Result must be not null");
      log.info("result: {}", result);
    }
  }

  @Test
  @Order(10)
  void calculateTheNumberOfDocumentReceivedWithFillersThenReturnSuccess() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder()
            .channels(List.of("Postal"))
            .categories(List.of("Lettre"))
            .build();

    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-16"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-16 07:00:00"));
      mockRequestFilter.setFillers(List.of("Filler1"));
      mockRequestFilter.setSecondFillerKey("Filler2");
      mockRequestFilter.setThirdFillerKey("Filler3");
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
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.flowTraceabilityReportRepository.findAll(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Tuple>>any()))
            .thenReturn(ConstantProperties.TUPLES);
        var result = this.statisticService.summaryDocsByFillerGrouping(mockRequestFilter);

        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  @Test
  @Order(11)
  void testCalculateDistributionVolumeReceive_ThenReturnSuccess() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder().channels(List.of("Postal")).build();

    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"));
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
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        when(this.flowTraceabilityReportRepository.findAll(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Tuple>>any()))
            .thenReturn(ConstantProperties.TUPLES);
        var result = this.statisticService.calculateDistributionVolumeReceive(mockRequestFilter);
        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  @Test
  @Order(12)
  void calculateTheNumberOfDocumentReceivedWithFillersAndMapPNDThenReturnSuccess() {
    StatisticRequestFilter mockRequestFilter =
        StatisticRequestFilter.builder()
            .channels(List.of("Postal"))
            .categories(List.of("Lettre"))
            .build();

    try {
      mockRequestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
      mockRequestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-16"));
      mockRequestFilter.setRequestedAt(
          new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-16 07:00:00"));
      mockRequestFilter.setFillers(List.of("Filler1"));
      mockRequestFilter.setSecondFillerKey("Filler2");
      mockRequestFilter.setThirdFillerKey("Filler3");
      mockRequestFilter.setIncludeMetadata(true);
      mockRequestFilter.setOwnerIds(List.of(1L));
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
        criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
        criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
        when(this.profileFeignClient.getAllClientFillers(anyString()))
            .thenReturn(SHARED_CLIENT_FILLERS_DTOS);
        when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
            .thenReturn(criteriaDistributionsResponse);
        List<ProductionDetailsFillers> productionDetailsFillers =
            DOCUMENT_DETAIL_SUMMARY_PND.stream().map(this::mapPndMnd).collect(Collectors.toList());
        lenient()
            .when(this.statisticService.fetchProdDetailsPndMnd(mockRequestFilter))
            .thenReturn(productionDetailsFillers);
        when(this.flowTraceabilityReportRepository.findAll(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Tuple>>any()))
            .thenReturn(ConstantProperties.TUPLES);
        var result = this.statisticService.calculateProductionDetails(mockRequestFilter);
        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  private ProductionDetailsFillers mapPndMnd(Tuple sourceTuple) {
    return ProductionDetailsFillers.builder()
        .total(TupleUtils.defaultIfNull(sourceTuple, 0, Long.class, 0L))
        .fillerGroup1(TupleUtils.defaultIfBlank(sourceTuple, 1, AnalyticsConstants.BLANK))
        .fillerGroup2(TupleUtils.defaultIfBlank(sourceTuple, 2, AnalyticsConstants.BLANK))
        .fillerGroup3(TupleUtils.defaultIfBlank(sourceTuple, 3, AnalyticsConstants.BLANK))
        .build();
  }
}
