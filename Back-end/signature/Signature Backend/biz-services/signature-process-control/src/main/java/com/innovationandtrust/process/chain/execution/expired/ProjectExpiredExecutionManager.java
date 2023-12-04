package com.innovationandtrust.process.chain.execution.expired;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredLauncherHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProjectExpiredExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final ProjectExpiredLauncherHandler updateProjectLauncherHandler;
  private final ProjectWebHookHandler projectWebHookHandler;

  public ProjectExpiredExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      ProjectExpiredLauncherHandler updateProjectLauncherHandler,
      ProjectWebHookHandler projectWebHookHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.updateProjectLauncherHandler = updateProjectLauncherHandler;
    this.projectWebHookHandler = projectWebHookHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            updateProjectLauncherHandler,
            projectWebHookHandler,
            jsonFileProcessHandler));
  }
}
