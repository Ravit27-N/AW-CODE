package com.innovationandtrust.corporate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.corporate.service.CompanyDetailService;
import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
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
class CompanyDetailControllerTests {
  @Mock CompanyDetailController controller;
  @Mock CompanyDetailService service;
  @Mock CompanyDetailDTO dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    dto = new CompanyDetailDTO();
    dto.setId(1L);
    dto.setCompanyId(1L);
    dto.setFirstName("Her");
    dto.setLastName("Man");
    dto.setGender("Male");
    dto.setAddress("PP");
    dto.setUserId(1L);
  }

  @Test
  @DisplayName("Get all company detail as pagination api test")
  void get_all_company_details_as_pagination() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    service.findAll(paging, "");

    mockMvc
        .perform(
            get("/v1/company/details")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortByField", "id")
                .param("sortDirection", "desc"))
        .andExpect(status().isOk());
    // then
    verify(service, times(1)).findAll(paging, "");
  }

  @Test
  @DisplayName("Save company detail api test")
  void save_company_detail_api_test() throws Exception {
    when(service.save(dto)).thenReturn(dto);
    mockMvc
        .perform(
            post("/v1/company/details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isOk());

    // then
    assertThat(service.save(dto)).isEqualTo(dto);
    verify(service, times(1)).save(dto);
  }

  @Test
  @DisplayName("Find company detail by id api test")
  void find_company_detail_by_id_api_test() throws Exception {
    when(service.findById(1L)).thenReturn(dto);
    mockMvc
        .perform(get("/v1/company/details/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    var result = service.findById(1L);

    // then
    assertThat(service.findById(1L)).isEqualTo(dto);
    assertEquals("Her", result.getFirstName());
    verify(service, times(2)).findById(1L);
  }

  @Test
  @DisplayName("Update company detail api test")
  void update_company_detail_api_test() throws Exception {
    // given
    var newDto = new CompanyDetailDTO();
    newDto.setId(1L);
    newDto.setCompanyId(1L);
    newDto.setFirstName("All");
    newDto.setLastName("Web");
    newDto.setGender("Male");
    newDto.setAddress("PP");
    newDto.setUserId(1L);

    // when
    when(service.update(newDto)).thenReturn(newDto);
    mockMvc
        .perform(
            get("/v1/company/details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newDto)))
        .andExpect(status().isOk());

    var result = service.update(newDto);

    // then
    assertThat(result).isNotNull();
    assertEquals("All", result.getFirstName());
    verify(service, times(1)).update(newDto);
  }
}
