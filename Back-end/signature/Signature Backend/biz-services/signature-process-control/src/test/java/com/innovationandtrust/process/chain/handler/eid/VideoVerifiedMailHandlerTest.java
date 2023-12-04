package com.innovationandtrust.process.chain.handler.eid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.handler.ValidateCorporateSettingHandler;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.model.email.EmailInvitationRequest;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.eid.model.IdentityDto;
import com.innovationandtrust.utils.eid.model.VideoRecordedResponse;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith(SpringExtension.class)
class VideoVerifiedMailHandlerTest {

  private VideoVerifiedMailHandler videoVerifiedMailHandler;
  @Mock private EmailService emailService;
  @Mock private EIDServiceProvider eIDServiceProvider;
  private ExecutionContext context;
  private EmailInvitationRequest emailInvitationRequest;
  private VideoRecordedResponse videoRecordedResponse;
  private IdentityDto identityDto;

  @BeforeEach
  public void setUp() {

    videoVerifiedMailHandler =
        spy(
            new VideoVerifiedMailHandler(
                eIDServiceProvider,
                mock(TemplateEngine.class),
                emailService,
                mock(FileProvider.class),
                mock(ValidateCorporateSettingHandler.class),
                mock(ProjectFeignClient.class)));
    emailInvitationRequest = UnitTestProvider.getInvitationRequest();
    emailInvitationRequest.setRole(ProcessStatus.EID_ACCEPTED);
    identityDto = UnitTestProvider.getIdentityDto();
    videoRecordedResponse = UnitTestProvider.getVideoRecordedResponse();
    context = UnitTestProvider.getContext();
    context.put(SignProcessConstant.VIDEO_VERIFIED_STATUS, ProcessStatus.EID_ACCEPTED);
    context.put(SignProcessConstant.VIDEO_ID, UnitTestConstant.VIDEO_ID);
    context.put(SignProcessConstant.VIDEO_VERIFICATION_ID, UnitTestConstant.VERIFICATION_ID);
  }

  @Test
  @DisplayName("[VideoVerifiedMailHandler] Video Verified Mail Process Handler")
  void testVerifiedMailHandler() {
    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            participant -> {
              participant.setVideoId(UnitTestConstant.VIDEO_ID);
              when(this.emailService.prepareVideoVerifiedMail(
                      project, participant, project.getCorporateInfo()))
                  .thenReturn(emailInvitationRequest);
            });
    when(this.eIDServiceProvider.createIdentity(anyString())).thenReturn(identityDto);
    when(this.eIDServiceProvider.requestMp4OfRecordedVideoData(anyString()))
        .thenReturn("mp4".getBytes());
    when(this.eIDServiceProvider.requestRecordedVideoData(anyString()))
        .thenReturn(videoRecordedResponse);
    this.videoVerifiedMailHandler.execute(context);

    // then
    verify(this.videoVerifiedMailHandler).execute(context);
  }

  @Test
  @DisplayName("[VideoVerifiedMailHandler] Video Verified Mail Process Ejected Handler")
  void testVerifiedMailHandlerEjected() {
    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    context.put(SignProcessConstant.VIDEO_VERIFIED_STATUS, ProcessStatus.EID_REJECTED);
    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            participant -> {
              participant.setVideoId(UnitTestConstant.VIDEO_ID);
              when(this.emailService.prepareVideoVerifiedMail(
                      project, participant, project.getCorporateInfo()))
                  .thenReturn(emailInvitationRequest);
            });
    when(this.eIDServiceProvider.createIdentity(anyString())).thenReturn(identityDto);
    when(this.eIDServiceProvider.requestMp4OfRecordedVideoData(anyString()))
        .thenReturn("mp4".getBytes());
    when(this.eIDServiceProvider.requestRecordedVideoData(anyString()))
        .thenReturn(videoRecordedResponse);
    this.videoVerifiedMailHandler.execute(context);

    // then
    verify(this.videoVerifiedMailHandler).execute(context);
  }

  @Test
  @DisplayName("[VideoVerifiedMailHandler] Video Verified Mail Process Project Completed")
  void testVerifiedMailHandlerCompleted() {
    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    context.put(SignProcessConstant.VIDEO_VERIFIED_STATUS, ProcessStatus.EID_REJECTED);
    project.setStatus(ProjectStatus.COMPLETED.name());
    final var exception =
        assertThrows(
            BadRequestException.class,
            () -> this.videoVerifiedMailHandler.execute(context),
            UnitTestConstant.ASSERT_EXCEPTION);

    // then
    assertEquals(
        "Invalid action. Project is completed...",
        exception.getMessage(),
        UnitTestConstant.ASSERT_EQUALS);
  }
}
