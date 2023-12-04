package com.innovationandtrust.process.chain.handler.eid;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.eid.model.VideoIDVerificationDto;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
class VideoVerificationHandlerTest {

  private VideoVerificationHandler videoVerificationHandler;
  @Mock private EIDServiceProvider eIDServiceProvider;
  private ExecutionContext context;
  private VideoIDVerificationDto videoIDVerificationDto;

  @BeforeEach
  public void setUp() {

    videoVerificationHandler =
        spy(new VideoVerificationHandler(eIDServiceProvider, mock(ProjectFeignClient.class)));
    context = UnitTestProvider.getContext();
    videoIDVerificationDto = UnitTestProvider.getVideoIDVerificationDto();
  }

  @Test
  @DisplayName("[VideoVerificationHandler] Video Verification Process Handler")
  void testVerificationProcessHandler() {
    when(eIDServiceProvider.requestVerificationVideoID(UnitTestConstant.VIDEO_ID))
        .thenReturn(videoIDVerificationDto);
    this.videoVerificationHandler.execute(context);

    // then
    verify(this.videoVerificationHandler).execute(context);
  }

  @Test
  @DisplayName("[VideoVerificationHandler] Video Verification Exception")
  void testVideoVerificationHandlerException() {
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(participant -> participant.setVideoId(UnitTestConstant.VIDEO_ID));
    final var exception =
        assertThrows(
            BadRequestException.class,
            () -> this.videoVerificationHandler.execute(context),
            UnitTestConstant.ASSERT_EXCEPTION);
    log.info("[Exception thrown]: {}", exception.getMessage());
  }
}
