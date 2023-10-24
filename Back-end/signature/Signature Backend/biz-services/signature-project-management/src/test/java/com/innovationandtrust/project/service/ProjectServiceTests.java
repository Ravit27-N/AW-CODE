package com.innovationandtrust.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.repository.ProjectRepository;
import com.innovationandtrust.project.service.specification.ProjectSpecification;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.DocumentRequest;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class ProjectServiceTests {
  @Mock ProjectService projectService;
  @Mock ProjectRepository projectRepository;
  @Mock Project project;
  @Mock ProjectDTO dto;
  @Mock Page<ProjectDTO> projectDTOPage;
  @Mock Page<com.innovationandtrust.project.model.entity.Project> projectPage;
  @Mock ProjectAfterSignRequest request;
  @Mock List<DocumentRequest> documentRequests;
  @Mock SignatoryRequest signatory;
  @Mock DocumentRequest documentRequest;
  @Mock List<String> filterSteps;

  @BeforeEach
  void setup() {
    project = new Project();
    project.setId(1L);
    project.setName("Test project");

    dto = new ProjectDTO();
    dto.setId(1L);
    dto.setName("Test project");
    dto.setStatus("IN PROGRESS");
    dto.setStep("2");

    documentRequest = new DocumentRequest();
    documentRequest.setId(1L);
    documentRequest.setSignedDocUrl("http://localhost:3000");
    documentRequests.add(documentRequest);

    signatory = new SignatoryRequest();
    signatory.setId(1L);
    signatory.setDocumentStatus(DocumentStatus.SIGNED);

    request = new ProjectAfterSignRequest();
    request.setSignatory(signatory);
    request.setDocuments(documentRequests);
  }

  @Test
  @DisplayName("Get all projects")
  void get_all_projects() {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());

    // when
    when(projectRepository.findAll(
            Specification.where(ProjectSpecification.searchByName("")), paging))
        .thenReturn(projectPage);
    when(projectService.findAll(paging, "")).thenReturn(projectDTOPage);

    Page<ProjectDTO> result = projectService.findAll(paging, "");

    // then
    assertThat(result).isNotNull();
    verify(projectService, times(1)).findAll(paging, "");
  }

  @Test
  @DisplayName("Save project")
  void save_project() {
    // Create a mock multipart file array.
    MultipartFile[] files =
        new MultipartFile[] {
          new MockMultipartFile("file1", "file1.txt", "text/plain", "Hello, world!".getBytes()),
          new MockMultipartFile("file2", "file2.txt", "text/plain", "Goodbye, world!".getBytes())
        };

    // when
    when(projectService.save(dto, files)).thenReturn(dto);

    ProjectDTO result = projectService.save(dto, files);

    // then
    assertThat(result.getName()).isEqualTo(dto.getName());
  }

  @Test
  @DisplayName("Update project after signed")
  void update_project_after_signed() {
    projectService.updateProjectAfterSigned(request);

    // then
    verify(projectService, times(1)).updateProjectAfterSigned(request);
  }

  @Nested
  @DisplayName("Get project by Id")
  class GetProjectById {
    @DisplayName("When project with given id is found in database")
    @Test
    void get_project_by_id() {
      // when
      when(projectService.findById(1L)).thenReturn(dto);

      ProjectDTO result = projectService.findById(1L);

      // then
      assertThat(result).isNotNull();
      assertEquals("Test project", result.getName());
      verify(projectService, times(1)).findById(1L);
    }
  }

  @Nested
  @DisplayName("Get all projects by user")
  class GetAllProjectsByUser {
    @Test
    @DisplayName("When user with given id is found in database")
    void get_all_projects_by_user() {
      Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());

      // when
      when(projectRepository.findAll(
              Specification.where(
                  Objects.requireNonNull(ProjectSpecification.searchByCreatedBy(1L))
                      .and(ProjectSpecification.searchByName(""))),
              paging))
          .thenReturn(projectPage);
      when(projectService.findAllByUser(paging, "", "COMPLETED")).thenReturn(projectDTOPage);

      Page<ProjectDTO> result = projectService.findAllByUser(paging, "", "COMPLETED");

      // then
      assertThat(result).isNotNull();
      verify(projectService, times(1)).findAllByUser(paging, "", "COMPLETED");
    }
  }

  @Nested
  @DisplayName("Get all projects by corporate")
  class GetAllProjectsByCorporate {
    @Test
    @DisplayName("When corporate with given id is found in database")
    void get_all_projects_by_corporate() {
      Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());

      // when
      when(projectService.findAllByCorporate(
              paging, 1L, "", filterSteps, Collections.emptyList(), "", ""))
          .thenReturn(projectDTOPage);

      Page<ProjectDTO> result =
          projectService.findAllByCorporate(
              paging, 1L, "", filterSteps, Collections.emptyList(), "", "");

      // then
      assertThat(result).isNotNull();
      verify(projectService, times(1))
          .findAllByCorporate(paging, 1L, "", filterSteps, Collections.emptyList(), "", "");
    }
  }

  @Nested
  @DisplayName("Request sign process")
  class RequestSignProcess {
    @Test
    @DisplayName("When project with given id is found in database")
    void request_sign_process() {
      projectService.requestSignProcess(1L, any());

      // then
      verify(projectService, times(1)).requestSignProcess(1L, any());
    }
  }

  @Nested
  @DisplayName("Delete project")
  class DeleteProject {
    @Test
    @DisplayName("When project with given id is found in database")
    void delete_project() {
      // when
      projectService.delete(1L);

      // then
      verify(projectService, times(1)).delete(1L);
    }
  }

  @Nested
  @DisplayName("Complete project")
  class CompleteProject {
    @Test
    @DisplayName("When project with given id is found in database")
    void complete_project() {
      // when
      projectService.completeProject(1L, ProjectStatus.COMPLETED.name());

      // then
      verify(projectService, times(1)).completeProject(1L, ProjectStatus.COMPLETED.name());
    }
  }
}
