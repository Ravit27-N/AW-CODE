package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningInfoHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SigningInfoExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final SigningInfoHandler signingInfoHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(List.of(jsonFileProcessHandler, signingInfoHandler, jsonFileProcessHandler));
  }
}