package com.tessi.cxm.pfl.ms3.controller;

import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_FLOW_DOCUMENT_FILLERS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.config.InternalConfig;

import com.tessi.cxm.pfl.ms3.dto.BaseFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentHistoryDto;
import com.tessi.cxm.pfl.ms3.dto.LoadFlowDocumentDetailsDto;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentDetailsNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentStatusNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingSubChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.service.FlowDocumentService;
import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityValidationService;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    value = FlowDocumentController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {
      FlowDocumentController.class,
      FlowTraceabilityGlobalExceptionHandler.class,
      InternalConfig.class
    })
@MockBeans({
  @MockBean(FlowDocumentService.class),
  @MockBean(FlowTraceabilityValidationService.class)
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class FlowDocumentControllerTest {

  private static final String URL = "/v1/flow-document";
  private final FlowDocumentDto flowDocumentDto = ConstantProperties.FLOW_DOCUMENT_DTO;
  private final LoadFlowDocumentDetailsDto flowDocumentDetailsDto =
      new LoadFlowDocumentDetailsDto(
          "",
          "",
          ConstantProperties.FLOW_DOCUMENT_DETAILS_DTO,
          Set.of(new FlowDocumentHistoryDto()),
          Set.of(new ElementAssociationDto()),
              new Date());
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;
  @MockBean private FlowDocumentService flowDocumentService;
  @MockBean private CSVPrinter csvPrinter;
  @InjectMocks private  FlowDocumentController flowDocumentController;

  @Test
  @Order(1)
  void givenRequestGetAllFlowDocuments_thenReturnSuccess() throws Exception {
    Page<FlowDocumentDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.flowDocumentDto));
    given(flowDocumentService.findAll(any(Pageable.class), any(BaseFilterCriteria.class)))
        .willReturn(mockResult);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "/{page}/{pageSize}", 1, 10)
                .param("sortByField", "createdAt")
                .param("sortDirection", "desc")
                .param("filter", "")
                .param("channels", "")
                .param("categories", "")
                .param("status", "")
                .param("startDate", "")
                .param("endDate", "")
                .param("fillers", "Filler1", "Filler2")
                .param("searchByFiller", "M")
                .accept("application/json")
                .content("application/json")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contents.*", hasSize(1)))
        .andExpect(jsonPath("$.contents[0].channel", is("Postal")))
        .andExpect(jsonPath("$.contents[0].status", is("In progress")))
        .andDo(print());
  }

  @Test
  @Order(2)
  void givenGetAllFlowDocumentsByFlowId_thenReturnSuccess() throws Exception {
    Page<FlowDocumentDto> mockResult =
        new PageImpl<>(Collections.singletonList(this.flowDocumentDto));
    given(
            flowDocumentService.findAllByFlowId(
                anyString(), anyLong(), any(Pageable.class), any(BaseFilterCriteria.class)))
        .willReturn(new EntityResponseHandler<>(mockResult, this.flowDocumentDto));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "/{flowTraceabilityId}/{page}/{pageSize}", 1, 1, 10)
                .header(HttpHeaders.AUTHORIZATION , "TOKEN")
                .param("sortByField", "createdAt")
                .param("sortDirection", "desc")
                .param("filter", "")
                .param("channels", "")
                .param("categories", "")
                .param("status", "")
                .param("startDate", "")
                .param("endDate", "")
                .param("fillers", "Filler1", "Filler2")
                .param("searchByFiller", "M")
                .accept("application/json")
                .content("application/json")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contents.*", hasSize(1)))
        .andExpect(jsonPath("$.contents[0].channel", is("Postal")))
        .andExpect(jsonPath("$.contents[0].status", is("In progress")))
        .andDo(print());
  }

  @Test
  @Order(3)
  void testSaveFlowDocument() throws Exception {
    given(flowDocumentService.save(flowDocumentDto)).willReturn(flowDocumentDto);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.flowDocumentDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(4)
  void testSaveFlowDocumentWithSendingChannelNotFound() throws Exception {
    willThrow(new SendingChannelNotFoundException()).given(this.flowDocumentService).save(any());

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(this.flowDocumentDto)))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  void testSaveFlowDocumentWithSendingSubChannelNotFound() throws Exception {
    willThrow(new SendingSubChannelNotFoundException()).given(this.flowDocumentService).save(any());

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(this.flowDocumentDto)))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(6)
  void testSaveFlowDocumentWithFlowDocumentStatusNotFound() throws Exception {
    willThrow(new FlowDocumentStatusNotFoundException())
        .given(this.flowDocumentService)
        .save(any());

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(this.flowDocumentDto)))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(9)
  void testGetDocumentDetails() throws Exception {
    flowDocumentDetailsDto.setElementAssociations(new HashSet<>());
    given(flowDocumentService.getFlowDocumentDetailsById(anyLong()))
        .willReturn(flowDocumentDetailsDto);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/details/{id}", 1))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(10)
  void testGetDocumentDetailsNotFound() throws Exception {
    willThrow(new FlowDocumentDetailsNotFoundException(1L))
        .given(this.flowDocumentService)
        .getFlowDocumentDetailsById(anyLong());

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/details/{id}", 1))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  void givenRequestGetAllDocumentFiller_thenReturnSuccess() throws Exception {
    given(flowDocumentService.getFlowDocumentFillers()).willReturn(MOCK_FLOW_DOCUMENT_FILLERS);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL + "/client-fillers")
                .accept("application/json")
                .content("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.[0].key", is("Filler1")))
        .andExpect(jsonPath("$.[4].key", is("Filler5")))
        .andDo(print());
  }



}





