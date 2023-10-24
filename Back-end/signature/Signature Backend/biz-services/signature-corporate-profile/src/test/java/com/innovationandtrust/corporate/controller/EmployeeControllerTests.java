package com.innovationandtrust.corporate.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.corporate.service.EmployeeService;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
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
class EmployeeControllerTests {
  @Mock EmployeeController controller;
  @Mock EmployeeService service;
  @Mock EmployeeDTO dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Get all employees in pagination test")
  void get_all_employees_in_pagination() throws Exception {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    service.findAll(paging);

    mockMvc
        .perform(
            get("/v1/employees")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("sortDirection", "id")
                .param("sortByField", "desc"))
        .andExpect(status().isOk());
    // then
    verify(service, times(1)).findAll(paging);
  }

  @Test
  @DisplayName("Save employee test")
  void save_employee_test() throws Exception {
    service.save(dto);
    mockMvc
        .perform(
            post("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Her\",\"userId\":\"1\",\"businessUnitId\":\"1\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).save(dto);
  }

  @Test
  @DisplayName("Find employee by id test")
  void find_employee_by_id_test() throws Exception {
    service.findById(1L);
    mockMvc.perform(get("/v1/employees/{id}", 1L)).andExpect(status().isOk());
    // then
    verify(service, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Update employee test")
  void update_employee_test() throws Exception {
    service.update(dto);
    mockMvc
        .perform(
            put("/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Her\",\"userId\":\"1\",\"businessUnitId\":\"1\"}"))
        .andExpect(status().isOk());

    // then
    verify(service, times(1)).update(dto);
  }

  @Test
  @DisplayName("Get employees by corporate test")
  void get_employees_by_corporate_test() throws Exception {
    service.findAllByCorporateId(1L);
    mockMvc.perform(get("/v1/employees/corporate/{id}", 1L)).andExpect(status().isOk());

    // then
    verify(service, times(1)).findAllByCorporateId(1L);
  }
}
