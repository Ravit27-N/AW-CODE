package com.innovationandtrust.process.chain.handler.approve;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.approve.ApprovingCoSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingCounterSignExecutionManager;
import com.innovationandtrust.process.chain.execution.approve.ApprovingIndividualSignExecutionManager;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class ApprovingDecisionHandlerTest {
  private ApprovingDecisionHandler approvingDecisionHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    approvingDecisionHandler =
        spy(
            new ApprovingDecisionHandler(
                mock(ApprovingCounterSignExecutionManager.class),
                mock(ApprovingCoSignExecutionManager.class),
                mock(ApprovingIndividualSignExecutionManager.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ApprovingDecisionHandler]")
  void testExecute() {
    // given
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);

    this.approvingDecisionHandler.execute(context);
    verify(this.approvingDecisionHandler).execute(context);
  }
}
