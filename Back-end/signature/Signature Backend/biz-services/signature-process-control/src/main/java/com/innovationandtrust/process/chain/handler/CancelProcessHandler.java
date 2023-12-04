package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.constant.ProjectEventConstant;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import lombok.extern.slf4j.Slf4j;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

/**
 * This class is about process canceling current active project.
 */
@Slf4j
@Component
public class CancelProcessHandler extends AbstractExecutionHandler {
  private static final String EXPIRE = "EXPIRE-";
  private final SchedulerHandler schedulerHandler;

  public CancelProcessHandler(SchedulerHandler schedulerHandler) {
    this.schedulerHandler = schedulerHandler;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    this.unScheduleJob(project);
    project.setStatus(ProjectStatus.ABANDON.name());
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.WEBHOOK_EVENT, ProjectEventConstant.PROJECT_CANCELED);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void unScheduleJob(Project project) {
    var triggerKey =
        TriggerKey.triggerKey(
            project.getFlowId(), EXPIRE + project.getTemplate().getSignProcess().name());
    log.info("Un-scheduling project expired:{}", project.getFlowId());
    this.schedulerHandler.unScheduledJob(triggerKey);
    log.info("Successfully un-schedule project expired:{}", project.getFlowId());

    triggerKey =
        TriggerKey.triggerKey(project.getFlowId(), project.getTemplate().getSignProcess().name());
    log.info("Un-scheduling project reminder:{}", project.getFlowId());
    this.schedulerHandler.unScheduledJob(triggerKey);
    log.info("Successfully un-schedule project reminder:{}", project.getFlowId());
  }
}
