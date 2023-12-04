package com.innovationandtrust.process.chain.handler.approve;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectUpdateRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class ReadProcessHandler extends AbstractExecutionHandler {

  private final ProjectFeignClient projectFeignClient;

  public ReadProcessHandler(ProjectFeignClient projectFeignClient) {
    this.projectFeignClient = projectFeignClient;
  }

  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    ProcessControlUtils.checkIsCanceled(project.getStatus());

    project
        .getParticipantByUuid(context.get(SignProcessConstant.PARTICIPANT_ID, String.class))
        .ifPresent(
            person -> {
              this.projectFeignClient.updateProjectStatusRead(
                  new ProjectUpdateRequest(
                      new SignatoryRequest(person.getId(), DocumentStatus.READ)));
              person.setApproved(true);
              person.setActionedDate(new Date());
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }
}
