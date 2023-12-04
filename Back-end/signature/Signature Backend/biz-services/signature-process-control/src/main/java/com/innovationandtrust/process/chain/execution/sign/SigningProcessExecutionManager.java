package com.innovationandtrust.process.chain.execution.sign;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.RecipientInvitationHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessDecisionHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SigningProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final SigningProcessDecisionHandler processDecisionHandler;
  private final RecipientInvitationHandler recipientInvitationHandler;
  private final CompleteSigningProcessHandler completeSigningProcessHandler;
  private final DocumentProcessingHandler documentProcessingHandler;

  public SigningProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      SigningProcessDecisionHandler processDecisionHandler,
      RecipientInvitationHandler recipientInvitationHandler,
      CompleteSigningProcessHandler completeSigningProcessHandler,
      DocumentProcessingHandler documentProcessingHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.processDecisionHandler = processDecisionHandler;
    this.recipientInvitationHandler = recipientInvitationHandler;
    this.completeSigningProcessHandler = completeSigningProcessHandler;
    this.documentProcessingHandler = documentProcessingHandler;
  }

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(
            jsonFileProcessHandler,
            processDecisionHandler,
            recipientInvitationHandler,
            documentProcessingHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
