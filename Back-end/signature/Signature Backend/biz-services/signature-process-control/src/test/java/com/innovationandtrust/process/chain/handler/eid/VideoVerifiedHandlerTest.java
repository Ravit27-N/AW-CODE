package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.project.SignatoryResponse;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
class VideoVerifiedHandlerTest {

  private VideoVerifiedHandler videoVerifiedHandler;
  @Mock private ProjectFeignClient projectFeignClient;
  private ExecutionContext context;
  private SignatoryResponse signatoryResponse;

  @BeforeEach
  public void setUp() {
    videoVerifiedHandler = spy(new VideoVerifiedHandler(projectFeignClient));
    signatoryResponse = UnitTestProvider.getSignatoryResponse();
    context = UnitTestProvider.getContext();
    context.put(SignProcessConstant.VIDEO_VERIFIED, UnitTestProvider.getVideoVerified());
  }

  @Test
  @DisplayName("[VideoVerifiedHandler] Video Verified Process Handler")
  void testVideoVerifiedHandler() {
    when(this.projectFeignClient.findSignatoryByVideoId(UnitTestConstant.VIDEO_ID))
        .thenReturn(signatoryResponse);
    this.videoVerifiedHandler.execute(context);

    // then
    verify(this.videoVerifiedHandler).execute(context);
  }
}
