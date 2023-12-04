package com.innovationandtrust.process.chain.execution.approve;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApprovingCoSignExecutionManager extends ExecutionManager {
  private final ApprovingProcessHandler approvingProcessHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final ParticipantUnorderedInvitationHandler unorderedInvitationHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;

  public ApprovingCoSignExecutionManager(
      ApprovingProcessHandler approvingProcessHandler,
      ProjectWebHookHandler projectWebHookHandler,
      ParticipantUnorderedInvitationHandler unorderedInvitationHandler,
      JsonFileProcessHandler jsonFileProcessHandler) {
    this.approvingProcessHandler = approvingProcessHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.unorderedInvitationHandler = unorderedInvitationHandler;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            approvingProcessHandler,
            projectWebHookHandler,
            unorderedInvitationHandler,
            jsonFileProcessHandler));
  }
}
