package com.tessi.cxm.pfl.ms5.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.service.DivisionService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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

@WebMvcTest(
    value = DivisionController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {DivisionController.class, ProfileGlobalExceptionHandler.class})
@MockBeans({@MockBean(DivisionService.class)})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class DivisionControllerTest {

  private static final String URL = "/v1/divisions";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private DivisionService divisionService;

  @Test
  @Order(1)
  void testCreateDivision() throws Exception {
    when(divisionService.save(any(DivisionDto.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO);
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        ProfileUnitTestConstants.SAMPLE_DIVISION_DTO)))
            .andExpect(status().isCreated())
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testCreateDivisionClientNotFound() throws Exception {
    when(divisionService.save(any(DivisionDto.class))).thenThrow(new ClientNotFoundException(1));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        ProfileUnitTestConstants.SAMPLE_DIVISION_DTO)))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void testUpdateDivision() throws Exception {
    when(divisionService.update(any(DivisionDto.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO);
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        ProfileUnitTestConstants.SAMPLE_DIVISION_DTO)))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(4)
  void testUpdateDivisionWithClientNotFound() throws Exception {
    when(divisionService.update(any(DivisionDto.class)))
        .thenThrow(new ClientNotFoundException(1));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        ProfileUnitTestConstants.SAMPLE_DIVISION_DTO)))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  void testUpdateDivisionNotFound() throws Exception {
    when(divisionService.update(any(DivisionDto.class)))
        .thenThrow(new DivisionNotFoundException(1));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                        ProfileUnitTestConstants.SAMPLE_DIVISION_DTO)))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(6)
  void testDeleteDivision() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void testDeleteDivisionNotFound() throws Exception {
    doThrow(new DivisionNotFoundException(1)).when(divisionService).delete(1L);
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(8)
  void testGetAllDivision() throws Exception {
    when(divisionService.findAll()).thenReturn(
        List.of(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO));
    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(9)
  void successOnCheckDivisionIsDuplicate() throws Exception {
    when(divisionService.validateDuplicateName(anyLong(), anyLong(), anyString()))
        .thenReturn(true);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate/{clientId}", 1)
                .param("name", "Division1")// Division name
                .param("id", "1") // Division id
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value("true"))
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(10)
  void successOnCheckDivisionIsNotDuplicate() throws Exception {
    when(divisionService.validateDuplicateName(anyLong(), anyLong(), anyString()))
        .thenReturn(false);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate/{clientId}", 1)
                .param("name", "Division1")// Division name
                .param("id", "1") // Division id
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value("false"))
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(11)
  void failOnCheckDivisionIsDuplicate() throws Exception {
    final var divisionId = 1L;
    var divisionNotFoundException = new DivisionNotFoundException(divisionId);
    when(divisionService.validateDuplicateName(eq(divisionId), anyLong(), anyString()))
        .thenThrow(divisionNotFoundException);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate/{clientId}", 1)
                .param("name", "Division1")// Division name
                .param("id", String.valueOf(divisionId)) // Division id
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(
                jsonPath("$.apierrorhandler.message").value(divisionNotFoundException.getMessage()))
            .andReturn();
    log.info("Response: {}", result.getResponse().getContentAsString());
  }
}
