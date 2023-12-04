package com.innovationandtrust.process.chain.handler.approve;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
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
class ReadProcessHandlerTest {
  private ReadProcessHandler readProcessHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    readProcessHandler = spy(new ReadProcessHandler(mock(ProjectFeignClient.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ReadProcessHandler]")
  void testExecute() {
    // given
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);

    this.readProcessHandler.execute(context);
    verify(this.readProcessHandler).execute(context);
  }
}
