package com.innovationandtrust.process.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.service.RequestSignService;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.InvitationMessage;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectDetail;
import java.util.Date;
import java.util.List;
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
class ProjectControllerTests {
  @Mock ProjectController controller;
  @Mock
  RequestSignService service;
  @Mock Project project;
  @Mock List<Document> documents;
  @Mock List<Participant> participants;
  @Mock Template template;
  @Mock
  ProjectDetail detail;
  @Mock
  List<InvitationMessage> invitationMessages;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    detail = new ProjectDetail();
    detail.setInvitationMessages(invitationMessages);
    detail.setExpireDate(new Date());
    detail.setSessionId(1L);
    detail.setScenarioId(1L);

    template = Template.builder().id(1L).name("Individual").approval(1).signature(1).approvalProcess(
        ScenarioStep.APPROVAL).signProcess(ScenarioStep.INDIVIDUAL_SIGN).build();

    project = new Project();
    project.setId(1L);
    project.setFlowId("123");
    project.setName("HERMAN");
    project.setCreatedBy(1L);
    project.setDocuments(documents);
    project.setParticipants(participants);
    project.setTemplate(template);
    project.setDetail(detail);
  }

  @DisplayName("Request sign")
  @Test
  void request_sign() throws Exception {
    service.requestSign(project);
    mockMvc
        .perform(
            post("/v1/process-controls/project/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(project)))
        .andExpect(status().isOk());

    // then
    verify(service, times(2)).requestSign(project);
  }
}
