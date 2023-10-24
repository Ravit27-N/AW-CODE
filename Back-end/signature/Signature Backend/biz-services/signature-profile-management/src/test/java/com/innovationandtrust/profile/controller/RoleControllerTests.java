package com.innovationandtrust.profile.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.profile.model.dto.RoleDto;
import com.innovationandtrust.profile.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class RoleControllerTests {
  @Mock RoleController controller;
  @Mock RoleService service;
  @Mock
  RoleDto dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Save role api test")
  void save_role_api_test() throws Exception {
    service.save(dto);
    mockMvc
        .perform(
            post("/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\",\"name\":\"Admin\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).save(dto);
  }
  @Test
  @DisplayName("Update role api test")
  void update_role_api_test() throws Exception {
    service.update(dto);
    mockMvc
            .perform(
                    put("/v1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":\"1\",\"name\":\"Admin\"}"))
            .andExpect(status().isOk());

    // then
    verify(service, times(1)).update(dto);
  }
  @Test
  @DisplayName("Find role by api test")
  void find_role_by_id_api_test() throws Exception {
    service.findById(1L);
    mockMvc
            .perform(
                    get("/v1/roles/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    // then
    verify(service, times(1)).findById(1L);
  }
}
