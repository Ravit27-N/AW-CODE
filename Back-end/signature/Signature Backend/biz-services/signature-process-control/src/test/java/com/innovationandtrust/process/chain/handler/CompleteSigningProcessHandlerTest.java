package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.handler.sign.UploadSignedDocument;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.tdcservice.TdcFeignClient;
import com.innovationandtrust.utils.tdcservice.model.TdcResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@Slf4j
@ExtendWith(SpringExtension.class)
class CompleteSigningProcessHandlerTest {

  private CompleteSigningProcessHandler completeSigningProcessHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private EmailService emailService;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private TdcFeignClient tdcFeignClient;
  @Mock private TemplateEngine templateEngine;
  private Project project;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {

    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    completeSigningProcessHandler =
        spy(
            new CompleteSigningProcessHandler(
                templateEngine,
                emailService,
                projectFeignClient,
                mock(UploadSignedDocument.class)));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  private void after() {
    // after complete process, make processed json to be original
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @DisplayName("[Completed Project] Completed signed (archiving false)")
  void completeSignedArchivingFalse() {
    // given
    project.getParticipants().forEach(UnitTestProvider::toProcessed);
    var mailRequest = UnitTestProvider.getInvitationRequest();

    // when
    when(this.emailService.prepareSignCompleteMail(any(), any(), any())).thenReturn(mailRequest);

    this.completeSigningProcessHandler.execute(context);
    verify(this.completeSigningProcessHandler).execute(context);

    // after
    this.after();
  }

  @Test
  @DisplayName("[Completed Project] Completed signed (archiving true)")
  void completeSignedArchivingTrue() {
    // given
    project.getParticipants().forEach(UnitTestProvider::toProcessed);
    var mailRequest = UnitTestProvider.getInvitationRequest();
    var userCompany = UnitTestProvider.getUserCompany();
    var user = UnitTestProvider.getUser();

    // when
    when(this.emailService.prepareSignCompleteMail(any(), any(), any())).thenReturn(mailRequest);
    when(this.profileFeignClient.findCompanyUserById(anyLong())).thenReturn(userCompany);
    when(this.profileFeignClient.findUserById(anyLong())).thenReturn(user);
    when(this.tdcFeignClient.uploadDocument(any(), any())).thenReturn(new TdcResponse());

    this.completeSigningProcessHandler.execute(context);
    verify(this.completeSigningProcessHandler).execute(context);

    // after
    this.after();
  }

  @Test
  @DisplayName("[Completed Project] Completed signed thrown exception")
  void completeSignedThrownException() {
    // given
    project.getParticipants().forEach(UnitTestProvider::toProcessed);
    var mailRequest = UnitTestProvider.getInvitationRequest();
    var userCompany = UnitTestProvider.getUserCompany();

    // when
    when(this.emailService.prepareSignCompleteMail(any(), any(), any())).thenReturn(mailRequest);
    when(this.profileFeignClient.findCompanyUserById(anyLong())).thenReturn(userCompany);

    // then
    this.completeSigningProcessHandler.execute(context);
    verify(this.completeSigningProcessHandler).execute(context);

    // after
    this.after();
  }

  @Test
  @DisplayName("[Completed Project] Completed signed (has refused)")
  void completeSignedHasRefused() {
    // given
    project.getParticipants().get(0).setRefused(true);
    project.getParticipants().get(0).setInvited(true);
    project.getParticipants().get(1).setInvited(true);
    project.getParticipants().get(1).setRole(RoleConstant.ROLE_SIGNATORY);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var mailRequest = UnitTestProvider.getInvitationRequest();

    // when
    when(this.emailService.prepareSignCompleteMail(any(), any(), any())).thenReturn(mailRequest);

    this.completeSigningProcessHandler.execute(context);
    verify(this.completeSigningProcessHandler).execute(context);

    // after
    this.after();
  }
}
