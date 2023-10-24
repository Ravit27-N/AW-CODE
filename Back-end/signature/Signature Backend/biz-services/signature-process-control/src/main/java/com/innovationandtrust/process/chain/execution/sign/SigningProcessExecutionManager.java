package com.innovationandtrust.process.chain.execution.sign;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.RecipientInvitationHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessDecisionHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SigningProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final SigningProcessDecisionHandler processDecisionHandler;

  private final RecipientInvitationHandler recipientInvitationHandler;

  private final CompleteSigningProcessHandler completeSigningProcessHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(
            jsonFileProcessHandler,
            processDecisionHandler,
            recipientInvitationHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
