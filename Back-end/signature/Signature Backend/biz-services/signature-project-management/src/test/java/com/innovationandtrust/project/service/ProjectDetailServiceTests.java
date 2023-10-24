package com.innovationandtrust.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.project.constant.ProjectDetailTypeConstant;
import com.innovationandtrust.project.model.dto.ProjectDetailDTO;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.ProjectDetail;
import com.innovationandtrust.project.repository.ProjectDetailRepository;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
class ProjectDetailServiceTests {

  @Mock ProjectDetailDTO projectDetailDTO;
  @Mock List<ProjectDetailDTO> projectDetailDTOS;
  private ProjectDetailService projectDetailService;
  @Mock private ProjectDetailRepository projectDetailRepository;
  @Mock private ModelMapper modelMapper;

  @BeforeEach
  void setup() {
    projectDetailDTO = new ProjectDetailDTO();
    projectDetailDTO.setId(1L);
    projectDetailDTO.setTitleInvitation("Invite HERMAN");
    projectDetailDTO.setMessageInvitation("Message HERMAN");
    projectDetailDTO.setType(ProjectDetailTypeConstant.SIGNATORY);
    projectDetailDTO.setProjectId(1L);
    projectDetailService = spy(new ProjectDetailService(projectDetailRepository, modelMapper));
  }

  @Test
  @DisplayName("Get all project details")
  void find_all_project_details() {
    // when
    when(projectDetailRepository.findAll())
        .thenReturn(List.of(new ProjectDetail(), new ProjectDetail()));

    List<ProjectDetailDTO> result = projectDetailService.findAll();

    // then
    assertEquals(2, result.size());
    verify(projectDetailService, times(1)).findAll();
  }

  @Test
  @DisplayName("Save project detail")
  void save_project_detail() {
    // when
    when(projectDetailService.save(projectDetailDTO)).thenReturn(projectDetailDTO);

    ProjectDetailDTO result = projectDetailService.save(projectDetailDTO);

    // then
    assertThat(result).isNotNull();
    verify(projectDetailService, times(2)).save(projectDetailDTO);
  }

  @Test
  @DisplayName("Save project detail with exception")
  void save_project_detail_throw_exception() {
    var detail = new ProjectDetailDTO(1L, "Invite HERMAN", "Invite HERMAN", "", 1L);
    // when
    assertThatThrownBy(() -> projectDetailService.save(detail))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  @DisplayName("Update project detail")
  void update_project_detail() {
    // when
    when(projectDetailService.update(projectDetailDTO)).thenReturn(projectDetailDTO);

    ProjectDetailDTO result = projectDetailService.update(projectDetailDTO);

    // then
    assertEquals("Invite HERMAN", result.getTitleInvitation());
    verify(projectDetailService, times(2)).update(projectDetailDTO);
  }

  @Test
  @DisplayName("Save all project details")
  void save_all_project_details() {
    // when
    when(projectDetailRepository.saveAll(any()))
        .thenReturn(List.of(new ProjectDetail(), new ProjectDetail()));

    List<ProjectDetailDTO> result = projectDetailService.saveAll(projectDetailDTOS);

    // then
    assertEquals(2, result.size());
    verify(projectDetailService, times(1)).saveAll(projectDetailDTOS);
  }

  @Nested
  @DisplayName("Get project detail by id")
  class GetProjectDetailById {
    @DisplayName("When project detail with given id is found in database")
    @Test
    void find_project_detail_by_id() {
      var detail =
          Optional.of(
              new ProjectDetail(
                  1L,
                  "Invite HERMAN",
                  "Invite HERMAN",
                  ProjectDetailTypeConstant.SIGNATORY,
                  new Project()));
      ReflectionTestUtils.setField(projectDetailService, "modelMapper", modelMapper);
      // when
      when(projectDetailRepository.findById(1L)).thenReturn(detail);

      ProjectDetailDTO result = projectDetailService.findById(1L);

      // then
      assertThat(result).isNotNull();
      verify(projectDetailService, times(1)).findById(1L);
    }

    @Test
    void find_project_detail_by_type() {
      var detail =
          Optional.of(
              new ProjectDetail(
                  1L,
                  "Invite HERMAN",
                  "Invite HERMAN",
                  ProjectDetailTypeConstant.SIGNATORY,
                  new Project(1L, "Hello","")));
      ReflectionTestUtils.setField(projectDetailService, "modelMapper", modelMapper);
      // when
      when(projectDetailRepository.findByTypeAndProjectId(ProjectDetailTypeConstant.SIGNATORY, 1L))
          .thenReturn(detail);

      when(projectDetailService.findByType(ProjectDetailTypeConstant.SIGNATORY, 1L))
          .thenReturn(projectDetailDTO);
      ProjectDetailDTO result =
          projectDetailService.findByType(ProjectDetailTypeConstant.SIGNATORY, 1L);

      // then
      assertThat(result).isNotNull();
      assertEquals(detail.get().getTitleInvitation(), result.getTitleInvitation());
      verify(projectDetailService, times(2)).findByType(ProjectDetailTypeConstant.SIGNATORY, 1L);
    }

    @Test
    void find_project_detail_by_id_then_throw_exception() {
      assertThatThrownBy(() -> projectDetailService.findEntityById(1L))
          .isInstanceOf(EntityNotFoundException.class);
    }
  }
}
