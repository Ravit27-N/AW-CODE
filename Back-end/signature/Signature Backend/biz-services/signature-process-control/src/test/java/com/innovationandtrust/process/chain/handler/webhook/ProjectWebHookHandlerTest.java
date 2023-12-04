package com.innovationandtrust.process.chain.handler.webhook;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class ProjectWebHookHandlerTest {
  private ProjectWebHookHandler projectWebHookHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    projectWebHookHandler = spy(new ProjectWebHookHandler(mock(CorporateProfileFeignClient.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ProjectWebHookHandler]")
  void testExecute() {

    this.projectWebHookHandler.execute(context);
    verify(this.projectWebHookHandler).execute(context);
  }
}
