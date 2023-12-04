package com.innovationandtrust.process.chain.execution;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.expired.ProjectExpiredExecutionManager;
import com.innovationandtrust.process.chain.execution.expired.UpdateProjectExecutionManager;
import com.innovationandtrust.process.chain.execution.refuse.RefusingProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.CancelProcessHandler;
import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.GetUserInfoHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.approve.ReadProcessHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredLauncherHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.process.chain.handler.expired.UpdateProjectHandler;
import com.innovationandtrust.process.chain.handler.refuse.RefusingProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UpdateProjectProcessingExecutionManagerTest {
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ProjectExpiredLauncherHandler updateProjectLauncherHandler;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);
  }

  @Test
  @DisplayName("Project Expired Execution Manager Test")
  void projectExpiredExecutionManager() {
    ProjectExpiredExecutionManager projectExpiredExecutionManager =
        spy(
            new ProjectExpiredExecutionManager(
                jsonFileProcessHandler,
                updateProjectLauncherHandler,
                mock(ProjectWebHookHandler.class)));

    projectExpiredExecutionManager.afterPropertiesSet();
    verify(projectExpiredExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Update Project Execution Manager Test")
  void updateProjectExecutionManager() {
    UpdateProjectExecutionManager updateProjectExecutionManager =
        spy(
            new UpdateProjectExecutionManager(
                jsonFileProcessHandler,
                mock(UpdateProjectHandler.class),
                mock(ProjectExpiredScheduleHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(NotificationReminderScheduleHandler.class)));

    updateProjectExecutionManager.afterPropertiesSet();
    verify(updateProjectExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Refusing Process Execution Manager Test")
  void refusingProcessExecutionManager() {
    RefusingProcessExecutionManager refusingProcessExecutionManager =
        spy(
            new RefusingProcessExecutionManager(
                jsonFileProcessHandler,
                mock(RefusingProcessHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(GetUserInfoHandler.class),
                mock(CompleteSigningProcessHandler.class)));

    refusingProcessExecutionManager.afterPropertiesSet();
    verify(refusingProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Cancel Process Execution Manager Test")
  void cancelProcessExecutionManager() {
    CancelProcessExecutionManager cancelProcessExecutionManager =
        spy(
            new CancelProcessExecutionManager(
                jsonFileProcessHandler,
                mock(CancelProcessHandler.class),
                mock(ProjectWebHookHandler.class)));

    cancelProcessExecutionManager.afterPropertiesSet();
    verify(cancelProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Read Process Execution Manager Test")
  void readProcessExecutionManager() {
    ReadProcessExecutionManager readProcessExecutionManager =
        spy(
            new ReadProcessExecutionManager(
                jsonFileProcessHandler, mock(ReadProcessHandler.class)));

    readProcessExecutionManager.afterPropertiesSet();
    verify(readProcessExecutionManager, times(1)).afterPropertiesSet();
  }
}
