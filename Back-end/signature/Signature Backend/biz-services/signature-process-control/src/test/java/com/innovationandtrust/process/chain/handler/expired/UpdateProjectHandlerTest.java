package com.innovationandtrust.process.chain.handler.expired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
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
class UpdateProjectHandlerTest {
  private UpdateProjectHandler updateProjectHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    updateProjectHandler =
        spy(
            new UpdateProjectHandler(
                mock(ApiNgFeignClientFacade.class),
                mock(CorporateProfileFeignClient.class),
                mock(TemplateEngine.class),
                mock(ProcessControlProperty.class),
                mock(ImpersonateTokenService.class),
                mock(NotificationFeignClient.class),
                mock(SettingProperties.class),
                mock(ProjectFeignClient.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[ProjectExpiredScheduleHandler]")
  void testExecute() {
    // given
    context.put(SignProcessConstant.NEW_EXPIRE_DATE, UnitTestProvider.getDateTime(5));

    this.updateProjectHandler.execute(context);
    verify(this.updateProjectHandler).execute(context);
  }
}
