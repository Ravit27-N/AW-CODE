package com.innovationandtrust.profile.chain.execution;

import com.innovationandtrust.profile.chain.handler.DuplicateHandler;
import com.innovationandtrust.profile.chain.handler.PublishHandler;
import com.innovationandtrust.profile.chain.handler.StartHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAPIFacadeExecutionManager extends ExecutionManager {
  private final DuplicateHandler duplicateHandler;
  private final StartHandler startHandler;
  private final PublishHandler publishHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(duplicateHandler, startHandler, publishHandler));
  }
}
