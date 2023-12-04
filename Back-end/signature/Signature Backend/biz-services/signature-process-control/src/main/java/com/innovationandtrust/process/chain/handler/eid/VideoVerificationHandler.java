package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.eid.model.VideoIDVerificationDto;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VideoVerificationHandler extends AbstractExecutionHandler {

  private final EIDServiceProvider eIDServiceProvider;
  private final ProjectFeignClient projectFeignClient;

  public VideoVerificationHandler(
      EIDServiceProvider eIDServiceProvider, ProjectFeignClient projectFeignClient) {
    this.eIDServiceProvider = eIDServiceProvider;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {

    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    final String videoId = context.get(SignProcessConstant.VIDEO_ID, String.class);

    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            (Participant participant) -> {
              this.validateVideoId(participant.getVideoId());

              final VideoIDVerificationDto response =
                  this.eIDServiceProvider.requestVerificationVideoID(videoId);
              this.projectFeignClient.updateVideoId(participant.getId(), videoId);
              context.put(SignProcessConstant.VIDEOID_VERIFICATION, response);

              participant.setVideoId(videoId);
              participant.setVideoVerifiedStatus(ProcessStatus.EID_IN_PROGRESS);
            });

    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);

    return ExecutionState.NEXT;
  }

  private void validateVideoId(String videoId) {
    if (Strings.isNotBlank(videoId)) {
      log.error("[VideoVerificationHandler] request verification failed.");
      throw new BadRequestException("Video has already requested to be verified.");
    }
  }
}
