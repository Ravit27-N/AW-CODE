package com.innovationandtrust.profile.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.service.NormalUserService;
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
class NormalUserControllerTests {
  @Mock NormalUserService service;
  @Mock NormalUserController controller;
  @Mock
  NormalUserDto dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Find all normal users api test")
  void find_all_normal_users_api_test() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    service.findAll(paging, "");

    mockMvc
        .perform(
            get("/v1/users")
                .param("pageNum", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortField", "id")
                .param("sortDirection", "desc")
                .param("search", ""))
        .andExpect(status().isOk());
    // then
    verify(service, times(1)).findAll(paging, "");
  }

  @Test
  @DisplayName("Find normal user by id api test")
  void find_normal_user_by_id_api_test() throws Exception {
    service.findById(1L);
    mockMvc
        .perform(get("/v1/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Save normal user api test")
  void save_normal_user_api_test() throws Exception {
    service.save(dto);
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"businessId\":\"1\",\"firstName\":\"Her\",\"lastName\":\"Man\",\"email\":\"herman@gmaill.com\",\"password\":\"123\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).save(dto);
  }

  @Test
  @DisplayName("Update normal user api test")
  void update_normal_user_api_test() throws Exception {
    service.update(dto);
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\":\"1\",\"businessId\":\"1\",\"firstName\":\"Her\",\"lastName\":\"Man\",\"email\":\"herman@gmaill.com\",\"password\":\"123\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).update(dto);
  }
}
