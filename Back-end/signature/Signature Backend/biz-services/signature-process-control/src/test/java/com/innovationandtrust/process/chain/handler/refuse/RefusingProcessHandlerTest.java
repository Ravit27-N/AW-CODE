package com.innovationandtrust.process.chain.handler.refuse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
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
class RefusingProcessHandlerTest {
  private RefusingProcessHandler refusingProcessHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    refusingProcessHandler =
        spy(
            new RefusingProcessHandler(
                mock(TemplateEngine.class),
                mock(ProjectFeignClient.class),
                mock(CorporateProfileFeignClient.class),
                mock(ProfileFeignClient.class),
                mock(SchedulerHandler.class),
                mock(NotificationFeignClient.class),
                mock(ApiNgFeignClientFacade.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[RefusingProcessHandler]")
  void testExecute() {

    this.refusingProcessHandler.execute(context);
    verify(this.refusingProcessHandler).execute(context);
  }
}
