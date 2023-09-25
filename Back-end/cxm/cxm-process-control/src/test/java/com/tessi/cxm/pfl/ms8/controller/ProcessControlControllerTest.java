package com.tessi.cxm.pfl.ms8.controller;

import static com.tessi.cxm.pfl.ms8.constant.ProcessControlConstant.MOCK_CLIENT_UNLOAD_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ConstantProperties;
import com.tessi.cxm.pfl.ms8.service.FlowUnloadingService;
import com.tessi.cxm.pfl.ms8.service.ProcessControlService;
import com.tessi.cxm.pfl.ms8.service.UnloadingSchedulerService;
import com.tessi.cxm.pfl.ms8.service.manual.ProcessControlServiceManual;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(ProcessControlController.class)
@TestPropertySource("classpath:bootstrap.yml")
@ContextConfiguration(
    classes = {
      ServiceDiscoveryAutoConfigurationImportSelector.class,
      ServiceDiscoveryBootstrapConfiguration.class
    })
@Slf4j
class ProcessControlControllerTest {

  private static final String URL = "/v1/process-control";
  private static final String CREATED_BY = "dev@gmail.com";

  @MockBean private ProcessControlService processControlService;
  @MockBean private ProcessControlServiceManual processControlServiceManual;
  @MockBean private UnloadingSchedulerService flowUnloadingService;
  @MockBean private FlowUnloadingService forceClientUnloading;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new ProcessControlController(
                    processControlService, processControlServiceManual, flowUnloadingService,
                    forceClientUnloading))
            .setControllerAdvice(ProcessControlGlobalExceptionHandler.class)
            .build();
  }

  @Test
  void givenExecutionContext_thenLaunchProcess() throws Exception {
    var mockRequest =
        DepositedFlowLaunchRequest.builder()
            .depositDate(new Date())
            .flowType("ENI/Batch/C1/zip")
            .connector("C1")
            .customer("ENI")
            .depositType("Batch")
            .extension("zip")
            .idCreator(1L)
            .serviceId("cxm-test")
            .fileId("FILE_ID")
            .build();

    doNothing().when(this.processControlService).launch(any());

    var result =
        this.mockMvc
            .perform(
                post(URL + "/launch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockRequest)))
            .andExpect(status().isOk())
            /*            .andExpect(jsonPath("$.['message']").value("Launched"))
            .andExpect(jsonPath("$.['status']").value(200))*/
            .andReturn()
            .getResponse();

    log.info("actual response: {}", result.getContentAsString());
  }

  @Test
  void givenClientUnloadSchedule_thenSucceed() throws Exception {
    doNothing().when(this.flowUnloadingService).scheduleFlowUnloading(any());

    this.mockMvc
        .perform(
            post(URL + "/set-client-unload-schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(MOCK_CLIENT_UNLOAD_DETAILS)))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  @Test
  @Order(2)
  void testSendFlow_thenReturnSuccess() throws Exception {
    when(this.processControlService.sendFlow(anyString(), anyString(), anyBoolean(), anyBoolean()))
        .thenReturn(ConstantProperties.SWITCH_FLOW_RESPONSE);

    var result =
        this.mockMvc
            .perform(
                get(URL + "/send-flow")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("uuid", "8cd0e247-ceef-42b8-9a40-570e20c83d76")
                    .param("validation", String.valueOf(false))
                    .param("isModify", String.valueOf(false))
                    .param("composedFileId", "41bb5a9b-62ac-461e-8175-82236a7f36bc"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    log.info("Response: {}", result.getContentAsString());
  }

  @Test
  @Order(3)
  void testSendFlow_thenReturnFail() throws Exception {
    var result =
        this.mockMvc
            .perform(get(URL + "/send-flow/test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

    log.info("Response: {}", result.getContentAsString());
  }

  @Test
  @Order(4)
  void testCancelFlow_thenSuccess() throws Exception {
    doNothing()
        .when(this.processControlService)
        .cancelFlow(anyString());

    this.mockMvc
        .perform(
            put(URL + "/cancel/{uuid}", "e1c63607-cf91-42fd-b4aa-9d3d50adab73")
                .param("createdBy", CREATED_BY))
        .andExpect(status().isOk());
  }

  @Test
  @Order(5)
  void testCancelFlow_thenFail() throws Exception {
    doNothing()
        .when(this.processControlService)
        .cancelFlow(anyString());

    this.mockMvc
        .perform(put(URL + "/cancel").param("createdBy", CREATED_BY))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(6)
  void testForceUnloadFlow_thenSuccess() throws Exception {
    doNothing()
        .when(this.forceClientUnloading)
        .forceUnloadFlow(anyLong(), any(Date.class));

    this.mockMvc
        .perform(
            patch(URL + "/flow-unloading/{clientId}/force-schedule", "1"))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void testForceUnloadFlow_thenFail() throws Exception {
    this.mockMvc
        .perform(
            patch(URL + "/flow-unloading/{clientId}/force-schedule", "0"))
        .andExpect(status().isBadRequest());
  }
}
