package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningInfoHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SigningInfoExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final SigningInfoHandler signingInfoHandler;

  public SigningInfoExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler, SigningInfoHandler signingInfoHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.signingInfoHandler = signingInfoHandler;
  }

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(List.of(jsonFileProcessHandler, signingInfoHandler, jsonFileProcessHandler));
  }
}
