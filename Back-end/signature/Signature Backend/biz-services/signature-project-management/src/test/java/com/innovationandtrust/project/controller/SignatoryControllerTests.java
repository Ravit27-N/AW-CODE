package com.innovationandtrust.project.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.project.ProjectManagementApplication;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.service.SignatoryService;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = ProjectManagementApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class SignatoryControllerTests {

  @Mock MockMvc mockMvc;
  @Mock
  SignatoryService signatoryService;
  @Mock
  SignatoryDto signatoryDTO;
  @InjectMocks SignatoryController signatoryController;
  @Mock Page<SignatoryDto> signatoryDTOPage;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());

    mockMvc = MockMvcBuilders.standaloneSetup(signatoryController).build();

    signatoryDTO = new SignatoryDto();
    signatoryDTO.setId(1L);
    signatoryDTO.setFirstName("Sok");
    signatoryDTO.setLastName("Panharith");
    signatoryDTO.setRole(RoleConstant.ROLE_SIGNATORY);
    signatoryDTO.setEmail("sokpanharith.dev@gmail.com");
    signatoryDTO.setPhone("085123123");
    signatoryDTO.setSortOrder(1);
  }

  @DisplayName("Find all signatory api test")
  @Test
  void find_all_signatory_api_test() throws Exception {
    // given
    var filter = "";

    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());

    // when
    when(signatoryService.findAll(paging, filter)).thenReturn(signatoryDTOPage);

    // then
    mockMvc
        .perform(
            get("/v1/signatories")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("filter", "")
                .param("sortDirection", "desc")
                .param("sortByField", "id"))
        .andExpect(status().isOk());

    verify(signatoryService, times(1)).findAll(paging, filter);
  }

  @DisplayName("Save signatory api test")
  @Test
  void save_signatory_api_test() throws Exception {
    // when
    when(signatoryService.save(signatoryDTO)).thenReturn(signatoryDTO);

    // then
    mockMvc
        .perform(
            post("/v1/signatories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signatoryDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName", is("Sok")));

    verify(signatoryService, times(1)).save(signatoryDTO);
  }

  @DisplayName("Find signatory buy id api test")
  @Test
  void find_signatory_by_id_api_test() throws Exception {
    // given
    Long id = 1L;

    // when
    when(signatoryService.findById(id)).thenReturn(signatoryDTO);

    // then
    mockMvc
        .perform(get("/v1/signatories/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));

    verify(signatoryService, times(1)).findById(1L);
  }

  @DisplayName("Update signatory api test")
  @Test
  void update_signatory_api_test() throws Exception {
    // given
    SignatoryDto signatoryDTO = new SignatoryDto();
    signatoryDTO.setId(1L);
    signatoryDTO.setFirstName("Levi");
    signatoryDTO.setLastName("Jaxonn");
    signatoryDTO.setRole(RoleConstant.ROLE_SIGNATORY);
    signatoryDTO.setEmail("jaxonnlevi.dev@gmail.com");
    signatoryDTO.setPhone("085123123");
    signatoryDTO.setSortOrder(1);

    // when
    when(signatoryService.update(signatoryDTO)).thenReturn(signatoryDTO);

    // then
    mockMvc
        .perform(
            put("/v1/signatories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signatoryDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email", is("jaxonnlevi.dev@gmail.com")));

    verify(signatoryService, times(1)).update(signatoryDTO);
  }

  @DisplayName("Delete signatory api test")
  @Test
  void delete_signatory_test() throws Exception {
    // given
    Long id = 1L;

    signatoryService.delete(id);

    // then
    mockMvc.perform(delete("/v1/signatories/{id}", id)).andExpect(status().isOk());

    verify(signatoryService, times(2)).delete(1L);
  }

  @DisplayName("Update invitation status api test")
  @Test
  void update_status_api_test() throws Exception {
    var signatories = List.of(new SignatoryRequest(1L, InvitationStatus.IN_PROGRESS));
    signatoryService.updateStatus(
        1L, signatories);

    // then
    mockMvc
        .perform(
            put("/v1/signatories/{projectId}/status/", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signatories)))
        .andExpect(status().isOk());

    verify(signatoryService, times(2)).updateStatus(1L, signatories);
  }
}
