package com.innovationandtrust.process.chain.handler.expired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class ProjectExpiredLauncherHandlerTest {
  private ProjectExpiredLauncherHandler projectExpiredLauncherHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    projectExpiredLauncherHandler =
        spy(
            new ProjectExpiredLauncherHandler(
                mock(ProjectFeignClient.class),
                mock(NotificationFeignClient.class),
                new TemplateEngine()));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ReadProcessHandler]")
  void testExecute() {
    // given
    context.put(SignProcessConstant.JOB_GROUP, ProjectExpiredScheduleHandler.EXPIRE);

    this.projectExpiredLauncherHandler.execute(context);
    verify(this.projectExpiredLauncherHandler).execute(context);
  }
}
