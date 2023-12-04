package com.innovationandtrust.process.chain.handler;

import static com.innovationandtrust.utils.commons.CommonUsages.convertToList;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** This class about getting current project owner. */
@Slf4j
@Component
public class GetUserInfoHandler extends AbstractExecutionHandler {
  private final ProjectFeignClient projectFeignClient;

  public GetUserInfoHandler(ProjectFeignClient projectFeignClient) {
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    if (Objects.nonNull(project) && Objects.isNull(project.getAssignedTo())) {
      final var foundProject = this.projectFeignClient.findExternalById(project.getId());
      project.setAssignedTo(foundProject.getAssignedTo());
      context.put(SignProcessConstant.PROJECT_KEY, project);
      return ExecutionState.NEXT;
    }

    final var requestsObj = context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS);
    if (Objects.nonNull(requestsObj)) {
      var projects = convertToList(context.get(SignProcessConstant.PROJECTS), Project.class);
      var projectIds =
          projects.stream()
              .filter(p -> Objects.isNull(p.getAssignedTo()))
              .map(Project::getId)
              .toList();

      if (!projectIds.isEmpty()) {
        final var foundProjects = this.projectFeignClient.findByIds(projectIds);
        for (var p : projects) {
          foundProjects.stream()
              .filter(foundProject -> Objects.equals(foundProject.getId(), p.getId()))
              .findFirst()
              .ifPresent(foundProject -> p.setAssignedTo(foundProject.getAssignedTo()));
        }

        context.put(SignProcessConstant.PROJECTS, projects);
      }
    }

    return ExecutionState.NEXT;
  }
}
