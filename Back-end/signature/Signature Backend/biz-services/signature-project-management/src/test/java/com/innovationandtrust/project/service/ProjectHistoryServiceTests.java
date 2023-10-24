package com.innovationandtrust.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.project.model.dto.ProjectHistoryDTO;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class ProjectHistoryServiceTests {
  @Mock ProjectHistoryService projectHistoryService;
  @InjectMocks
  ProjectHistoryDTO projectHistoryDTO;
  @Mock List<ProjectHistoryDTO> projectHistoryDTOS;

  @BeforeEach
  void setup() {
    projectHistoryDTO = new ProjectHistoryDTO();
    projectHistoryDTO.setId(1L);
    projectHistoryDTO.setDateStatus(new Date());
    projectHistoryDTO.setAction("");
    projectHistoryDTO.setActionBy("");
    projectHistoryDTO.setSortOrder(1);
  }

  @DisplayName("Get all project histories")
  @Test
  void find_all_project_history() {
    // when
    when(projectHistoryService.findAll()).thenReturn(projectHistoryDTOS);
    when(projectHistoryService.findAll().size()).thenReturn(3);

    List<ProjectHistoryDTO> result = projectHistoryService.findAll();

    // then
    assertEquals(3, result.size());
    verify(projectHistoryService, times(2)).findAll();
  }

  @DisplayName("Save project history")
  @Test
  void save_project_history() {
    // when
    when(projectHistoryService.save(projectHistoryDTO)).thenReturn(projectHistoryDTO);

    ProjectHistoryDTO result = projectHistoryService.save(projectHistoryDTO);

    // then
    assertThat(result.getId()).isEqualTo(projectHistoryDTO.getId());
    verify(projectHistoryService, times(1)).save(projectHistoryDTO);
  }

  @DisplayName("Update project history")
  @Test
  void update_project_history() {
    // given
    projectHistoryDTO = new ProjectHistoryDTO();
    projectHistoryDTO.setId(3L);
    projectHistoryDTO.setDateStatus(new Date());
    projectHistoryDTO.setAction("");
    projectHistoryDTO.setActionBy("");
    projectHistoryDTO.setSortOrder(1);
    projectHistoryDTO.setProjectId(1L);
    projectHistoryDTOS.add(projectHistoryDTO);

    // when
    when(projectHistoryService.update(projectHistoryDTO)).thenReturn(projectHistoryDTO);

    ProjectHistoryDTO result = projectHistoryService.update(projectHistoryDTO);

    // then
    assertEquals(3, result.getId());
    verify(projectHistoryService, times(1)).update(projectHistoryDTO);
  }

  @DisplayName("Save all project histories")
  @Test
  void save_all_project_histories() {
    // when
    when(projectHistoryService.saveAll(projectHistoryDTOS)).thenReturn(projectHistoryDTOS);
    when(projectHistoryService.saveAll(projectHistoryDTOS).size()).thenReturn(3);

    List<ProjectHistoryDTO> result = projectHistoryService.saveAll(projectHistoryDTOS);

    // then
    assertEquals(3, result.size());
    verify(projectHistoryService, times(2)).saveAll(projectHistoryDTOS);
  }

  @Nested
  @DisplayName("Get project history by id")
  class get_project_history_by_id {
    @DisplayName("when_project_history_id_is_found")
    @Test
    void find_project_history_by_id() {
      // when
      when(projectHistoryService.findById(1L)).thenReturn(projectHistoryDTO);

      ProjectHistoryDTO result = projectHistoryService.findById(1L);

      // then
      assertThat(result).isNotNull();
      verify(projectHistoryService, times(1)).findById(1L);
    }
  }
}
