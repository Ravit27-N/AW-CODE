package com.innovationandtrust.process.chain.handler.expired;

import static com.innovationandtrust.utils.commons.CommonUsages.convertToList;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateProjectStatusHandler extends AbstractExecutionHandler {
  @Override
  public ExecutionState execute(ExecutionContext context) {
    final var status = context.get(SignProcessConstant.STATUS, String.class);
    var projects = convertToList(SignProcessConstant.PROJECTS, Project.class);

    projects.forEach(project -> project.setStatus(status));

    context.put(SignProcessConstant.PROJECTS, projects);
    context.put(
        SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE_MULTIPLE);
    return ExecutionState.NEXT;
  }
}
