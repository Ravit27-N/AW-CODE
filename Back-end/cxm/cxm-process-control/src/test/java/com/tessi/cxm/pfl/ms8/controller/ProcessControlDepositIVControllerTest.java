package com.tessi.cxm.pfl.ms8.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.config.InternalConfig;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualProcessFlowValidation;
import com.tessi.cxm.pfl.ms8.service.ProcessControlService;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = ProcessControlDepositIVController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@TestPropertySource("classpath:bootstrap.yml")
@ContextConfiguration(
    classes = {
      InternalConfig.class,
      ProcessControlDepositIVController.class,
      ProcessControlGlobalExceptionHandler.class,
      ServiceDiscoveryAutoConfigurationImportSelector.class,
      ServiceDiscoveryBootstrapConfiguration.class,
    })
@Slf4j
class ProcessControlDepositIVControllerTest {
  private static final String URL = "/v1/process-control/iv";

  @MockBean private ProcessControlService processControlService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @Order(1)
  void testSuccessDepositIV() throws Exception {
    // Setup
    String url = URL + "/identify-flow";
    DepositedFlowLaunchRequest portalDepositIVRequest = new DepositedFlowLaunchRequest();
    final var refBearerToken = UUID.randomUUID().toString();
    // Stub
    when(this.processControlService.identifyDepositedFlow(portalDepositIVRequest, refBearerToken))
        .thenReturn(new ProcessCtrlIdentificationResponse());
    // Call and Expect
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(portalDepositIVRequest)))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result :{}", result.getResponse().getContentAsString());
  }
}
