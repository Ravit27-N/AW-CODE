package com.innovationandtrust.process.chain.execution.approve;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingDecisionHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ApprovingProcessExecutionManager extends ExecutionManager {
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final ApprovingDecisionHandler approvingDecisionHandler;

  public ApprovingProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      ApprovingDecisionHandler approvingDecisionHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.approvingDecisionHandler = approvingDecisionHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(jsonFileProcessHandler, approvingDecisionHandler));
  }
}
