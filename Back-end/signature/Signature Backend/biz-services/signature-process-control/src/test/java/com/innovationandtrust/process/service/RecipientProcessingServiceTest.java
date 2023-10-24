package com.innovationandtrust.process.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.execution.RecipientExecutionManager;
import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.RecipientHandler;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.restclient.SignatoryFeignClient;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.encryption.TokenParam;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.tdcservice.TdcFeignClient;
import com.innovationandtrust.utils.tdcservice.TdcProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith(SpringExtension.class)
class RecipientProcessingServiceTest {

  private RecipientProcessingService recipientProcessingService;
  private RecipientExecutionManager recipientExecutionManager;
  @Mock private ImpersonateTokenService impersonateTokenService;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private TdcFeignClient tdcFeignClient;
  @Mock private TdcProperty tdcProperty;
  private EmailService emailService;
  private ExecutionContext context;
  private final TokenParam param = UnitTestConstant.getParam();
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
            projectFeignClient,
            UnitTestConstant.fileProvider(),
            keycloakProvider,
            profileFeignClient,
            corporateProfileFeignClient);

    RecipientHandler recipientHandler = new RecipientHandler(apiNgFeignClient, projectFeignClient);

    CompleteSigningProcessHandler completeSigningProcessHandler =
        new CompleteSigningProcessHandler(
            corporateProfileFeignClient,
            new TemplateEngine(),
            emailService,
            projectFeignClient,
            profileFeignClient,
            tdcFeignClient,
            apiNgFeignClient,
            tdcProperty);

    recipientExecutionManager =
        spy(
            new RecipientExecutionManager(
                jsonFileProcessHandler, recipientHandler, completeSigningProcessHandler));
    this.recipientExecutionManager.afterPropertiesSet();

    recipientProcessingService =
        spy(new RecipientProcessingService(recipientExecutionManager, impersonateTokenService));

    context = UnitTestConstant.getContext();
  }

  @Test
  @DisplayName("Recipient retrieved project test")
  void retrieved_project_test() {
    // when
    this.recipientExecutionManager.execute(context);
    this.recipientProcessingService.recipient(flowId, uuid);

    verify(recipientProcessingService, times(1)).recipient(flowId, uuid);
  }

  @Test
  @DisplayName("[Public] Recipient retrieved project test")
  void retrieved_external_project_test() {
    // when
    when(this.impersonateTokenService.validateImpersonateToken(any(), any())).thenReturn(param);
    this.recipientExecutionManager.execute(context);
    this.recipientProcessingService.recipient(flowId, uuid);
    this.recipientProcessingService.recipientExternal(flowId, uuid);

    verify(recipientProcessingService, times(1)).recipientExternal(flowId, uuid);
  }
}
