package com.innovationandtrust.corporate.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.corporate.model.dto.TemplateDetailDto;
import com.innovationandtrust.corporate.service.TemplateDetailService;
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
class TemplateDetailControllerTests {
  @Mock TemplateDetailController controller;
  @Mock TemplateDetailService service;
  @Mock
  TemplateDetailDto dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    dto = new TemplateDetailDto();
    dto.setTemplateId(1L);
    dto.setRoleId(1L);
    dto.setName("TemplateN");
  }

  @Test
  @DisplayName("List all template details in pagination api test")
  void list_all_template_detail_in_pagination_test() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    service.findAll(paging);

    mockMvc
        .perform(
            get("/v1/template-details")
                .param("pageNum", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortDirection", "desc")
                .param("sortByField", "id"))
        .andExpect(status().isOk());
    // then
    verify(service, times(1)).findAll(paging);
  }

  @Test
  @DisplayName("Save template detail api test")
  void save_template_detail_api_test() throws Exception {
    service.save(dto);
    mockMvc
        .perform(
            post("/v1/template-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).save(dto);
  }

  @Test
  @DisplayName("Find template detail by id api test")
  void find_template_detail_by_id_test() throws Exception {
    service.findById(1L);
    mockMvc
        .perform(get("/v1/template-details/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Update template detail api test")
  void update_template_detail_test() throws Exception {
    service.update(dto);
    mockMvc
        .perform(
            post("/v1/template-details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).update(dto);
  }
}
