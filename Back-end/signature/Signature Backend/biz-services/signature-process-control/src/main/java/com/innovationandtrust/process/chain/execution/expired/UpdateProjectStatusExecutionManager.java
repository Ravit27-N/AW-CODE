package com.innovationandtrust.process.chain.execution.expired;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.expired.UpdateProjectStatusHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UpdateProjectStatusExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final UpdateProjectStatusHandler updateProjectStatusHandler;

  public UpdateProjectStatusExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      UpdateProjectStatusHandler updateProjectStatusHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.updateProjectStatusHandler = updateProjectStatusHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(jsonFileProcessHandler, updateProjectStatusHandler, jsonFileProcessHandler));
  }
}
