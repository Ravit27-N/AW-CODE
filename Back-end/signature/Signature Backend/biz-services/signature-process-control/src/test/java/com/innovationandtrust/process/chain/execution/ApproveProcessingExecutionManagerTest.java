package com.innovationandtrust.process.chain.execution;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.approve.ApprovingCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingIndividualSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.ValidateCorporateSettingHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingDecisionHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith(SpringExtension.class)
class ApproveProcessingExecutionManagerTest {
  private final EmailService emailService;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private ProjectWebHookHandler projectWebHookHandler;
  private ApprovingCounterSignExecutionManager approvingCounterSignExecutionManager;
  private ApprovingCoSignExecutionManager approvingCoSignExecutionManager;
  private ApprovingIndividualSignExecutionManager approvingIndividualSignExecutionManager;
  private ApprovingProcessExecutionManager approvingProcessExecutionManager;

  ApproveProcessingExecutionManagerTest(EmailService emailService) {
    this.emailService = emailService;
  }

  @BeforeEach
  public void setup() {
    JsonFileProcessHandler jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    var approvingProcessHandler = new ApprovingProcessHandler(apiNgFeignClient, projectFeignClient);

    var participantOrderInvitationHandler =
        new ParticipantOrderInvitationHandler(
            new TemplateEngine(),
            projectFeignClient,
            emailService,
            mock(ValidateCorporateSettingHandler.class));

    var unorderedInvitationHandler =
        new ParticipantUnorderedInvitationHandler(
            new TemplateEngine(),
            projectFeignClient,
            emailService,
            mock(ValidateCorporateSettingHandler.class));

    approvingCounterSignExecutionManager =
        spy(
            new ApprovingCounterSignExecutionManager(
                approvingProcessHandler,
                projectWebHookHandler,
                participantOrderInvitationHandler,
                jsonFileProcessHandler));

    approvingCoSignExecutionManager =
        spy(
            new ApprovingCoSignExecutionManager(
                approvingProcessHandler,
                projectWebHookHandler,
                unorderedInvitationHandler,
                jsonFileProcessHandler));

    approvingIndividualSignExecutionManager =
        spy(
            new ApprovingIndividualSignExecutionManager(
                approvingProcessHandler,
                projectWebHookHandler,
                unorderedInvitationHandler,
                jsonFileProcessHandler));

    var approvingDecisionHandler =
        new ApprovingDecisionHandler(
            approvingCounterSignExecutionManager,
            approvingCoSignExecutionManager,
            approvingIndividualSignExecutionManager);

    approvingProcessExecutionManager =
        spy(new ApprovingProcessExecutionManager(jsonFileProcessHandler, approvingDecisionHandler));
  }

  @Test
  @DisplayName("Approving Process Execution Manager Test")
  void approvingProcessExecutionManager() {
    this.approvingProcessExecutionManager.afterPropertiesSet();
    verify(this.approvingProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Approving Counter Sign Execution Manager Test")
  void approvingCounterSignExecutionManager() {
    this.approvingCounterSignExecutionManager.afterPropertiesSet();
    verify(this.approvingCounterSignExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Approving CoSign Execution Manager Test")
  void approvingCoSignExecutionManager() {
    this.approvingCoSignExecutionManager.afterPropertiesSet();
    verify(this.approvingCoSignExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Approving Individual Sign Execution Manager Test")
  void approvingIndividualSignExecutionManager() {
    this.approvingIndividualSignExecutionManager.afterPropertiesSet();
    verify(this.approvingIndividualSignExecutionManager, times(1)).afterPropertiesSet();
  }
}
