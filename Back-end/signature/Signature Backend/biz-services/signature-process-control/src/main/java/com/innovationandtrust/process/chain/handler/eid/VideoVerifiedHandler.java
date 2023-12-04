package com.innovationandtrust.process.chain.handler.eid;

import static com.innovationandtrust.share.constant.ProcessStatus.eidStatus;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.SignatoryResponse;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.eid.model.VideoVerifiedDto;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VideoVerifiedHandler extends AbstractExecutionHandler {

  private final ProjectFeignClient projectFeignClient;

  public VideoVerifiedHandler(ProjectFeignClient projectFeignClient) {
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("[VideoVerifiedHandler] Processing video verified...");
    final VideoVerifiedDto videoVerified =
        context.get(SignProcessConstant.VIDEO_VERIFIED, VideoVerifiedDto.class);

    try {
      final String videoId = videoVerified.getData().getVideoId();
      final String verifiedStatus = eidStatus(videoVerified.getData().getStatus());
      final String verificationId = videoVerified.getData().getId();

      log.info("[VideoVerifiedHandler] Find a signatory.");
      final SignatoryResponse signatory = this.projectFeignClient.findSignatoryByVideoId(videoId);
      if (Objects.nonNull(signatory.getProject())) {
        final Project project =
            ProcessControlUtils.getProject(signatory.getProject().getFlowId(), signatory.getUuid())
                .get(SignProcessConstant.PROJECT_KEY, Project.class);
        context.put(SignProcessConstant.PROJECT_KEY, project);
        context.put(SignProcessConstant.PARTICIPANT_ID, signatory.getUuid());
        context.put(SignProcessConstant.SIGNATORY_ID, signatory.getId());
        context.put(SignProcessConstant.VIDEO_ID, videoId);
        context.put(SignProcessConstant.VIDEO_VERIFIED_STATUS, verifiedStatus);
        context.put(SignProcessConstant.VIDEO_VERIFICATION_ID, verificationId);
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
      }
      return ExecutionState.NEXT;
    } catch (Exception exception) {
      log.error("[VideoVerifiedHandler] processing video verified failed.", exception);
      return ExecutionState.END;
    }
  }
}
