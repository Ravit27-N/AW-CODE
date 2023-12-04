package com.innovationandtrust.process.chain.execution.sign;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.sign.PrepareSignDocumentHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessHandler;
import com.innovationandtrust.process.chain.handler.sign.UpdateToProcessingHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CoSignProcessExecutionManager extends ExecutionManager {
  private final PrepareSignDocumentHandler prepareSignDocumentHandler;
  private final SigningProcessHandler signingProcessHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final ParticipantUnorderedInvitationHandler participantUnorderedInvitationHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final UpdateToProcessingHandler updateToProcessingHandler;

  public CoSignProcessExecutionManager(
      PrepareSignDocumentHandler prepareSignDocumentHandler,
      SigningProcessHandler signingProcessHandler,
      ProjectWebHookHandler projectWebHookHandler,
      ParticipantUnorderedInvitationHandler participantUnorderedInvitationHandler,
      JsonFileProcessHandler jsonFileProcessHandler,
      UpdateToProcessingHandler updateToProcessingHandler) {
    this.prepareSignDocumentHandler = prepareSignDocumentHandler;
    this.signingProcessHandler = signingProcessHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.participantUnorderedInvitationHandler = participantUnorderedInvitationHandler;
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
            participantUnorderedInvitationHandler,
            jsonFileProcessHandler));
  }
}
