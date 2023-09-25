package com.tessi.cxm.pfl.ms32.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class AbstractStaticServiceTest {

  private GlobalStatisticService globalStatisticService;
  @Mock
  private FlowTraceabilityReportRepository flowTraceabilityReportRepository;

  @Mock
  private SettingFeignClient settingFeignClient;

  @Mock
  private ProfileFeignClient profileFeignClient;

  @BeforeEach()
  void setUp() {
    this.globalStatisticService = new GlobalStatisticService(flowTraceabilityReportRepository,
        settingFeignClient, profileFeignClient);
  }

  @Test
  @Order(1)
  void fetchVolumeReceiveDetailsSuccess() {
    List<String> statuses = new ArrayList<>();
    statuses.add("In progress");
    statuses.add("Completed");
    statuses.add("In error");

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportDocument(any()))
        .thenReturn(List.of(ConstantProperties.DOCUMENT_TOTAL_DTO));

    var result = this.globalStatisticService.fetchVolumeReceiveDetails(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertNotNull(result, "Result must be not null");

  }

  @Test
  @Order(2)
  void fetchVolumeReceiveDetailsReturnSms() {
    List<String> statuses = new ArrayList<>();
    statuses.add("In progress");
    statuses.add("Completed");
    statuses.add("In error");

    List<String> categories = new ArrayList<>();
    categories.add("Sms");

    GlobalStatisticRequestFilter mockRequestFilter = GlobalStatisticRequestFilter.builder()
        .startDate(new Date())
        .endDate(new Date()).requestedAt(new Date()).build();
    mockRequestFilter.setStatuses(statuses);
    mockRequestFilter.setCategories(categories);

    when(this.flowTraceabilityReportRepository.reportDocument(any()))
        .thenReturn(ConstantProperties.LIST_DOCUMENT_TOTAL_DTO);

    var result = this.globalStatisticService.fetchVolumeReceiveDetails(mockRequestFilter);
    log.info("Result: {}", result.get("SMS"));
    Assertions.assertNotNull(result.get("SMS"), "Result must be not null");

  }

  @Test
  @Order(3)
  void fetchTotalInProgressSuccess() {
    List<String> statuses = new ArrayList<>();
    statuses.add("In progress");

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportDocument(any()))
        .thenReturn(List.of(ConstantProperties.DOCUMENT_TOTAL_DTO));

    var result = this.globalStatisticService.fetchTotalInProgress(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertNotNull(result, "Result must be not null");

  }

  @Test
  @Order(4)
  void fetchTotalInProgressReturnSmsAndEmail() {
    List<String> statuses = new ArrayList<>();
    statuses.add("In progress");

    List<String> categories = new ArrayList<>();
    categories.add("Sms");

    GlobalStatisticRequestFilter mockRequestFilter = GlobalStatisticRequestFilter.builder()
        .startDate(new Date())
        .endDate(new Date()).requestedAt(new Date()).build();
    mockRequestFilter.setStatuses(statuses);
    mockRequestFilter.setCategories(categories);

    when(this.flowTraceabilityReportRepository.reportDocument(any()))
        .thenReturn(ConstantProperties.LIST_DOCUMENT_TOTAL_DTO);

    var result = this.globalStatisticService.fetchTotalInProgress(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertNotNull(result.get("SMS"), "Result must be not null");
    Assertions.assertNotNull(result.get("Email"), "Result must be not null");

  }

  @Test
  @Order(5)
  void getTotalPNDSuccess() {
    List<String> statuses = this.getPNDStatuses();

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportProductionDetails(any()))
        .thenReturn(List.of(ConstantProperties.PRODUCTION_DETAIL_POSTAL));

    var result = this.globalStatisticService.getTotalPND(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertEquals(2L, result);
  }

  @Test
  @Order(6)
  void getTotalPNDSuccessValueMoreThanZero() {
    List<String> statuses = this.getPNDStatuses();

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportProductionDetails(any()))
        .thenReturn(List.of(ConstantProperties.PRODUCTION_DETAIL_POSTAL));

    var result = this.globalStatisticService.getTotalPND(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertTrue(result > 0L);

  }

  @Test
  @Order(7)
  void getTotalMNDSuccess() {
    List<String> statuses = this.getMNDStatuses();

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportProductionDetails(any()))
        .thenReturn(List.of(ConstantProperties.PRODUCTION_DETAIL_SMS));

    var result = this.globalStatisticService.getTotalMND(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertNotNull(result, "Result must be not null");

  }

  @Test
  @Order(8)
  void getTotalMNDReturnSMS() {
    List<String> statuses = this.getMNDStatuses();

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportProductionDetails(any()))
        .thenReturn(List.of(ConstantProperties.PRODUCTION_DETAIL_SMS));

    var result = this.globalStatisticService.getTotalMND(mockRequestFilter);
    log.info("Result: {}", result.get("SMS"));
    Assertions.assertNotNull(result.get("SMS"), "Result must be not null");

  }

  @Test
  @Order(9)
  void getReportDocumentProcessSuccess() {
    List<String> statuses = new ArrayList<>();
    statuses.add("Completed");
    statuses.add("In error");

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportDocument(any()))
        .thenReturn(ConstantProperties.LIST_DOCUMENT_TOTAL_DTO);

    var result = this.globalStatisticService.getReportDocumentProcess(mockRequestFilter);
    log.info("Result: {}", result);
    Assertions.assertNotNull(result, "Result must be not null");

  }

  @Test
  @Order(10)
  void getReportDocumentProcessSuccessReturnSms() {
    List<String> statuses = new ArrayList<>();
    statuses.add("Completed");
    statuses.add("In error");

    GlobalStatisticRequestFilter mockRequestFilter = ConstantProperties.BASE_FILTER_DOCUMENT_REPORT;
    mockRequestFilter.setStatuses(statuses);

    when(this.flowTraceabilityReportRepository.reportDocument(any()))
        .thenReturn(ConstantProperties.LIST_DOCUMENT_TOTAL_DTO);

    var result = this.globalStatisticService.getReportDocumentProcess(mockRequestFilter);
    log.info("Result {}: {}", result.get(0).getSubChannel(), result.get(0).getTotal());
    Assertions.assertNotNull(result.get(0).getTotal(), "Result must be not null");

  }

  private List<String> getPNDStatuses() {
    return FlowDocumentStatus.getPNDStatus().stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());
  }

  private List<String> getMNDStatuses() {
    return FlowDocumentStatus.getMNDStatus().stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());
  }
}
