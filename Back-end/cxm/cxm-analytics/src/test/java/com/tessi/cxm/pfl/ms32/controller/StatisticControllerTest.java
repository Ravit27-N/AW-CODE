package com.tessi.cxm.pfl.ms32.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tessi.cxm.pfl.ms32.dto.StatisticExportingRequestFilter;
import com.tessi.cxm.pfl.ms32.service.DigitalStatisticService;
import com.tessi.cxm.pfl.ms32.service.GlobalStatisticService;
import com.tessi.cxm.pfl.ms32.service.StatisticExportingService;
import com.tessi.cxm.pfl.ms32.service.StatisticService;
import com.tessi.cxm.pfl.ms32.service.specification.PostalStatisticService;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(StatisticController.class)
@TestPropertySource("classpath:bootstrap.yml")
@ContextConfiguration(
    classes = {
      ServiceDiscoveryAutoConfigurationImportSelector.class,
      ServiceDiscoveryBootstrapConfiguration.class
    })
@Slf4j
class StatisticControllerTest {

  private static final String GLOBAL = "/v1/statistic/global";
  private static final String VOLUME_RECEIVED = "volume-received";
  private static final String PRODUCTION_DETAILS = "production-details";
  private static final String PRODUCTION_PROGRESS = "production-progress";
  private static final String STATISTIC_URL = "/v1/statistic";
  private static final String NON_DISTRIBUTION_BY_STATUS = "/postal/none-distribution-by-status";
  private static final String DISTRIBUTION_VOLUME_RECEIVED = "distribution-volume-received";
  private static final String PRODUCTION_DELIVERED = "/production-delivered";
  private static final String NON_DISTRIBUTION_VOLUME_RECEIVED = "/digital/distribution-by-status";

