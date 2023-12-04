package com.innovationandtrust.process.chain.handler.sign;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.SignRequest;
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
class SigningProcessHandlerTest {
  private SigningProcessHandler signingProcessHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    signingProcessHandler =
        spy(
            new SigningProcessHandler(
                mock(ApiNgFeignClientFacade.class),
                mock(ProjectFeignClient.class),
                mock(UpdateToProcessingHandler.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[SigningProcessHandler]")
  void testExecute() {
    context.put(SignProcessConstant.PARTICIPANT, UnitTestProvider.participant(context));
    context.put(SignProcessConstant.API_NG_SIGN_REQUEST, new SignRequest());

    this.signingProcessHandler.execute(context);
    verify(this.signingProcessHandler).execute(context);
  }
}
