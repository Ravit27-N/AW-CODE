package com.innovationandtrust.process.chain.execution.sign;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterSignProcessExecutionManager extends ExecutionManager {

  private final SigningProcessHandler signingProcessHandler;
  private final ParticipantOrderInvitationHandler participantOrderInvitationHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(signingProcessHandler, participantOrderInvitationHandler, jsonFileProcessHandler));
  }
}
