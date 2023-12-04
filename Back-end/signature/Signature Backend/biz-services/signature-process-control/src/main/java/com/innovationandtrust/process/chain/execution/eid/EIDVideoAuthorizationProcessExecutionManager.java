package com.innovationandtrust.process.chain.execution.eid;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoAuthorizationHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EIDVideoAuthorizationProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final VideoAuthorizationHandler videoAuthorizationHandler;

  public EIDVideoAuthorizationProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      VideoAuthorizationHandler videoAuthorizationHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.videoAuthorizationHandler = videoAuthorizationHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(jsonFileProcessHandler, videoAuthorizationHandler, jsonFileProcessHandler));
  }
}
