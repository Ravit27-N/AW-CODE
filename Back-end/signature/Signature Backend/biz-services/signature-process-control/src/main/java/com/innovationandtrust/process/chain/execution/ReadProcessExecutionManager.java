package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.approve.ReadProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReadProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final ReadProcessHandler readProcessHandler;

  public ReadProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler, ReadProcessHandler readProcessHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.readProcessHandler = readProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(jsonFileProcessHandler, readProcessHandler, jsonFileProcessHandler));
  }
}
