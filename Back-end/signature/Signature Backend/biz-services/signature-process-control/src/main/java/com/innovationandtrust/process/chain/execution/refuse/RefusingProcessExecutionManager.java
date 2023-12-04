package com.innovationandtrust.process.chain.execution.refuse;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.GetUserInfoHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.refuse.RefusingProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RefusingProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final RefusingProcessHandler refusingProcessHandler;
  private final ProjectWebHookHandler projectWebHookHandler;
  private final GetUserInfoHandler getUserInfoHandler;
  private final CompleteSigningProcessHandler completeSigningProcessHandler;

  public RefusingProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      RefusingProcessHandler refusingProcessHandler,
      ProjectWebHookHandler projectWebHookHandler,
      GetUserInfoHandler getUserInfoHandler,
      CompleteSigningProcessHandler completeSigningProcessHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.refusingProcessHandler = refusingProcessHandler;
    this.projectWebHookHandler = projectWebHookHandler;
    this.getUserInfoHandler = getUserInfoHandler;
    this.completeSigningProcessHandler = completeSigningProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            getUserInfoHandler,
            refusingProcessHandler,
            projectWebHookHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
