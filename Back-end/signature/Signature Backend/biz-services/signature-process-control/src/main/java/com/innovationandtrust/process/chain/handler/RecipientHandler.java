package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.RecipientRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** This class handle on invitation recipients to accept project signed. */
@Slf4j
@Component
public class RecipientHandler extends AbstractExecutionHandler {
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final ProjectFeignClient projectFeignClient;

  public RecipientHandler(
      ApiNgFeignClientFacade apiNgFeignClient, ProjectFeignClient projectFeignClient) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    this.recipientProcess(project, uuid);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.ACCEPTED_DOCUMENT);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void recipientProcess(Project project, String uuid) {
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            (Participant participant) -> {
              participant.setReceived(true);
              participant.setActionedDate(Date.from(Instant.now()));
              this.completeSignProcess(
                  project.getSessionId(), participant.getActorUrl(), project.getDocumentUrls());
              this.updateStatus(participant.getId());
            });
  }

  private void completeSignProcess(Long sessionId, String actorUrl, List<String> docUrls) {
    this.apiNgFeignClient.completeSignProcess(
        sessionId, new RecipientRequest(actorUrl, docUrls, RoleConstant.ROLE_API_NG_RECEIPT));
  }

  private void updateStatus(Long id) {
    this.projectFeignClient.updateProjectAfterSigned(
        new ProjectAfterSignRequest(new SignatoryRequest(id, DocumentStatus.RECEIVED), List.of()));
  }
}
