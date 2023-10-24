package com.innovationandtrust.profile.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class CompanyControllerTests {
  @InjectMocks CompanyController companyController;
  @Mock CompanyService companyService;
  @Mock
  CompanyDto dto;
  @Mock Page<CompanyDto> companyPage;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(companyController).build();
  }

  @DisplayName("Save company api test")
  @Test
  void save_company_api_test() throws Exception {
    companyService.save(dto);
    mockMvc
        .perform(
            post("/v1/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"HERMAN\",\"siret\":\"123\"}"))
        .andExpect(status().isOk());

    // then
    verify(companyService, times(1)).save(dto);
  }

  @DisplayName("Update company api test")
  @Test
  void update_company_api_test() throws Exception {
    companyService.update(dto);
    mockMvc
        .perform(
            put("/v1/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"HERMAN\",\"siret\":\"123\"}"))
        .andExpect(status().isOk());

    // then
    verify(companyService, times(1)).update(dto);
  }

  @DisplayName("Find company by id")
  @Test
  void find_company_by_id() throws Exception {
    companyService.findById(1L);
    mockMvc
        .perform(get("/v1/companies/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(companyService, times(2)).findById(1L);
  }

  @DisplayName("Find all companies")
  @Test
  void find_all_companies() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    companyService.findAll(paging, "");

    // when
    when(companyService.findAll(paging, "")).thenReturn(companyPage);

    mockMvc
        .perform(
            get("/v1/companies")
                .param("pageNum", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortField", "id")
                .param("sortDirection", "desc")
                .param("search", ""))
        .andExpect(status().isOk());

    // then
    verify(companyService, times(2)).findAll(paging, "");
  }
}
