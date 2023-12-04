package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CancelProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CancelProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final CancelProcessHandler cancelProcessHandler;
  private final ProjectWebHookHandler projectWebHookHandler;

  public CancelProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      CancelProcessHandler cancelProcessHandler,
      ProjectWebHookHandler projectWebHookHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.cancelProcessHandler = cancelProcessHandler;
    this.projectWebHookHandler = projectWebHookHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            cancelProcessHandler,
            projectWebHookHandler,
            jsonFileProcessHandler));
  }
}
