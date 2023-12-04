package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class ParticipantOrderInvitationHandlerTest {
  private ParticipantOrderInvitationHandler participantOrderInvitationHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private TemplateEngine templateEngine;
  @Mock private EmailService emailService;
  private Project project;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    participantOrderInvitationHandler =
        spy(
            new ParticipantOrderInvitationHandler(
                templateEngine,
                projectFeignClient,
                emailService,
                mock(ValidateCorporateSettingHandler.class)));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  private void after() {
    // update json file to original project for other test case
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @DisplayName("[Participant order invite handler]")
  void orderInvitation() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(participant -> participant.setInvited(false));
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var mailRequest = UnitTestProvider.getInvitationRequest();

    // when
    when(this.emailService.prepareParticipantMail(any(), any(), any()))
        .thenReturn(mailRequest);

    this.participantOrderInvitationHandler.execute(context);
    verify(this.participantOrderInvitationHandler, times(1)).execute(context);

    // case End-user
    log.info("[Case participant is END-USER]");
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setEndUser(true);
              participant.setInvited(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    this.participantOrderInvitationHandler.execute(context);
    verify(this.participantOrderInvitationHandler, times(2)).execute(context);
  }

  @Test
  @DisplayName("[Participant order invite handler] Order not approval")
  void orderInvitationWithOrderApproval() {
    // given
    project.getTemplate().setOrderApprove(false);
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setInvited(false);
              participant.setRole(RoleConstant.ROLE_APPROVAL);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var mailRequest = UnitTestProvider.getInvitationRequest();

    // when
    when(this.emailService.prepareParticipantMail(any(), any(), any()))
        .thenReturn(mailRequest);

    this.participantOrderInvitationHandler.execute(context);
    verify(this.participantOrderInvitationHandler, times(1)).execute(context);

    log.info("[Case ready to sign]");
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setInvited(true);
              participant.setApproved(true);
              participant.setRole(RoleConstant.ROLE_APPROVAL);
            });
    var signatory = UnitTestProvider.participant(context);
    signatory.setInvited(false);
    signatory.setOrder(1000);
    project.getParticipants().add(signatory);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    this.participantOrderInvitationHandler.execute(context);
    verify(this.participantOrderInvitationHandler, times(2)).execute(context);
  }
}
