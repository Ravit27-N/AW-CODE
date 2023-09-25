package com.tessi.cxm.pfl.ms11.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms11.config.InternalConfig;
import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms11.dto.ResourceParam;
import com.tessi.cxm.pfl.ms11.exception.ResourceLibraryNotFoundException;
import com.tessi.cxm.pfl.ms11.service.ResourceLibraryService;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    value = ResourceLibraryController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {
      ResourceLibraryController.class,
      SettingGlobalExceptionHandler.class,
      InternalConfig.class
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class ResourceLibraryControllerTest {
  @MockBean private ResourceLibraryService resourceLibraryService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  private static final String URL = "/v1/resources";

  @Test
  @Order(1)
  void testFindAllResources() throws Exception {
    Page<ResourceLibraryDto> mockResult =
        new PageImpl<>(Collections.singletonList(ConstantProperties.RESOURCE_LIBRARY_DTO));

    given(this.resourceLibraryService.findAll(any(ResourceParam.class)))
        .willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                get(URL)
                    .param("page", "1")
                    .param("pageSize", "10")
                    .param("sortDirection", "desc")
                    .param("sortByField", "createdAt")
                    .param("filter", "")
                    .param("types", "Background")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  @Test
  @Order(2)
  void testGetResourceByIdSuccess() throws Exception {
    when(resourceLibraryService.findById(anyLong()))
        .thenReturn(ConstantProperties.RESOURCE_LIBRARY_DTO);
    final MvcResult result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/{id}", 1))
            .andExpect(status().isOk())
            .andReturn();
    this.loggingActualResult(result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void testGetResourceByIdNotFound() throws Exception {
    when(resourceLibraryService.findById(anyLong()))
        .thenThrow(new ResourceLibraryNotFoundException(1L));

    final MvcResult result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/{id}", 1))
            .andExpect(status().isNotFound())
            .andReturn();

    this.loggingActualResult(result.getResponse().getContentAsString());
  }

  @Test
  @Order(4)
  void testCreateResource() throws Exception {
    given(resourceLibraryService.save(ConstantProperties.RESOURCE_LIBRARY_DTO))
        .willReturn(ConstantProperties.RESOURCE_LIBRARY_DTO);
    ResourceLibraryDto resourceLibraryDto =
        ResourceLibraryDto.builder().fileId("fileid").fileName("filename").type("type").build();

    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(resourceLibraryDto)))
            .andExpect(status().isOk())
            .andReturn();

    this.loggingActualResult(result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  void testDeleteResource() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
        .andExpect(status().isNoContent());
  }

  private void loggingActualResult(String result) {
    log.info("Actual result: {}.", result);
  }
}
