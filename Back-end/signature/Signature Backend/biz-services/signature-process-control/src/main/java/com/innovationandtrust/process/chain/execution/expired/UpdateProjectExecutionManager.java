package com.innovationandtrust.process.chain.execution.expired;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.process.chain.handler.expired.UpdateProjectHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UpdateProjectExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final UpdateProjectHandler updateProjectHandler;
  private final ProjectExpiredScheduleHandler expiredProjectScheduleHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final NotificationReminderScheduleHandler reminderScheduleHandler;

  public UpdateProjectExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      UpdateProjectHandler updateProjectHandler,
      ProjectExpiredScheduleHandler expiredProjectScheduleHandler,
      ProjectWebHookHandler projectWebHookHandler,
      NotificationReminderScheduleHandler reminderScheduleHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.updateProjectHandler = updateProjectHandler;
    this.expiredProjectScheduleHandler = expiredProjectScheduleHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.reminderScheduleHandler = reminderScheduleHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            updateProjectHandler,

            // re-schedule reminder
            reminderScheduleHandler,

            // re-schedule update project when expired
            expiredProjectScheduleHandler,
            projectWebHookHandler,
            jsonFileProcessHandler));
  }
}
