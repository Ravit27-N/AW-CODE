package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

@Slf4j
@ExtendWith(SpringExtension.class)
class RecipientInvitationHandlerTest {
  private RecipientInvitationHandler recipientInvitationHandler;
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

    recipientInvitationHandler =
        spy(new RecipientInvitationHandler(templateEngine, projectFeignClient, emailService));

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
  @DisplayName("[Recipient Invitation Handler]")
  void recipientInvitationHandler() {
    this.recipientInvitationHandler.execute(context);
    verify(this.recipientInvitationHandler, times(1)).execute(context);

    // given
    project.getParticipants().forEach(UnitTestProvider::toProcessed);
    project.getParticipants().get(1).setRole(RoleConstant.ROLE_RECEIPT);
    var mailRequest = UnitTestProvider.getInvitationRequest();

    // when
    when(this.emailService.prepareParticipantMail(any(), any(), any()))
        .thenReturn(mailRequest);
    this.projectFeignClient.updateStatus(anyLong(), any());
    this.recipientInvitationHandler.execute(context);
    verify(this.recipientInvitationHandler, times(2)).execute(context);
  }
}
