package com.innovationandtrust.process.chain.execution.approve;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ApprovingCounterSignExecutionManager extends ExecutionManager {
  private final ApprovingProcessHandler approvingProcessHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final ParticipantOrderInvitationHandler participantOrderInvitationHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;

  public ApprovingCounterSignExecutionManager(
      ApprovingProcessHandler approvingProcessHandler,
      ProjectWebHookHandler projectWebHookHandler,
      ParticipantOrderInvitationHandler participantOrderInvitationHandler,
      JsonFileProcessHandler jsonFileProcessHandler) {
    this.approvingProcessHandler = approvingProcessHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.participantOrderInvitationHandler = participantOrderInvitationHandler;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            approvingProcessHandler,
            projectWebHookHandler,
            participantOrderInvitationHandler,
            jsonFileProcessHandler));
  }
}
