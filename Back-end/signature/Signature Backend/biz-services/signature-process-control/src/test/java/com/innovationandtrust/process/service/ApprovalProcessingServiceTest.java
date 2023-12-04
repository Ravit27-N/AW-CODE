package com.innovationandtrust.process.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.ReadProcessExecutionManager;
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
import com.innovationandtrust.process.chain.handler.approve.ReadProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith(SpringExtension.class)
class ApprovalProcessingServiceTest {

  private ApprovalProcessingService approvalProcessingService;
  private ReadProcessExecutionManager readProcessExecutionManager;
  private ApprovingProcessExecutionManager approvingProcessExecutionManager;
  @Mock private ImpersonateTokenService impersonateTokenService;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private ProjectWebHookHandler projectWebHookHandler;
  private ExecutionContext context;
  private final TokenParam param = UnitTestProvider.getParam();
  private final String flowId = UnitTestConstant.FLOW_ID;
  private final String uuid = UnitTestConstant.UUID;

  @BeforeAll
  public static void init() {
    mockStatic(CompanySettingUtils.class);
  }

  @BeforeEach
  public void setup() {
    JsonFileProcessHandler jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    ApprovingProcessHandler approvingProcessHandler =
        new ApprovingProcessHandler(apiNgFeignClient, projectFeignClient);

    ParticipantOrderInvitationHandler participantOrderInvitationHandler =
        new ParticipantOrderInvitationHandler(
            new TemplateEngine(),
            projectFeignClient,
            mock(EmailService.class),
            mock(ValidateCorporateSettingHandler.class));

    ParticipantUnorderedInvitationHandler unorderedInvitationHandler =
        new ParticipantUnorderedInvitationHandler(
            new TemplateEngine(),
            projectFeignClient,
            mock(EmailService.class),
            mock(ValidateCorporateSettingHandler.class));

    ApprovingCounterSignExecutionManager approvingCounterSignExecutionManager =
        new ApprovingCounterSignExecutionManager(
            approvingProcessHandler,
            projectWebHookHandler,
            participantOrderInvitationHandler,
            jsonFileProcessHandler);
    approvingCounterSignExecutionManager.afterPropertiesSet();

    ApprovingCoSignExecutionManager approvingCoSignExecutionManager =
        new ApprovingCoSignExecutionManager(
            approvingProcessHandler,
            projectWebHookHandler,
            unorderedInvitationHandler,
            jsonFileProcessHandler);
    approvingCoSignExecutionManager.afterPropertiesSet();

    ApprovingIndividualSignExecutionManager approvingIndividualSignExecutionManager =
        new ApprovingIndividualSignExecutionManager(
            approvingProcessHandler,
            projectWebHookHandler,
            unorderedInvitationHandler,
            jsonFileProcessHandler);
    approvingIndividualSignExecutionManager.afterPropertiesSet();

    ApprovingDecisionHandler approvingDecisionHandler =
        new ApprovingDecisionHandler(
            approvingCounterSignExecutionManager,
            approvingCoSignExecutionManager,
            approvingIndividualSignExecutionManager);

    approvingProcessExecutionManager =
        spy(new ApprovingProcessExecutionManager(jsonFileProcessHandler, approvingDecisionHandler));
    this.approvingProcessExecutionManager.afterPropertiesSet();

    readProcessExecutionManager =
        spy(
            new ReadProcessExecutionManager(
                jsonFileProcessHandler, new ReadProcessHandler(projectFeignClient)));
    this.readProcessExecutionManager.afterPropertiesSet();

    approvalProcessingService =
        spy(
            new ApprovalProcessingService(
                approvingProcessExecutionManager,
                readProcessExecutionManager,
                impersonateTokenService));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("Approve project test")
  void testApproveProject() {
    this.approvingProcessExecutionManager.execute(context);
    this.approvalProcessingService.approve(flowId, uuid);

    verify(approvalProcessingService, times(1)).approve(flowId, uuid);
  }

  @Test
  @DisplayName("Approve cosign project test")
  void testApproveCoSignProject() {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.getTemplate().setSignProcess(ScenarioStep.COSIGN);
    this.approvingProcessExecutionManager.execute(context);
    this.approvalProcessingService.approve(flowId, uuid);

    verify(approvalProcessingService, times(1)).approve(flowId, uuid);
  }

  @Test
  @DisplayName("[Public] Approve project test")
  void testApproveExternalProject() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.approvingProcessExecutionManager.execute(context);
    this.approvalProcessingService.approve(flowId, uuid);
    this.approvalProcessingService.approveExternal(flowId, uuid);

    verify(approvalProcessingService, times(1)).approveExternal(flowId, uuid);
  }

  @Test
  @DisplayName("Read project test")
  void testReadProject() {
    this.readProcessExecutionManager.execute(context);
    this.approvalProcessingService.read(flowId, uuid);

    verify(approvalProcessingService, times(1)).read(flowId, uuid);
  }

  @Test
  @DisplayName("[Public] Read project test")
  void testReadExternalProject() {
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.approvingProcessExecutionManager.execute(context);
    this.approvalProcessingService.read(flowId, uuid);
    this.approvalProcessingService.readExternal(flowId, uuid);

    verify(approvalProcessingService, times(1)).readExternal(flowId, uuid);
  }
}
