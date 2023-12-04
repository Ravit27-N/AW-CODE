package com.innovationandtrust.process.chain.execution.eid;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoVerificationHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EIDVideoVerificationProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final VideoVerificationHandler videoVerificationHandler;

  public EIDVideoVerificationProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      VideoVerificationHandler videoVerificationHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.videoVerificationHandler = videoVerificationHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(jsonFileProcessHandler, videoVerificationHandler, jsonFileProcessHandler));
  }
}
