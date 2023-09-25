package com.tessi.cxm.pfl.ms32.service;

import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.DOCUMENT_DETAIL_SUMMARY_PND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.service.specification.PostalStatisticService;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.ms32.utils.MockTuple;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
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
class PostalStatisticServiceTest {
  private PostalStatisticService postalStatisticService;
  @Mock private FlowTraceabilityReportRepository flowTraceabilityReportRepository;

  @Mock private SettingFeignClient settingFeignClient;

  @Mock private ProfileFeignClient profileFeignClient;

  @BeforeEach()
  void setUp() {
    this.postalStatisticService =
        new PostalStatisticService(
            flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  @Test
  @Order(1)
  void testCalculateProductionDeliveredSummary_ThenReturnSuccess() {
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
        when(this.flowTraceabilityReportRepository.reportProductionDetails(mockRequestFilter))
            .thenReturn(List.of(ConstantProperties.PRODUCTION_DETAIL_POSTAL));
        when(this.flowTraceabilityReportRepository.reportDocument(mockRequestFilter))
            .thenReturn(ConstantProperties.LIST_DOCUMENT_TOTAL_DTO);

        var result =
            this.postalStatisticService.calculateProductionDeliveredSummary(mockRequestFilter);

        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }

  @Test
  @Order(2)
  void testGetNonDistributionVolumeReceived_ThenReturnSuccess() {
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
                ArgumentMatchers.<Class<MockTuple>>any()))
            .thenReturn(DOCUMENT_DETAIL_SUMMARY_PND);

        var result =
            this.postalStatisticService.calculateNonDistributedDocumentDetailsSummary(
                mockRequestFilter);

        Assertions.assertNotNull(result, "Result must be not null");
        log.info("result: {}", result);
      }
    }
  }
}
