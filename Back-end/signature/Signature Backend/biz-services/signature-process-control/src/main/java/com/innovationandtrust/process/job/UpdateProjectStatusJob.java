package com.innovationandtrust.process.job;

import com.innovationandtrust.process.chain.execution.expired.ProjectExpiredExecutionManager;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UpdateProjectStatusJob extends QuartzJobBean implements InterruptableJob {

  private ProjectExpiredExecutionManager updateProjectExecutionManager;

  @Autowired
  public void setUpdateProjectExecutionManager(
      ProjectExpiredExecutionManager updateProjectExecutionManager) {
    this.updateProjectExecutionManager = updateProjectExecutionManager;
  }

  @Override
  public void interrupt() {
    log.info("Job interrupted");
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    log.info("Quartz schedule start ...");
    var flowId = context.getMergedJobDataMap().getString(SignProcessConstant.FLOW_ID);
    var group = context.getMergedJobDataMap().getString(SignProcessConstant.JOB_GROUP);
    if (Objects.nonNull(flowId) && Objects.nonNull(group)) {
      var ctx = new ExecutionContext();
      ctx.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
      ctx.put(SignProcessConstant.JOB_GROUP, group);
      this.updateProjectExecutionManager.execute(ctx);
    }
  }
}
