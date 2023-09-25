package com.tessi.cxm.pfl.ms3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDto;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.service.ElementAssociationService;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = ElementAssociationController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {
      ElementAssociationController.class,
      FlowTraceabilityGlobalExceptionHandler.class,
      InternalConfig.class
    })
@MockBeans({@MockBean(ElementAssociationService.class)})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class ElementAssociationControllerTest {
  private static final String URL = "/v1/element-association";
  private final ElementAssociationDto elementAssociationDto =
      ConstantProperties.ELEMENT_ASSOCIATION_DTO;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;
  @MockBean private ElementAssociationService service;

  @Test
  void testSaveElementAssociation() throws Exception {
    given(service.save(any(ElementAssociationDto.class))).willReturn(elementAssociationDto);
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(elementAssociationDto)))
            .andExpect(status().isCreated())
            .andReturn();
    log.info("Response body :{}", result.getResponse().getContentAsString());
  }

  @Test
  void testSaveElementAssociationFail() throws Exception {
    willThrow(new FlowDocumentNotFoundException(1))
        .given(service)
        .save(any(ElementAssociationDto.class));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(elementAssociationDto)))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("Response body :{}", result.getResponse().getContentAsString());
  }

  @Test
  void testGetListElementAssociation() throws Exception {
    given(this.service.findAll(anyLong()))
        .willReturn(Collections.singletonList(this.elementAssociationDto));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/{documentId}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response body :{}", result.getResponse().getContentAsString());
  }
}
