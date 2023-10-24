package com.innovationandtrust.corporate.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.corporate.model.dto.BusinessUnitDto;
import com.innovationandtrust.corporate.service.BusinessUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class BusinessUnitControllerTests {
  @Mock BusinessUnitController controller;
  @Mock BusinessUnitService service;
  @Mock
  BusinessUnitDto dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Find all business unit api test")
  void find_all_business_unit_api_test() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    service.findAll(paging, "", 1L);

    mockMvc
        .perform(
            get("/v1/business-units")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortByField", "id")
                .param("sortDirection", "desc")
                .param("filter", "")
                .param("companyId", "1"))
        .andExpect(status().isOk());
    // then
    verify(service, times(1)).findAll(paging, "", 1L);
  }

  @Test
  @DisplayName("Save business unit api test")
  void save_business_unit_api_test() throws Exception {
    service.save(dto);
    mockMvc
        .perform(
            post("/v1/business-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"companyId\":\"1\",\"parentId\":\"1\",\"unitName\":\"IT\",\"sortOrder\":\"1\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).save(dto);
  }

  @Test
  @DisplayName("Find business unit by id api test")
  void find_business_unit_by_id_api_test() throws Exception {
    service.findById(1L);
    mockMvc
        .perform(get("/v1/business-units/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Update business unit api test")
  void update_business_unit_api_test() throws Exception {
    service.update(dto);
    mockMvc
        .perform(
            post("/v1/business-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":\"1\",\"companyId\":\"1\",\"parentId\":\"1\",\"unitName\":\"IT\",\"sortOrder\":\"1\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).update(dto);
  }
}
