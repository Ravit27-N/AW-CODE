package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.RecipientRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipientHandler extends AbstractExecutionHandler {
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final ProjectFeignClient projectFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    this.recipientProcess(project, uuid);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void recipientProcess(Project project, String uuid) {
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            participant -> {
              this.apiNgFeignClient.completeSignProcess(
                  project.getSessionId(),
                  new RecipientRequest(
                      participant.getActorUrl(),
                      project.getDocumentUrls(),
                      RoleConstant.ROLE_API_NG_RECEIPT));
              this.projectFeignClient.updateProjectAfterSigned(
                  new ProjectAfterSignRequest(
                      new SignatoryRequest(participant.getId(), DocumentStatus.RECEIVED),
                      List.of()));
              participant.setReceived(true);
            });
  }
}
