package com.innovationandtrust.process.chain.handler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class CreateProjectCompleteHandlerTest {

  private CreateProjectCompleteHandler createProjectCompleteHandler;
  @Mock private ProjectFeignClient projectFeignClient;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {
    createProjectCompleteHandler = spy(new CreateProjectCompleteHandler(projectFeignClient));
    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[Create project complete]")
  void createProjectComplete() {
    this.createProjectCompleteHandler.execute(context);
    verify(this.createProjectCompleteHandler).execute(context);
  }
}
