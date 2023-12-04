package com.innovationandtrust.process.chain.execution;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.reminder.SendReminderProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderLauncherHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class NotificationProcessingExecutionManagerTest {

  @Test
  @DisplayName("Send Reminder Process Execution Manager Test")
  void sendReminderProcessExecutionManager() {
    SendReminderProcessExecutionManager sendReminderProcessExecutionManager =
        spy(
            new SendReminderProcessExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(NotificationReminderLauncherHandler.class)));

    sendReminderProcessExecutionManager.afterPropertiesSet();
    verify(sendReminderProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Notification Reminder Execution Manager Test")
  void notificationReminderExecutionManager() {
    NotificationReminderExecutionManager notificationReminderExecutionManager =
        spy(
            new NotificationReminderExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(NotificationReminderLauncherHandler.class)));

    notificationReminderExecutionManager.afterPropertiesSet();
    verify(notificationReminderExecutionManager, times(1)).afterPropertiesSet();
  }
}
