package com.tessi.cxm.pfl.ms32.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tessi.cxm.pfl.ms32.service.FlowTraceabilityReportService;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(FlowTraceabilityReportController.class)
@TestPropertySource("classpath:bootstrap.yml")
@ContextConfiguration(
    classes = {
        ServiceDiscoveryAutoConfigurationImportSelector.class,
        ServiceDiscoveryBootstrapConfiguration.class
    })
@Slf4j
class FlowTraceabilityReportControllerTest {

  private static final String URL = "/v1/flow-traceability/report";
  private static final String REQUESTED_AT = "2023-04-01 00:11:00";
  @MockBean
  private FlowTraceabilityReportService flowTraceabilityReportService;
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(
            new FlowTraceabilityReportController(flowTraceabilityReportService))
        .setControllerAdvice(AnalyticGlobalExceptionController.class).build();
  }

  @Test
  @Order(1)
  void testGetDepositModes_ThenReturnSuccess() throws Exception {
    when(this.flowTraceabilityReportService.getDepositModes(anyString(), any(Date.class)))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_RESPONSE_DTO));

    var result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(URL + "/deposit-modes")
                .param("requestedAt", REQUESTED_AT)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testGetDepositModes_ThenReturnNotFound() throws Exception {
    when(this.flowTraceabilityReportService.getDepositModes(anyString(), any(Date.class)))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "deposit-modes")
                .param("requestedAt", REQUESTED_AT)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(3)
  void getGetFlowDocuments_ThenReturnSuccess() throws Exception {
    when(this.flowTraceabilityReportService.getFlowDocumentReport(anyString(), any(Date.class)))
        .thenReturn(ConstantProperties.FLOW_DOCUMENT_REPORT_DTO);

    var result = this.mockMvc.perform(
            MockMvcRequestBuilders.get(URL + "/flow-documents")
                .contentType(MediaType.APPLICATION_JSON)
                .param("requestedAt", REQUESTED_AT)
        ).andExpect(status().isOk())
        .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(4)
  void getGetFlowDocuments_ThenReturnNotFound() throws Exception {
    when(this.flowTraceabilityReportService.getFlowDocumentReport(anyString(), any(Date.class)))
        .thenReturn(ConstantProperties.FLOW_DOCUMENT_REPORT_DTO);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "flow-documents")
                .contentType(MediaType.APPLICATION_JSON)
                .param("requestedAt", REQUESTED_AT))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(5)
  void testGetFlowChannel_ThenReturnSuccess() throws Exception {
    when(this.flowTraceabilityReportService.getByGroupSubChannel(anyString(), any(Date.class)))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_RESPONSE_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/flow-channel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("requestedAt", REQUESTED_AT))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(6)
  void testGetFlowChannel_ThenReturnNotFound() throws Exception {
    when(this.flowTraceabilityReportService.getByGroupSubChannel(anyString(), any(Date.class)))
        .thenReturn(List.of(ConstantProperties.DEPOSIT_MODE_RESPONSE_DTO));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "flow-channel")
                .contentType(MediaType.APPLICATION_JSON)
                .param("requestedAt", REQUESTED_AT))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(7)
  void testGetEvolutionOfVolumesByChannel_ThenReturnSuccess() throws Exception {
    when(this.flowTraceabilityReportService.getFlowDocumentEvolutionReport(anyString(), any(Date.class)))
        .thenReturn(List.of(ConstantProperties.FLOW_DOCUMENT_EVOLUTION_REPORT_DATA));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/evolution")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("requestedAt", REQUESTED_AT))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(8)
  void testGetEvolutionOfVolumesByChannel_ThenReturnNotFound() throws Exception {
    when(this.flowTraceabilityReportService.getFlowDocumentEvolutionReport(anyString(), any(Date.class)))
        .thenReturn(List.of(ConstantProperties.FLOW_DOCUMENT_EVOLUTION_REPORT_DATA));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "evolution")
                .contentType(MediaType.APPLICATION_JSON)
                .param("requestedAt", REQUESTED_AT))
        .andExpect(status().isNotFound());
  }
}
