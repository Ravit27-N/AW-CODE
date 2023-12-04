package com.innovationandtrust.process.chain.execution.eid;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.eid.EIDSignProcessHandler;
import com.innovationandtrust.process.chain.handler.eid.EIDValidateOTPSignHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EIDCounterSignDocumentExecuteManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final EIDSignProcessHandler eIDSignProcessHandler;
  private final ParticipantOrderInvitationHandler participantOrderInvitationHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final EIDValidateOTPSignHandler eIDValidateOTPSignHandler;

  public EIDCounterSignDocumentExecuteManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      EIDSignProcessHandler eIDSignProcessHandler,
      ParticipantOrderInvitationHandler participantOrderInvitationHandler,
      ProjectWebHookHandler projectWebHookHandler,
      EIDValidateOTPSignHandler eIDValidateOTPSignHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.eIDSignProcessHandler = eIDSignProcessHandler;
    this.participantOrderInvitationHandler = participantOrderInvitationHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.eIDValidateOTPSignHandler = eIDValidateOTPSignHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            eIDSignProcessHandler,
            projectWebHookHandler,
            jsonFileProcessHandler,
            eIDValidateOTPSignHandler,
            participantOrderInvitationHandler,
            jsonFileProcessHandler));
  }
}
