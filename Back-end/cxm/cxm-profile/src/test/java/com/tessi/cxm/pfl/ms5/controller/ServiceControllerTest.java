package com.tessi.cxm.pfl.ms5.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.service.DepartmentService;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.exception.DepartmentNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
    value = ServiceController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {ServiceController.class, ProfileGlobalExceptionHandler.class})
@MockBeans({@MockBean(DepartmentService.class)})
@Slf4j
class ServiceControllerTest {

  private static final String URL = "/v1/services";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private DepartmentService departmentService;

  @Test
  @Order(1)
  void testCreateDepartment() throws Exception {
    when(departmentService.save(any(DepartmentDto.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO);
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO)))
            .andExpect(status().isCreated())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testCreateDepartmentWithDivisionNotFound() throws Exception {
    when(departmentService.save(any(DepartmentDto.class)))
        .thenThrow(new DivisionNotFoundException(1));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO)))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("ApiError :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testUpdateDepartment() throws Exception {
    when(departmentService.update(any(DepartmentDto.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO);
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO)))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void testUpdateDepartmentNotFound() throws Exception {
    when(departmentService.update(any(DepartmentDto.class)))
        .thenThrow(new DepartmentNotFoundException(1));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO)))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("ApiError :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(4)
  void testGetALl() throws Exception {
    when(departmentService.findAll()).thenReturn(
        List.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO));
    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  void testDelete() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
        .andExpect(status().isOk());
  }

  @Test
  @Order(6)
  void testDeleteNotFound() throws Exception {
    doThrow(new DepartmentNotFoundException(1)).when(departmentService).delete(anyLong());
    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("ApiError :{}", result.getResponse().getContentAsString());
  }
}
