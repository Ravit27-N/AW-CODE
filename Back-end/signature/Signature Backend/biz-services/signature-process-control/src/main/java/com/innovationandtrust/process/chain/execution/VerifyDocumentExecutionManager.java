package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.VerificationProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyDocumentExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final VerificationProcessHandler verificationProcessHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(jsonFileProcessHandler, verificationProcessHandler, jsonFileProcessHandler));
  }
}
