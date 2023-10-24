package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.GetUserInfoHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.OtpProcessingHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateOTPExecutionManager extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final GetUserInfoHandler getUserInfoHandler;

  private final OtpProcessingHandler otpProcessingHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandlers(
        List.of(
            jsonFileProcessHandler,
            getUserInfoHandler,
            otpProcessingHandler,
            jsonFileProcessHandler));
  }
}
