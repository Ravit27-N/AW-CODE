package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.RecipientHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecipientExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final RecipientHandler recipientHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final CompleteSigningProcessHandler completeSigningProcessHandler;

  public RecipientExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      RecipientHandler recipientHandler,
      ProjectWebHookHandler projectWebHookHandler,
      CompleteSigningProcessHandler completeSigningProcessHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.recipientHandler = recipientHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.completeSigningProcessHandler = completeSigningProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            recipientHandler,
            projectWebHookHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
