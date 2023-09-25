package com.tessi.cxm.pfl.ms3.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.dto.DepositFlowInfoDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityService;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(FlowTraceabilityController.class)
@ContextConfiguration(classes = {FlowTraceabilityController.class, InternalConfig.class})
@Slf4j
class FlowTraceabilityControllerTest {

  private static final String URL = "/v1/flow-traceability";
  @MockBean private FlowTraceabilityService flowTraceabilityService;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private FlowTraceabilityDto flowTraceabilityDto;
  private ListFlowTraceabilityDto listFlowTraceabilityDto;

  @BeforeEach
  void setUp() {
    objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new FlowTraceabilityController(this.flowTraceabilityService))
            .setControllerAdvice(FlowTraceabilityGlobalExceptionHandler.class)
            .build();

    this.flowTraceabilityDto = ConstantProperties.flowTraceabilityDto;
    listFlowTraceabilityDto = ConstantProperties.LIST_FLOW_TRACEABILITY_DTO;
  }

  @Test
  void testFindAllFlowTraceability() throws Exception {

    Page<ListFlowTraceabilityDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.listFlowTraceabilityDto));
    given(
            this.flowTraceabilityService.findAll(any(Pageable.class), any(FlowFilterCriteria.class)))
        .willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/{page}/{pageSize}", 1, 10)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .param("sortDirection", "DESC")
                    .param("sortByField", "createdAt")
                    .param("filter", "")
                    .param("channel", "")
                    .param("subChannel", "")
                    .param("depositMode", "")
                    .param("status", "")
                    .param("createdBy", "")
                    .param("depositDateStart", "")
                    .param("depositDateEnd", "")
                    .param("statusDateStart", "")
                    .param("statusDateEnd", ""))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  void testUpdateStatusFlowTraceability() throws Exception {
    given(this.flowTraceabilityService.updateStatus(anyLong(), anyString(), anyString()))
        .willReturn(flowTraceabilityDto);
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.patch(
                    URL + "/status/{id}/{status}/{server}",
                    1L,
                    FlowTraceabilityStatus.DEPOSITED.getValue(),
                    "tessi"))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  void testUpdateStatusFlowTraceabilityNotFound() throws Exception {
    doThrow(new FlowTraceabilityNotFoundException("Status not found"))
        .when(this.flowTraceabilityService)
        .updateStatus(anyLong(), anyString(), anyString());

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.patch(
                        URL + "/status/{id}/{status}",
                        1,
                        FlowTraceabilityStatus.SCHEDULED.getValue())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(result.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    log.info("{}", result.getResponse().getContentAsString());
  }

  @Test
  void testGetDepositStep() throws Exception {
    given(this.flowTraceabilityService.getDepositFlowInfo(anyLong()))
        .willReturn(new DepositFlowInfoDto(1, UUID.randomUUID().toString()));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/deposit-info/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  void testUpdateDepositFlowInfo() throws Exception {
    final var composedId = UUID.randomUUID().toString();
    final int step = 4;
    given(
            this.flowTraceabilityService.updateDepositFlow(
                anyLong(), anyInt(), anyString(), any(Boolean.class)))
        .willReturn(new DepositFlowInfoDto(step, composedId));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.patch(
                        URL + "/deposit-info/{id}/{step}/{composedId}", 1, step, composedId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }
}
