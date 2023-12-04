package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.OtpProcessingHandler;
import com.innovationandtrust.process.chain.handler.RequestSigningHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SetupIndividualSignProcessExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final RequestSigningHandler requestSigningHandler;
  private final OtpProcessingHandler otpProcessingHandler;

  public SetupIndividualSignProcessExecutionManager(
      JsonFileProcessHandler jsonFileProcessHandler,
      RequestSigningHandler requestSigningHandler,
      OtpProcessingHandler otpProcessingHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.requestSigningHandler = requestSigningHandler;
    this.otpProcessingHandler = otpProcessingHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            jsonFileProcessHandler,
            requestSigningHandler,
            otpProcessingHandler,
            jsonFileProcessHandler));
  }
}
