package com.innovationandtrust.process.chain.execution.eid;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.eid.RequestToSignHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EIDRequestToSignExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final RequestToSignHandler requestToSignHandler;

  public EIDRequestToSignExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler, RequestToSignHandler requestToSignHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.requestToSignHandler = requestToSignHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(jsonFileProcessHandler, requestToSignHandler, jsonFileProcessHandler));
  }
}
