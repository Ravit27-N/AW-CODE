package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.SignatureFileHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignatureFileExecutionManager extends ExecutionManager {
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final SignatureFileHandler signatureFileHandler;

  public SignatureFileExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler, SignatureFileHandler signatureFileHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.signatureFileHandler = signatureFileHandler;
  }

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(List.of(jsonFileProcessHandler, signatureFileHandler, jsonFileProcessHandler));
  }
}