  @Mock private GlobalStatisticService globalStatisticService;
  @Mock private StatisticService statisticService;
  @Mock private PostalStatisticService postalStatisticService;
  @Mock private DigitalStatisticService digitalStatisticService;
  @Mock private StatisticExportingService statisticExportingService;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new StatisticController(
                    globalStatisticService,
                    statisticService,
                    postalStatisticService,
                    digitalStatisticService,
                    statisticExportingService))
            .setControllerAdvice(AnalyticGlobalExceptionController.class)
            .build();
  }

  @Test
  @Order(1)
  void testGetVolumeReceived_ReturnSuccess() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(GLOBAL + "/" + VOLUME_RECEIVED)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "")
                    .param("categories", "")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testGetVolumeReceived_ReturnNotFound() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(GLOBAL + VOLUME_RECEIVED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04 00:00:00")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  @Order(3)
  void testGetVolumeReceived_ReturnBadRequest() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(GLOBAL + "/" + VOLUME_RECEIVED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  @Order(4)
  void testGetGlobalProductionDetails_ReturnSuccess() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(GLOBAL + "/" + PRODUCTION_DETAILS)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "")
                    .param("categories", "")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  void testGetGlobalProductionDetails_ReturnNotFound() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(GLOBAL + PRODUCTION_DETAILS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04 00:00:00")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  @Order(6)
  void testGetGlobalProductionDetails_ReturnBadRequest() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(GLOBAL + "/" + PRODUCTION_DETAILS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  @Order(7)
  void testGetProductionProgress_ReturnSuccess() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(GLOBAL + "/" + PRODUCTION_PROGRESS)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "")
                    .param("categories", "")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(8)
  void testGetProductionProgress_ReturnNotFound() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(GLOBAL + PRODUCTION_PROGRESS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04 00:00:00")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  @Order(9)
  void testGetProductionProgress_ReturnBadRequest() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(GLOBAL + "/" + PRODUCTION_PROGRESS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  @Order(10)
  void testGetProductionDetail_ReturnSuccess() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + "/" + PRODUCTION_DETAILS)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "Postal")
                    .param("categories", "")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(11)
  void testGetProductionDetail_ReturnNotFound() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(STATISTIC_URL + PRODUCTION_DETAILS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "Postal")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04 00:00:00")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isNotFound())
        .andReturn();
  }

  @Test
  @Order(12)
  void testGetProductionDetail_ReturnBadRequest() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(STATISTIC_URL + "/" + PRODUCTION_DETAILS)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "Postal")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  @Order(13)
  void testGetDistributionVolumeReceived_ReturnSuccess() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + "/" + DISTRIBUTION_VOLUME_RECEIVED)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "Postal")
                    .param("categories", "")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(14)
  void testGetDistributionVolumeReceived_ReturnBadRequest() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(STATISTIC_URL + "/" + DISTRIBUTION_VOLUME_RECEIVED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "Postal")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  @Order(15)
  void testGetProductionDeliveredSummary_ReturnSuccess() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + PRODUCTION_DELIVERED)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "Postal")
                    .param("categories", "")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(16)
  void testGetProductionDeliveredSummary_ReturnBadRequest() throws Exception {
    when(this.globalStatisticService.getVolumeReceived(any()))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_VOLUME_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(STATISTIC_URL + PRODUCTION_DELIVERED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("channels", "Postal")
                .param("categories", "")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-04")
                .param("requestedAt", "2023-04-04")
                .param("fillers", "")
                .param("searchByFiller", "M"))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  @Order(17)
  void testGetNonDistributionVolumeReceived_ThenReturnSuccess() throws Exception {
    when(this.postalStatisticService.calculateNonDistributedDocumentDetailsSummary(any()))
        .thenReturn(ConstantProperties.NON_DISTRIBUTED_VOLUME_RECEIVED);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + "/" + NON_DISTRIBUTION_BY_STATUS)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "Postal")
                    .param("categories", "Lettre")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M")
                    .param("secondFillerText", "")
                    .param("secondFillerKey", "")
                    .param("thirdFillerText", "")
                    .param("thirdFillerKey", ""))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(18)
  void testGetNonDistributionVolumeReceived_ThenReturnNotFound() throws Exception {
    when(this.postalStatisticService.calculateNonDistributedDocumentDetailsSummary(any()))
        .thenReturn(ConstantProperties.NON_DISTRIBUTED_VOLUME_RECEIVED);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + NON_DISTRIBUTION_BY_STATUS)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "channels")
                    .param("categories", "Lettre")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("fillers", "")
                    .param("searchByFiller", "M")
                    .param("secondFillerText", "")
                    .param("secondFillerKey", "")
                    .param("thirdFillerText", "")
                    .param("thirdFillerKey", ""))
            .andExpect(status().isBadRequest())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(19)
  void testDistributionByStatus_ThenReturnSuccess() throws Exception {
    when(this.postalStatisticService.calculateNonDistributedDocumentDetailsSummary(any()))
        .thenReturn(ConstantProperties.NON_DISTRIBUTED_VOLUME_RECEIVED);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + "/" + NON_DISTRIBUTION_VOLUME_RECEIVED)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "Digital")
                    .param("categories", "Email")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("requestedAt", "2023-04-04 00:00:00")
                    .param("fillers", "")
                    .param("searchByFiller", "M")
                    .param("secondFillerText", "")
                    .param("secondFillerKey", "")
                    .param("thirdFillerText", "")
                    .param("thirdFillerKey", ""))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(20)
  void testDistributionByStatus_ThenReturnNotFound() throws Exception {
    when(this.postalStatisticService.calculateNonDistributedDocumentDetailsSummary(any()))
        .thenReturn(ConstantProperties.NON_DISTRIBUTED_VOLUME_RECEIVED);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(STATISTIC_URL + NON_DISTRIBUTION_VOLUME_RECEIVED)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("channels", "channels")
                    .param("categories", "Email")
                    .param("startDate", "2023-04-04")
                    .param("endDate", "2023-04-04")
                    .param("fillers", "")
                    .param("searchByFiller", "M")
                    .param("secondFillerText", "")
                    .param("secondFillerKey", "")
                    .param("thirdFillerText", "")
                    .param("thirdFillerKey", ""))
            .andExpect(status().isBadRequest())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(21)
  void testGenerateAndExport_Success() throws Exception {
    Path exportedFile = Files.createTempFile("mocked-file", ".csv");
    when(statisticExportingService.generateAndExport(any(StatisticExportingRequestFilter.class)))
        .thenReturn(exportedFile);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(STATISTIC_URL + "/export")
                .contentType(MediaType.APPLICATION_JSON)
                .param("exportingType", "global")
                .param("channels", "Postal", "Digital")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-05")
                .param("requestedAt", "2023-04-05 07:00:00"))
        .andExpect(status().isOk());
  }

  @Test
  @Order(22)
  void testGenerateAndExport_FileNotFound() throws Exception {
    when(statisticExportingService.generateAndExport(any(StatisticExportingRequestFilter.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(STATISTIC_URL + "/export")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .param("exportingType", "invalid")
                .param("channels", "Postal", "Digital")
                .param("startDate", "2023-04-04")
                .param("endDate", "2023-04-05")
                .param("requestedAt", "2023-04-05 07:00:00"))
        .andExpect(status().isNotFound());
  }
}
