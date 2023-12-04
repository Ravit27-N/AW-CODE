package com.innovationandtrust.process.chain.handler.approve;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.ApprovalRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ApprovingProcessHandler extends AbstractExecutionHandler {

  private final ApiNgFeignClientFacade apiNgFeignClient;

  private final ProjectFeignClient projectFeignClient;

  public ApprovingProcessHandler(
      ApiNgFeignClientFacade apiNgFeignClient, ProjectFeignClient projectFeignClient) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    this.approve(project, context.get(SignProcessConstant.PARTICIPANT_ID, String.class));
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.APPROVED_DOCUMENT);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void approve(Project project, String uuid) {
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            person -> {
              this.apiNgFeignClient.approveDocuments(
                  project.getSessionId(),
                  new ApprovalRequest(
                      person.getActorUrl(),
                      project.getDocumentUrls(),
                      project.getTemplate().getApprovalProcess().getVal(),
                      person.getOtpCode()));
              this.projectFeignClient.updateProjectAfterSigned(
                  new ProjectAfterSignRequest(
                      new SignatoryRequest(person.getId(), DocumentStatus.APPROVED), List.of()));
              person.setApproved(true);
              person.setActionedDate(new Date());
            });
  }
}
