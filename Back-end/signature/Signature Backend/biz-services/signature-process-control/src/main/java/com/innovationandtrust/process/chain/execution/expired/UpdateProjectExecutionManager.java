package com.innovationandtrust.process.chain.execution.expired;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.process.chain.handler.expired.UpdateProjectHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateProjectExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final UpdateProjectHandler updateProjectHandler;

  private final ProjectExpiredScheduleHandler expiredProjectScheduleHandler;

  private final NotificationReminderScheduleHandler reminderScheduleHandler;

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
            jsonFileProcessHandler));
  }
}
