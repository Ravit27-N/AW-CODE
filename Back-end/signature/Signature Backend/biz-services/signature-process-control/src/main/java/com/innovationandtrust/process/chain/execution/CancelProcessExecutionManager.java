package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CancelProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final CancelProcessHandler cancelProcessHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(jsonFileProcessHandler, cancelProcessHandler, jsonFileProcessHandler));
  }
}
