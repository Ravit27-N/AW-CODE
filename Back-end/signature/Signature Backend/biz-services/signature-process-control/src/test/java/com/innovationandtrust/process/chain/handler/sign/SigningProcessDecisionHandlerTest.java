package com.innovationandtrust.process.chain.handler.sign;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.sign.CoSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.CounterSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.IndividualSignProcessExecutionManager;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SigningProcessDecisionHandlerTest {
  private SigningProcessDecisionHandler signingProcessDecisionHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setUp() {
    signingProcessDecisionHandler =
        spy(
            new SigningProcessDecisionHandler(
                mock(CounterSignProcessExecutionManager.class),
                mock(CoSignProcessExecutionManager.class),
                mock(IndividualSignProcessExecutionManager.class)));
    context = UnitTestProvider.getContext();
  }

  @Test
  @Order(1)
  @DisplayName("Get sign info handler test")
  void execute() {
    this.signingProcessDecisionHandler.execute(context);

    verify(this.signingProcessDecisionHandler, times(1)).execute(context);
  }
}
