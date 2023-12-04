package com.innovationandtrust.process.chain.handler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class CancelProcessHandlerTest {
  private CancelProcessHandler cancelProcessHandler;
  @Mock private SchedulerHandler schedulerHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {
    cancelProcessHandler = spy(new CancelProcessHandler(schedulerHandler));
    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("Cancel project")
  void downloadManifest() {
    this.cancelProcessHandler.execute(context);
    verify(this.cancelProcessHandler).execute(context);
  }
}
