package com.innovationandtrust.process.chain.execution.sign;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.sign.PrepareSignDocumentHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessHandler;
import com.innovationandtrust.process.chain.handler.sign.UpdateToProcessingHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CounterSignProcessExecutionManager extends ExecutionManager {

  private final PrepareSignDocumentHandler prepareSignDocumentHandler;
  private final SigningProcessHandler signingProcessHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final ParticipantOrderInvitationHandler participantOrderInvitationHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final UpdateToProcessingHandler updateToProcessingHandler;

  public CounterSignProcessExecutionManager(
      PrepareSignDocumentHandler prepareSignDocumentHandler,
      SigningProcessHandler signingProcessHandler,
      ProjectWebHookHandler projectWebHookHandler,
      ParticipantOrderInvitationHandler participantOrderInvitationHandler,
      JsonFileProcessHandler jsonFileProcessHandler,
      UpdateToProcessingHandler updateToProcessingHandler) {
    this.prepareSignDocumentHandler = prepareSignDocumentHandler;
    this.signingProcessHandler = signingProcessHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.participantOrderInvitationHandler = participantOrderInvitationHandler;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.updateToProcessingHandler = updateToProcessingHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            prepareSignDocumentHandler,
            updateToProcessingHandler,
            signingProcessHandler,
            projectWebHookHandler,
            participantOrderInvitationHandler,
            jsonFileProcessHandler));
  }
}
