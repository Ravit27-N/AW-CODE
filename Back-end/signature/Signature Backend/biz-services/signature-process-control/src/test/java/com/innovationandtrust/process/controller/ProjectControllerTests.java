package com.innovationandtrust.process.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.service.RequestSignService;
import com.innovationandtrust.process.service.SendReminderService;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.InvitationMessage;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectDetail;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
  @InjectMocks ProjectController projectController;
  @Mock RequestSignService requestSignService;
  @Mock SendReminderService sendReminderService;
  @Mock Project project;
  @Mock Template template;
  @Mock ProjectDetail detail;
  private MockMvc mockMvc;
  private FileResponse fileResponse;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

    final var invitationMessage = new InvitationMessage();
    invitationMessage.setInvitationMessage("Invite Message");
    invitationMessage.setInvitationSubject("Invite Subject");
    invitationMessage.setType(ParticipantRole.SIGNATORY.getRole());

    final List<InvitationMessage> invitationMessages = new ArrayList<>();
    invitationMessages.add(invitationMessage);

    detail = new ProjectDetail();
    detail.setInvitationMessages(invitationMessages);
    detail.setExpireDate(new Date());
    detail.setSessionId(1L);
    detail.setScenarioId(1L);

    final List<Document> documents = new ArrayList<>();
    final var document = new Document();
    document.setName("Project");
    document.setId(1L);
    documents.add(document);

    final List<Participant> participantList = new ArrayList<>();
    final var participant = new Participant();
    participant.setId(1L);
    participant.setUuid(UnitTestConstant.UUID);
    participantList.add(participant);

    template =
        Template.builder()
            .id(1L)
            .name("Individual")
            .approval(1)
            .signature(1)
            .approvalProcess(ScenarioStep.APPROVAL)
            .signProcess(ScenarioStep.INDIVIDUAL_SIGN)
            .build();

    project = new Project();
    project.setId(1L);
    project.setFlowId("123");
    project.setName("HERMAN");
    project.setCreatedBy(1L);
    project.setDocuments(documents);
    project.setParticipants(participantList);
    project.setTemplate(template);
    project.setDetail(detail);

    fileResponse = new FileResponse();
    fileResponse.setFilename("abc.pdf");
    fileResponse.setSize(1200);
    fileResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    fileResponse.setResource("123".getBytes());
  }

  @Test
  @DisplayName("Request sign")
  void testRequestSign() throws Exception {
    requestSignService.requestSign(project);
    mockMvc
        .perform(
            post("/v1/process-controls/project/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(project)))
        .andExpect(status().isOk());

    // then
    verify(requestSignService, times(2)).requestSign(project);
  }

  @Test
  @DisplayName("Update expire date")
  void testUpdateExpireDate() throws Exception {
    requestSignService.updateExpireDate(UnitTestConstant.FLOW_ID, UnitTestConstant.EXPIRED_DATE);
    mockMvc
        .perform(
            post("/v1/process-controls/project/update/{flowId}", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("expireDate", UnitTestConstant.EXPIRED_DATE))
        .andExpect(status().isOk());

    // then
    verify(requestSignService, times(2))
        .updateExpireDate(UnitTestConstant.FLOW_ID, UnitTestConstant.EXPIRED_DATE);
  }

  @Test
  @DisplayName("Send reminder")
  void testSendReminder() throws Exception {
    sendReminderService.sendReminder(UnitTestConstant.FLOW_ID);
    mockMvc
        .perform(
            post("/v1/process-controls/project/send-reminder/{flowId}", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(sendReminderService, times(2)).sendReminder(UnitTestConstant.FLOW_ID);
  }

  @Test
  @DisplayName("Get manifest")
  void testGetManifest() throws Exception {
    when(this.requestSignService.downloadManifest(UnitTestConstant.FLOW_ID))
        .thenReturn(fileResponse);
    mockMvc
        .perform(
            get("/v1/process-controls/manifest/{flowId}", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(this.requestSignService, times(1)).downloadManifest(UnitTestConstant.FLOW_ID);
  }

  @Test
  @DisplayName("Check finished")
  void testIsFinished() throws Exception {
    when(this.requestSignService.isFinished(UnitTestConstant.FLOW_ID)).thenReturn(Boolean.TRUE);
    mockMvc
        .perform(
            get("/v1/process-controls/is-finished/{flowId}", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(this.requestSignService, times(1)).isFinished(UnitTestConstant.FLOW_ID);
  }

  @Test
  @DisplayName("Get participant identity document")
  void testGetIdentityDocuments() throws Exception {
    when(this.requestSignService.getIdentityDocuments(UnitTestConstant.FLOW_ID))
        .thenReturn(new ArrayList<>());
    mockMvc
        .perform(
            get("/v1/process-controls/{flowId}/identity/documents", UnitTestConstant.FLOW_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(this.requestSignService, times(1)).getIdentityDocuments(UnitTestConstant.FLOW_ID);
  }
}
