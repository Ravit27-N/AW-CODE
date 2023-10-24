package com.innovationandtrust.process.chain.execution.approve;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.approve.ApprovingProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovingIndividualSignExecutionManager extends ExecutionManager {

  private final ApprovingProcessHandler approvingProcessHandler;

  private final ParticipantUnorderedInvitationHandler unorderedInvitationHandler;

  private final JsonFileProcessHandler jsonFileProcessHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(approvingProcessHandler, unorderedInvitationHandler, jsonFileProcessHandler));
  }
}
