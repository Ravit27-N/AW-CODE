package com.innovationandtrust.process.chain.handler.sign;

import static com.innovationandtrust.process.utils.ProcessControlUtils.getFilename;

import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Updating participant document status to be SIGNING. If any error, update document status back to
 * IN_PROGRESS
 */
@Slf4j
@Component
public class UpdateToProcessingHandler extends AbstractExecutionHandler {
  private final FileProvider fileProvider;
  private final ProjectFeignClient projectFeignClient;

  public UpdateToProcessingHandler(
      FileProvider fileProvider, ProjectFeignClient projectFeignClient) {
    this.fileProvider = fileProvider;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var status = context.get(SignProcessConstant.PARTICIPANT_DOCUMENT_STATUS, DocumentStatus.class);

    if (Objects.nonNull(status)) {
      var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
      final var filename = getFilename(project.getFlowId());
      final var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);

      // To read original
      var originalProject =
          this.fileProvider.readJson(filename, PathConstant.FILE_CONTROL_PATH, Project.class);

      var foundParticipant = originalProject.getParticipantByUuid(uuid);

      if (foundParticipant.isPresent()) {
        var participant = foundParticipant.get();

        participant.setProcessing(Objects.equals(status, DocumentStatus.IN_SIGNING));
        this.updateDocumentStatus(participant.getId(), status);
      } else {
        return ExecutionState.END;
      }

      this.fileProvider.updateJson(
          originalProject, filename, PathConstant.FILE_CONTROL_PATH, Project.class);
    }

    return ExecutionState.NEXT;
  }

  private void updateDocumentStatus(Long id, DocumentStatus status) {
    Executors.newSingleThreadExecutor()
        .execute(() -> this.projectFeignClient.updateDocumentStatus(id, status.name()));
  }
}
