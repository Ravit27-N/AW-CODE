package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderLauncherHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NotificationReminderExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final NotificationReminderLauncherHandler reminderLauncherHandler;

  public NotificationReminderExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      NotificationReminderLauncherHandler reminderLauncherHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.reminderLauncherHandler = reminderLauncherHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(jsonFileProcessHandler, reminderLauncherHandler));
  }
}
