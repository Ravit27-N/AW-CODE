package com.innovationandtrust.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.model.dto.ProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class SignatureServiceTests {
  @Mock ProjectRequest projectRequest;
  @Mock SignatureService signatureService;
  @Mock ProjectService projectService;
  @Mock
  ProjectDTO projectDTO;
  @BeforeEach
  void setup() {
    projectRequest = new ProjectRequest();
    projectRequest.setId(1L);
    projectRequest.setName("HERMAN");
  }

  @Nested
  @DisplayName("Update project and send process")
  class UpdateProjectAndSendProcess {
    @Test
    @DisplayName("Update project and send process step 2")
    void update_project_and_send_process_step2() {
      String signatoriesKey = "`signatories`";
      projectService.saveProjectStep2(projectRequest, signatoriesKey);

      // then
      verify(projectService, times(1)).saveProjectStep2(projectRequest, signatoriesKey);
    }
    @Test
    @DisplayName("Update project and send process step 3")
    void update_project_and_send_process_step3() {
      String documentDetailsKey = "`document detail`";
      projectService.saveProjectStep3(projectRequest, documentDetailsKey);

      // then
      verify(projectService, times(1)).saveProjectStep3(projectRequest, documentDetailsKey);
    }
    @Test
    @DisplayName("Update project and send process step 4")
    void update_project_and_send_process_step4() {
      String detailsKey = "`project detail`";
      projectService.saveProjectStep4(projectRequest, detailsKey);

      // then
      verify(projectService, times(1)).saveProjectStep4(projectRequest, detailsKey);
    }
    @Test
    @DisplayName("Update and and send process")
    void update_project_and_send_process_step() {
      //when
      when(signatureService.updateProjectAndSendProcess(projectRequest)).thenReturn(projectDTO);

      ProjectDTO result = signatureService.updateProjectAndSendProcess(projectRequest);

      //then
      assertThat(result).isNotNull();
      verify(signatureService, times(1)).updateProjectAndSendProcess(projectRequest);
    }
  }
}
