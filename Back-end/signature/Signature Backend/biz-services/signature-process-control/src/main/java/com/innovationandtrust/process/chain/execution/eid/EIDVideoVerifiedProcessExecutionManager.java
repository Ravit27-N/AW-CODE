package com.innovationandtrust.process.chain.execution.eid;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.eid.RefusedProjectValidateHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoVerifiedHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoVerifiedMailHandler;
import com.innovationandtrust.process.chain.handler.refuse.RefusingProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EIDVideoVerifiedProcessExecutionManager extends ExecutionManager {

  private final VideoVerifiedHandler videoApprovedHandler;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final VideoVerifiedMailHandler videoVerifiedMailHandler;
  private final RefusedProjectValidateHandler refusedProjectValidateHandler;
  private final RefusingProcessHandler refusingProcessHandler;
  private final CompleteSigningProcessHandler completeSigningProcessHandler;

  public EIDVideoVerifiedProcessExecutionManager(
      VideoVerifiedHandler videoApprovedHandler,
      JsonFileProcessHandler jsonFileProcessHandler,
      VideoVerifiedMailHandler videoVerifiedMailHandler,
      RefusedProjectValidateHandler refusedProjectValidateHandler,
      RefusingProcessHandler refusingProcessHandler,
      CompleteSigningProcessHandler completeSigningProcessHandler) {
    this.videoApprovedHandler = videoApprovedHandler;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.videoVerifiedMailHandler = videoVerifiedMailHandler;
    this.refusedProjectValidateHandler = refusedProjectValidateHandler;
    this.refusingProcessHandler = refusingProcessHandler;
    this.completeSigningProcessHandler = completeSigningProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            videoApprovedHandler,
            jsonFileProcessHandler,
            videoVerifiedMailHandler,
            jsonFileProcessHandler,
            refusedProjectValidateHandler,
            refusingProcessHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
