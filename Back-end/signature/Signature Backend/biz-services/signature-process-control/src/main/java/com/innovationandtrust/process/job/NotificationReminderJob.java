package com.innovationandtrust.process.job;

import com.innovationandtrust.process.chain.execution.NotificationReminderExecutionManager;
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
public class NotificationReminderJob extends QuartzJobBean implements InterruptableJob {

  private NotificationReminderExecutionManager reminderExecutionManager;

  @Autowired
  public void setReminderExecutionManager(
      NotificationReminderExecutionManager reminderExecutionManager) {
    this.reminderExecutionManager = reminderExecutionManager;
  }

  @Override
  public void interrupt() {
    log.info("Job interrupted");
  }

  @Override
  protected void executeInternal(JobExecutionContext context) {
    log.info("Quartz schedule start ...");
    var reminderJob = context.getMergedJobDataMap().getString(SignProcessConstant.FLOW_ID);
    if (Objects.nonNull(reminderJob)) {
      var ctx = new ExecutionContext();
      ctx.put(SignProcessConstant.PROJECT_KEY, new Project(reminderJob));
      this.reminderExecutionManager.execute(ctx);
    }
  }
}
