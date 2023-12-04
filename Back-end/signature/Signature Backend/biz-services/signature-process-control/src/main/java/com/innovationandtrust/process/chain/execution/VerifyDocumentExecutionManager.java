package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.VerificationProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VerifyDocumentExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final VerificationProcessHandler verificationProcessHandler;

  public VerifyDocumentExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      VerificationProcessHandler verificationProcessHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.verificationProcessHandler = verificationProcessHandler;
  }

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(jsonFileProcessHandler, verificationProcessHandler, jsonFileProcessHandler));
  }
}
