package com.innovationandtrust.process.chain.execution.eid;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.RecipientInvitationHandler;
import com.innovationandtrust.process.chain.handler.eid.EIDSigningProcessDecisionHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EIDSigningProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final RecipientInvitationHandler recipientInvitationHandler;
  private final CompleteSigningProcessHandler completeSigningProcessHandler;
  private final EIDSigningProcessDecisionHandler eIDSigningProcessDecisionHandler;
  private final DocumentProcessingHandler documentProcessingHandler;

  public EIDSigningProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      RecipientInvitationHandler recipientInvitationHandler,
      CompleteSigningProcessHandler completeSigningProcessHandler,
      EIDSigningProcessDecisionHandler eIDSigningProcessDecisionHandler,
      DocumentProcessingHandler documentProcessingHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.recipientInvitationHandler = recipientInvitationHandler;
    this.completeSigningProcessHandler = completeSigningProcessHandler;
    this.eIDSigningProcessDecisionHandler = eIDSigningProcessDecisionHandler;
    this.documentProcessingHandler = documentProcessingHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            eIDSigningProcessDecisionHandler,
            recipientInvitationHandler,
            documentProcessingHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
