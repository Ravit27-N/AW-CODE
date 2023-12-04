package com.innovationandtrust.process.chain.execution.reminder;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderLauncherHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SendReminderProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final NotificationReminderLauncherHandler sendReminderHandler;

  public SendReminderProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      NotificationReminderLauncherHandler sendReminderHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.sendReminderHandler = sendReminderHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(jsonFileProcessHandler, sendReminderHandler, jsonFileProcessHandler));
  }
}
