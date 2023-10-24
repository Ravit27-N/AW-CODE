package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompleteSignProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final DocumentProcessingHandler documentProcessingHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(jsonFileProcessHandler, documentProcessingHandler));
  }
}
