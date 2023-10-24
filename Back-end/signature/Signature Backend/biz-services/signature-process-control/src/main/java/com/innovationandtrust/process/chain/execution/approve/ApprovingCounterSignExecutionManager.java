package com.innovationandtrust.process.chain.execution.approve;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApprovingCounterSignExecutionManager extends ExecutionManager {

  private final ApprovingProcessHandler approvingProcessHandler;

  private final ParticipantOrderInvitationHandler participantOrderInvitationHandler;

  private final JsonFileProcessHandler jsonFileProcessHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            approvingProcessHandler, participantOrderInvitationHandler, jsonFileProcessHandler));
  }
}
