package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.tinyurl.TinyUrlFeignClient;
import com.innovationandtrust.utils.tinyurl.model.DataResponse;
import com.innovationandtrust.utils.tinyurl.model.TinyUrlResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@Slf4j
@ExtendWith(SpringExtension.class)
class NotificationReminderLauncherHandlerTest {
  private NotificationReminderLauncherHandler notificationReminderLauncherHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private TinyUrlFeignClient tinyUrlFeignClient;
  @Mock private NotificationFeignClient notificationFeignClient;
  @Mock private TemplateEngine templateEngine;
  @Mock private ImpersonateTokenService impersonateToken;
  private final ProcessControlProperty processControlProperty = UnitTestProvider.getProperty();
  private Project project;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeAll
  public static void init() {
    mockStatic(ProcessControlUtils.class);
  }

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    notificationReminderLauncherHandler =
        spy(
            new NotificationReminderLauncherHandler(
                templateEngine,
                processControlProperty,
                impersonateToken,
                tinyUrlFeignClient,
                notificationFeignClient,
                mock(ValidateCorporateSettingHandler.class)));
    context = UnitTestProvider.getContext();
    project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
  }

  private void after() {
    // update json file to original project for other test case
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @DisplayName("[Notification Reminder Launcher Handler] SMS")
  void reminderSms() {
    project.setReminderChannel(1);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    // given
    var tinyUrlResponse =
        new TinyUrlResponse(new DataResponse("domain", "alias", "tinyUrl", "url"));

    // when
    when(this.tinyUrlFeignClient.shortenUrl(any())).thenReturn(tinyUrlResponse);

    this.notificationReminderLauncherHandler.execute(context);
    verify(this.notificationReminderLauncherHandler, times(1)).execute(context);

    project.getParticipants().get(1).setInvited(true);
    project.getParticipants().get(1).setApproved(false);
    project.getParticipants().get(1).setRole(RoleConstant.ROLE_APPROVAL);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    this.notificationReminderLauncherHandler.execute(context);
    verify(this.notificationReminderLauncherHandler, times(2)).execute(context);

    this.after();
  }

  @Test
  @DisplayName("[Notification Reminder Launcher Handler] Email")
  void reminderEmail() {
    project.setReminderChannel(2);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    this.notificationReminderLauncherHandler.execute(context);
    verify(this.notificationReminderLauncherHandler, times(1)).execute(context);

    this.after();
  }

  @Test
  @DisplayName("[Notification Reminder Launcher Handler] Sms-Email")
  void reminderSmsEmail() {
    project.setReminderChannel(3);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    var tinyUrlResponse =
        new TinyUrlResponse(new DataResponse("domain", "alias", "tinyUrl", "url"));

    // when
    when(this.tinyUrlFeignClient.shortenUrl(any())).thenReturn(tinyUrlResponse);

    this.notificationReminderLauncherHandler.execute(context);
    verify(this.notificationReminderLauncherHandler, times(1)).execute(context);

    this.after();
  }
}
