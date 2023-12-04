package com.innovationandtrust.process.chain.handler.sign;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.file.provider.FileProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class UpdateToProcessingHandlerTest {
  private UpdateToProcessingHandler updateToProcessingHandler;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    updateToProcessingHandler =
        spy(
            new UpdateToProcessingHandler(
                mock(FileProvider.class), mock(ProjectFeignClient.class)));

    context = UnitTestProvider.getContext();
  }

  @Test
  @DisplayName("[UpdateToProcessingHandler]")
  void testExecute() {
    // given
    context.put(SignProcessConstant.PARTICIPANT_DOCUMENT_STATUS, DocumentStatus.IN_SIGNING);

    this.updateToProcessingHandler.execute(context);
    verify(this.updateToProcessingHandler).execute(context);
  }
}
