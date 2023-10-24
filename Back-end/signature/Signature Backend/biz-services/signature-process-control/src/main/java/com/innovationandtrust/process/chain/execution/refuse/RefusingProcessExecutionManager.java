package com.innovationandtrust.process.chain.execution.refuse;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.GetUserInfoHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.refuse.RefusingProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefusingProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final RefusingProcessHandler refusingProcessHandler;

  private final GetUserInfoHandler getUserInfoHandler;

  private final CompleteSigningProcessHandler completeSigningProcessHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            getUserInfoHandler,
            refusingProcessHandler,
            jsonFileProcessHandler,
            completeSigningProcessHandler,
            jsonFileProcessHandler));
  }
}
