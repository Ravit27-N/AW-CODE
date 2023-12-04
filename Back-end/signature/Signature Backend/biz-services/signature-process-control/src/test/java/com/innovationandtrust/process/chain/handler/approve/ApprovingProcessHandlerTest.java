package com.innovationandtrust.process.chain.handler.approve;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
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
class ApprovingProcessHandlerTest {
  private ApprovingProcessHandler approvingProcessHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    approvingProcessHandler =
        spy(
            new ApprovingProcessHandler(
                mock(ApiNgFeignClientFacade.class), mock(ProjectFeignClient.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ApprovingProcessHandler]")
  void testExecute() {
    // given

    this.approvingProcessHandler.execute(context);
    verify(this.approvingProcessHandler).execute(context);
  }
}
