package com.tessi.cxm.pfl.ms3.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.constant.FlowValidationConstant;
import com.tessi.cxm.pfl.ms3.dto.ApprovalRequest;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentValidationRequest;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityValidationService;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(FlowValidationController.class)
@ContextConfiguration(classes = {FlowValidationController.class, InternalConfig.class})
@Slf4j
class FlowValidationControllerTest {
  private static final String URL = "/v1/validation";

  @MockBean private FlowTraceabilityValidationService flowTraceabilityValidationService;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  private ListFlowTraceabilityDto listFlowTraceabilityDto;
  private final FlowDocumentDto flowDocumentDto = ConstantProperties.FLOW_DOCUMENT_DTO;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new FlowValidationController(this.flowTraceabilityValidationService))
            .setControllerAdvice(FlowTraceabilityGlobalExceptionHandler.class)
            .build();
    listFlowTraceabilityDto = ConstantProperties.LIST_FLOW_TRACEABILITY_DTO;
  }

  @Test
  void testFindAllFlowValidation() throws Exception {

    Page<ListFlowTraceabilityDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.listFlowTraceabilityDto));
    given(
            this.flowTraceabilityValidationService.getFlowValidationList(
                any(Pageable.class), any(FlowFilterCriteria.class)))
        .willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/flow/{page}/{pageSize}", 1, 10)
                    .param("sortByField", "createdAt")
                    .param("sortDirection", "DESC")
                    .param("filter", "")
                    .param("channels", "")
                    .param("categories", "")
                    .param("users", "")
                    .param("startDate", "")
                    .param("endDate", ""))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  void testRefuseFlow() throws Exception {

    Page<ListFlowTraceabilityDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.listFlowTraceabilityDto));
    given(
            this.flowTraceabilityValidationService.getFlowValidationList(
                any(Pageable.class), any(FlowFilterCriteria.class)))
        .willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL + "/flow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            ApprovalRequest.builder()
                                .status("Refuse document")
                                .fileIds(List.of())
                                .build())))
            .andExpect(status().isOk());
    log.info("Result => {}", result);
  }

  @Test
  void testValidateFlow() throws Exception {

    Page<ListFlowTraceabilityDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.listFlowTraceabilityDto));
    given(
            this.flowTraceabilityValidationService.getFlowValidationList(
                any(Pageable.class), any(FlowFilterCriteria.class)))
        .willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL + "/flow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            ApprovalRequest.builder()
                                .status("validated")
                                .fileIds(List.of())
                                .build())))
            .andExpect(status().isOk());
    log.info("Result => {}", result);
  }


  @Test
  @Order(11)
  void testFindAllFlowDocumentValidation() throws Exception {
    Page<FlowDocumentDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.flowDocumentDto));
    given(
        this.flowTraceabilityValidationService.getFlowDocumentValidationList(
            anyString(), anyLong(), any(Pageable.class)))
        .willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/document")
                    .param("page", "0")
                    .param("pageSize", "0")
                    .param("flowId", "0")
                    .param("filter", "")
                    .param("sortByField", "createdAt")
                    .param("sortDirection", "desc"))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(12)
  void testValidateFlowDocument() throws Exception {

    final var request =
        FlowDocumentValidationRequest.builder()
            .flowId(1L)
            .action(FlowValidationConstant.VALIDATE)
            .documentIds(List.of())
            .build();

    doNothing().when(flowTraceabilityValidationService).validateFlowDocument(request);

    final var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL + "/document")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    verify(this.flowTraceabilityValidationService)
        .validateFlowDocument(any(FlowDocumentValidationRequest.class));
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(12)
  void testRefuseFlowDocument() throws Exception {

    final var request =
        FlowDocumentValidationRequest.builder()
            .flowId(1L)
            .action(FlowValidationConstant.REFUSE)
            .documentIds(List.of())
            .build();

    doNothing().when(flowTraceabilityValidationService).validateFlowDocument(request);

    final var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL + "/document")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    verify(this.flowTraceabilityValidationService)
        .validateFlowDocument(any(FlowDocumentValidationRequest.class));
    log.info("Result => {}", result.getResponse().getContentAsString());
  }
}
