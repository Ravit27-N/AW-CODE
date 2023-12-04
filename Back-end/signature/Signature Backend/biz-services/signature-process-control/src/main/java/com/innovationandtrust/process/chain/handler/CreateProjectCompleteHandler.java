package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** This class about surely completed creating project. */
@Slf4j
@Component
public class CreateProjectCompleteHandler extends AbstractExecutionHandler {

  private final ProjectFeignClient projectFeignClient;

  public CreateProjectCompleteHandler(ProjectFeignClient projectFeignClient) {
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    // Insert the flowId, means the project war created completely.
    // And this request update the project status to in-progress
    this.projectFeignClient.insertProjectUuid(project.getId(), project.getFlowId());

    project.setStatus(ProjectStatus.IN_PROGRESS.name());
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.PROJECT_CREATED);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }
}
