package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.CronExpressionUtils;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.date.DateUtil;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import java.util.Date;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.quartz.TriggerBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class NotificationReminderScheduleHandlerTest {
  private NotificationReminderScheduleHandler notificationReminderScheduleHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private SchedulerHandler schedulerHandler;
  private Project project;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    notificationReminderScheduleHandler =
        spy(new NotificationReminderScheduleHandler(schedulerHandler));

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

  private void setExpire() {
    project.getDetail().setExpireDate(DateUtil.plushDays(new Date(), 4));
    context.put(SignProcessConstant.PROJECT_KEY, project);
  }

  @Test
  @Order(1)
  @DisplayName("[Notification Reminder Scheduler] Set schedule")
  void setSchedule() {
    // given
    this.setExpire();

    this.notificationReminderScheduleHandler.execute(context);
    verify(this.notificationReminderScheduleHandler).execute(context);
  }

  @Test
  @Order(2)
  @DisplayName("[Notification Reminder Scheduler] Set schedule fail build cron")
  void setScheduleFailBuiltCron() {
    // given
    this.setExpire();

    try (MockedStatic<CronExpressionUtils> cronExpressionUtilsMockedStatic =
        mockStatic(CronExpressionUtils.class)) {
      cronExpressionUtilsMockedStatic
          .when(() -> CronExpressionUtils.buildCronExpression(any(), any()))
          .thenReturn(null);
      this.notificationReminderScheduleHandler.execute(context);
      verify(this.notificationReminderScheduleHandler).execute(context);
    }
  }

  @Test
  @Order(2)
  @DisplayName("[Notification Reminder Scheduler] Set schedule fail")
  void setScheduleFail() {
    // given
    this.setExpire();
    this.notificationReminderScheduleHandler.execute(context);
    verify(this.notificationReminderScheduleHandler).execute(context);
  }

  @SneakyThrows
  @Test
  @Order(1)
  @DisplayName("[Notification Reminder Scheduler] Update-schedule")
  void setUpdateSchedule() {
    // given
    this.setExpire();
    context.put(SignProcessConstant.NEW_EXPIRE_DATE, DateUtil.plushDays(new Date(), 6));

    // when
    when(this.schedulerHandler.getTrigger(any())).thenReturn(TriggerBuilder.newTrigger().build());

    this.notificationReminderScheduleHandler.execute(context);
    verify(this.notificationReminderScheduleHandler, times(1)).execute(context);

    // when
    when(this.schedulerHandler.getTrigger(any())).thenReturn(null);

    this.notificationReminderScheduleHandler.execute(context);
    verify(this.notificationReminderScheduleHandler, times(2)).execute(context);
  }
}
