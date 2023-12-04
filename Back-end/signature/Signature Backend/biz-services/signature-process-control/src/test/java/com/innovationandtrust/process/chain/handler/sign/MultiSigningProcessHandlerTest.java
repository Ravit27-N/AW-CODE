package com.innovationandtrust.process.chain.handler.sign;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.sign.SigningProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class MultiSigningProcessHandlerTest {
  private MultiSigningProcessHandler multiSigningProcessHandler;
  private ExecutionContext context;
  private Project project;
  private ProjectFeignClient projectFeignClient;

  @BeforeEach
  public void setup() {

    multiSigningProcessHandler =
        spy(
            new MultiSigningProcessHandler(
                mock(SigningProcessExecutionManager.class),
                mock(JsonFileProcessHandler.class),
                mock(CorporateProfileFeignClient.class),
                mock(ProjectFeignClient.class)));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  @Test
  @Order(1)
  @DisplayName("[MultiSigningProcessHandler] Fail")
  void testExecuteFail() {
    // given
    UnitTestProvider.setMultipleProjects(context);

    var exception =
        assertThrows(
            InvalidRequestException.class,
            () -> this.multiSigningProcessHandler.execute(context),
            UnitTestConstant.ASSERT_EXCEPTION);

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @Order(2)
  @DisplayName("[MultiSigningProcessHandler]")
  void testExecute() {
    // given
    project.setSignatureLevel(SignatureSettingLevel.SIMPLE.name());
    context.put(SignProcessConstant.PROJECT_KEY, project);
    UnitTestProvider.setMultipleProjects(context);

    this.multiSigningProcessHandler.execute(context);
    verify(this.multiSigningProcessHandler).execute(context);
  }
}
