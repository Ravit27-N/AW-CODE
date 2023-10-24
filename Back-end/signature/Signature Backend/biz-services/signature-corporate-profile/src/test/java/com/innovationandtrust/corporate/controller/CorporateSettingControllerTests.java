package com.innovationandtrust.corporate.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.corporate.model.dto.CorporateSettingRequest;
import com.innovationandtrust.corporate.service.CorporateSettingService;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class CorporateSettingControllerTests {
  @Mock CorporateSettingController controller;
  @Mock CorporateSettingService service;
  @Mock CorporateSettingRequest request;
  @Mock
  CorporateSettingDto dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Get all corporate setting in pagination api test")
  void get_all_corporate_setting_test() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    service.findAll(paging);

    mockMvc
        .perform(
            get("/v1/corporate/settings")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortDirection", "desc")
                .param("sortByField", "id"))
        .andExpect(status().isOk());
    // then
    verify(service, times(1)).findAll(paging);
  }

  @Test
  @DisplayName("Save corporate setting api test")
  void save_corporate_setting_api_test() throws Exception {
    // given
    MultipartFile files =
        new MockMultipartFile("file2", "file2.txt", "text/plain", "Goodbye, world!".getBytes());

    mockMvc
        .perform(
            get("/v1/corporate/settings")
                .flashAttr("files", files)
                .flashAttr("corporateSettingRequest", request))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Find corporate setting by id api test")
  void find_by_id_api_test() throws Exception {
    mockMvc.perform(get("/v1/corporate/settings/{id}", 1L)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Update corporate setting api test")
  void update_api_test() throws Exception {
    // given
    MultipartFile files =
        new MockMultipartFile("file2", "file2.txt", "text/plain", "Goodbye, world!".getBytes());

    mockMvc
        .perform(
            get("/v1/corporate/settings")
                .flashAttr("files", files)
                .flashAttr("corporateSettingDTO", dto))
        .andExpect(status().isOk());
  }
}
