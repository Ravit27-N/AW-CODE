package com.innovationandtrust.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.model.dto.DocumentDTO;
import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.model.dto.ProjectDetailDTO;
import com.innovationandtrust.project.model.dto.ProjectRequest;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.service.DocumentService;
import com.innovationandtrust.project.service.ProjectService;
import com.innovationandtrust.project.service.SignatureService;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.DashboardDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.project.DocumentRequest;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.ProjectUpdateRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProjectControllerTests {
  @InjectMocks ProjectController projectController;
  @Mock private MockMvc mockMvc;
  @Mock private ProjectService projectService;
  @Mock private DocumentService documentService;
  @Mock private SignatureService signatureService;
  @Mock private FileProvider fileProvider;

  ProjectDTO projectDTO;
  SignatoryDto signatoryDTO;
  ProjectRequest projectRequest;
  ProjectAfterSignRequest request;
  FileResponse fileResponse;
  Resource resource;
  DocumentContent documentContent;
  Page<ProjectDTO> projectDTOPage;
  List<ProjectDTO> projectDTOS;
  List<FileResponse> fileResponses;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    projectController =
        new ProjectController(
            projectService, fileProvider, documentService, signatureService);

    mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

    projectDTO = new ProjectDTO();
    projectDTO.setId(1L);
    projectDTO.setName("HERMAN");
    projectDTO.setStep("1");

    projectDTOS = new ArrayList<>();
    projectDTOS.add(projectDTO);

    projectDTOPage = new PageImpl<>(projectDTOS);

    documentContent = new DocumentContent();
    documentContent.setFileName("abc.pdf");
    documentContent.setResource(resource);
    documentContent.setContentLength(100L);
    documentContent.setContentType("application/json");

    projectRequest = new ProjectRequest();
    projectRequest.setId(1L);
    projectRequest.setName("Signatory");
    projectRequest.setStep("1");
    projectRequest.setSignatories(new ArrayList<>());
    projectRequest.setDetails(new ArrayList<>());
    projectRequest.setDocumentDetails(new ArrayList<>());

    signatoryDTO = new SignatoryDto();
    signatoryDTO.setId(1L);
    signatoryDTO.setFirstName("Sok");
    signatoryDTO.setLastName("Panharith");

    fileResponses = new ArrayList<>();
    fileResponse =
        FileResponse.builder()
            .fileName("file1")
            .originalFileName("file1.pdf")
            .size(1200L)
            .fullPath("/uploads/")
            .build();
    fileResponses.add(fileResponse);
  }

  @Test
  @DisplayName("Create project api test")
  void create_project_api_test() throws Exception {
    MultipartFile[] files =
        new MultipartFile[] {
          new MockMultipartFile("file1", "file1.pdf", "application/pdf", "Hello, World!".getBytes())
        };

    var projectDto = new ProjectDTO(1L, "Signature");

    // when
    when(projectService.save(any(), any())).thenReturn(projectDto);

    mockMvc
        .perform(
            post("/v1/projects")
                .flashAttr("files", files)
                .param("dirs", "test")
                .flashAttr("projectDTO", projectDto)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @DisplayName("View document api test")
  @Test
  void view_document_api_test() throws Exception {
    // when
    when(documentService.viewDocumentBase64("abc.pdf")).thenReturn(anyString());

    // then
    mockMvc
        .perform(get("/v1/projects/view-documents").param("docName", "abc.pdf"))
        .andExpect(status().isOk());

    verify(documentService, times(1)).viewDocumentBase64("abc.pdf");
  }

  @DisplayName("View document content api test")
  @Test
  void view_document_base64_api_test() throws Exception {
    // given
    String docName = "abc.pdf";

    // when
    when(documentService.viewDocument(docName)).thenReturn(documentContent);

    mockMvc
        .perform(
            get("/v1/projects/view-documents/content")
                .param("docName", "abc.pdf")
                .header(HttpHeaders.CONTENT_TYPE, documentContent.getContentType())
                .header(HttpHeaders.CONTENT_TYPE, documentContent.getContentLength()))
        .andExpect(status().isOk());

    verify(documentService, times(1)).viewDocument(docName);
  }

  @DisplayName("Update project api test")
  @Test
  void update_project_api_test() throws Exception {
    var projectRequest =
        new ProjectRequest(
            1L,
            "Signature",
            List.of(
                new SignatoryDto(
                    1L,
                    "Vichet",
                    "CHANN",
                    RoleConstant.ROLE_SIGNATORY,
                    "admin.signature@tessi.fr",
                    "+8551532873",
                    1)),
            List.of(new ProjectDetailDTO()));
    signatureService.updateProjectAndSendProcess(projectRequest);
    mockMvc
        .perform(
            put("/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(projectRequest)))
        .andExpect(status().isOk());
  }

  @DisplayName("Update project after signed api test")
  @Test
  void update_project_after_signed_api_test() throws Exception {
    var projectRequest =
        new ProjectAfterSignRequest(
            new SignatoryRequest(1L, DocumentStatus.SIGNED),
            List.of(new DocumentRequest(1L, "/api/v1/session/1/documents/3")));

    projectService.updateProjectAfterSigned(projectRequest);

    // then
    mockMvc
        .perform(
            put("/v1/projects/signed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(projectRequest)))
        .andExpect(status().isOk());

    verify(projectService, times(1)).updateProjectAfterSigned(projectRequest);
  }

  @Test
  @DisplayName("Update project after refused api test")
  void update_project_after_refused() throws Exception {
    var projectUpdateRequest =
        new ProjectUpdateRequest(new SignatoryRequest(1L, DocumentStatus.REFUSED));

    projectService.updateProjectAfterRefused(projectUpdateRequest);

    mockMvc
        .perform(
            put("/v1/projects/refused")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(projectUpdateRequest)))
        .andExpect(status().isOk());

    verify(projectService, times(1)).updateProjectAfterRefused(projectUpdateRequest);
  }

  @Test
  @DisplayName("Update project after expired api test")
  void update_project_after_expired() throws Exception {
    projectService.updateProjectStatusExpired(1L);

    mockMvc
        .perform(put("/v1/projects/expired").param("id", String.valueOf(1L)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Update project after read api test")
  void update_project_after_read() throws Exception {
    var projectUpdateRequest =
        new ProjectUpdateRequest(new SignatoryRequest(1L, DocumentStatus.READ));

    projectService.readDocument(projectUpdateRequest);

    mockMvc
        .perform(
            put("/v1/projects/read")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(projectUpdateRequest)))
        .andExpect(status().isOk());
  }

  @DisplayName("Find all projects api test")
  @Test
  void find_all_projects_api_test() throws Exception {

    // when
    when(projectService.findAll(any(), anyString())).thenReturn(projectDTOPage);

    // then
    mockMvc
        .perform(
            get("/v1/projects")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("filter", "")
                .param("sortDirection", "desc")
                .param("sortByField", "id"))
        .andExpect(status().isOk());
  }

  @DisplayName("Find all by user api test")
  @Test
  void find_all_by_user_api_test() throws Exception {

    // when
    when(projectService.findAllByFilter(
            any(), any(), any(), anyString(), any(), any(), anyString()))
        .thenReturn(projectDTOPage);

    // then
    mockMvc
        .perform(
            get("/v1/projects/user")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("filter", "")
                .param("sortDirection", "desc")
                .param("sortByField", "id")
                .param("filterBy", "user")
                .param("filterSteps", "")
                .param("statuses", "")
                .param("startDate", "")
                .param("endDate", ""))
        .andExpect(status().isOk());
  }

  @DisplayName("Find all by corporate api test")
  @Test
  void find_all_by_corporate_api_test() throws Exception {

    // when
    when(projectService.findAllByCorporate(any(), any(),any(),any(),any(), anyString(), anyString())).thenReturn(projectDTOPage);

    // then
    mockMvc
        .perform(
            get("/v1/projects/corporate")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("search", anyString())
                .param("filterSteps", (""))
                .param("sortDirection", "desc")
                .param("sortByField", "id"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
  }

  @DisplayName("Find all by company api test")
  @Test
  void find_all_by_company_api_test() throws Exception {

    // when
    when(projectService.findAllByCompany(any(), anyString(), any())).thenReturn(projectDTOPage);

    // then
    mockMvc
        .perform(
            get("/v1/projects/company")
                .param("page", String.valueOf(1))
                .param("pageSize", String.valueOf(10))
                .param("search", anyString())
                .param("filterSteps", (""))
                .param("sortDirection", "desc")
                .param("sortByField", "id"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
  }

  @DisplayName("Find project by id api test")
  @Test
  void find_project_by_id() throws Exception {
    // given
    Long id = 1L;

    // when
    when(projectService.findById(id)).thenReturn(projectDTO);

    mockMvc.perform(get("/v1/projects/{id}", 1)).andExpect(status().isOk());

    verify(projectService, times(1)).findById(1L);
  }

  @DisplayName("Complete project by id")
  @Test
  void complete_project_by_id() throws Exception {
    mockMvc.perform(put("/v1/projects/complete/{id}", 1)).andExpect(status().isOk());
    // then
    verify(projectService, times(1)).completeProject(1L, ProjectStatus.COMPLETED.name());
  }

  @DisplayName("Count employees projects api test")
  @Test
  void count_employees_projects() throws Exception {
    // given
    List<EmployeeDTO> employeeDtoList = new ArrayList<>();
    employeeDtoList.add(EmployeeDTO.builder().id(1L).firstName("Ana").build());

    // when
    when(projectService.countEmployeeProject(any(), anyString(), anyString()))
        .thenReturn(employeeDtoList);

    ResultActions response =
        mockMvc.perform(
            post("/v1/projects/count/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .param("startDate", "")
                .param("endDate", "")
                .content(new ObjectMapper().writeValueAsString(employeeDtoList))
                .accept(MediaType.APPLICATION_JSON));

    response
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string(new ObjectMapper().writeValueAsString(employeeDtoList)));
  }

  @DisplayName("Inserting project flow id api test")
  @Test
  void insert_project_flow_id() throws Exception {

    projectService.insertProjectFlowId(any(), anyString());

    mockMvc.perform(put("/v1/projects/uuid/{id}/{flowId}", 1, "Ana")).andExpect(status().isOk());
  }

  @DisplayName("Corporate dashboard api test")
  @Test
  void corporate_dashboard() throws Exception {
    // given
    var userIds = Arrays.asList(1, 2, 3, 4, 5);
    var dashboardDTO = DashboardDTO.builder().totalProjects(1).build();

    // when
    when(projectService.corporateDashboard(any(), anyString(), anyString()))
        .thenReturn(dashboardDTO);

    ResultActions response =
        mockMvc.perform(
            post("/v1/projects/dashboard")
                .contentType(MediaType.APPLICATION_JSON)
                .param("startDate", "")
                .param("endDate", "")
                .content(new ObjectMapper().writeValueAsString(userIds))
                .accept(MediaType.APPLICATION_JSON));

    response
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string(new ObjectMapper().writeValueAsString(dashboardDTO)));
  }

  @DisplayName("Count project statuses api test")
  @Test
  void count_projects_statuses() throws Exception {

    projectService.countProject(any());

    mockMvc.perform(get("/v1/projects/count")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Update project expiration date api test")
  void update_project_expiration_date() throws Exception {
    mockMvc
        .perform(
            put("/v1/projects/update/expired/{id}", 1)
                .param("id", String.valueOf(1L))
                .param("expiredDate", ""))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Assign projects  api test")
  void assign_projects() throws Exception {
    mockMvc
        .perform(
            put("/v1/projects/assign/{id}", 1)
                .param("assignTo", String.valueOf(1L)))
        .andExpect(status().isOk());
  }
}
