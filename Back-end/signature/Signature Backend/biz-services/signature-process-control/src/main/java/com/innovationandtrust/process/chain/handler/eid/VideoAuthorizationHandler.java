package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.eid.model.VideoIDAuthorizationDto;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VideoAuthorizationHandler extends AbstractExecutionHandler {

  private final EIDServiceProvider eIDServiceProvider;

  public VideoAuthorizationHandler(EIDServiceProvider eIDServiceProvider) {
    this.eIDServiceProvider = eIDServiceProvider;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {

    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    final String participantUUID = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    project
        .getParticipantByUuid(participantUUID)
        .ifPresent(
            (Participant participant) -> {
              final VideoIDAuthorizationDto response =
                  this.eIDServiceProvider.requestVideoIDAuthentication();
              context.put(SignProcessConstant.VIDEOID_AUTHORIZATION, response);

              participant.setAuthorization(response.getAuthorization());
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);

    return ExecutionState.NEXT;
  }
}
