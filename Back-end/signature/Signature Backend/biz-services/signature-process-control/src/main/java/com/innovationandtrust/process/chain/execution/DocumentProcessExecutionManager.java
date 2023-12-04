package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DocumentProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final DocumentProcessingHandler documentProcessingHandler;

  public DocumentProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      DocumentProcessingHandler documentProcessingHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.documentProcessingHandler = documentProcessingHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(jsonFileProcessHandler, documentProcessingHandler));
  }
}
