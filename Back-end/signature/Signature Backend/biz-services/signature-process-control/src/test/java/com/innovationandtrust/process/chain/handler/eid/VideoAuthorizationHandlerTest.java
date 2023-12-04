package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.eid.model.VideoIDAuthorizationDto;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class VideoAuthorizationHandlerTest {

  private VideoAuthorizationHandler videoAuthorizationHandler;
  @Mock private EIDServiceProvider eIDServiceProvider;
  private ExecutionContext context;
  private VideoIDAuthorizationDto videoIDAuthorizationDto;

  @BeforeEach
  public void setUp() {

    videoAuthorizationHandler = spy(new VideoAuthorizationHandler(eIDServiceProvider));
    context = UnitTestProvider.getContext();
    videoIDAuthorizationDto = UnitTestProvider.getVideoIDAuthorizationDto();
  }

  @Test
  @DisplayName("Video Authorization Process Handler")
  void testVideoAuthorizationHandler() {
    when(this.eIDServiceProvider.requestVideoIDAuthentication())
        .thenReturn(videoIDAuthorizationDto);
    this.videoAuthorizationHandler.execute(context);
    verify(this.videoAuthorizationHandler, times(1)).execute(context);
  }
}
