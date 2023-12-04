package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompleteSignProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final DocumentProcessingHandler documentProcessingHandler;
  private final ProjectWebHookHandler projectWebHookHandler;

  public CompleteSignProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      DocumentProcessingHandler documentProcessingHandler,
      ProjectWebHookHandler projectWebHookHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.documentProcessingHandler = documentProcessingHandler;
    this.projectWebHookHandler = projectWebHookHandler;
  }

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(
            jsonFileProcessHandler,
            documentProcessingHandler,
            projectWebHookHandler,
            jsonFileProcessHandler));
  }
}
