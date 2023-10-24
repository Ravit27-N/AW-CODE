package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.share.constant.NotificationChannel;
import com.innovationandtrust.share.enums.NotificationReminderOption;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.job.NotificationReminderJob;
import com.innovationandtrust.process.model.NotificationReminderJobData;
import com.innovationandtrust.process.utils.CronExpressionUtils;
import com.innovationandtrust.process.utils.DateUtil;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import com.innovationandtrust.utils.schedule.model.CronTriggerDescriptor;
import com.innovationandtrust.utils.schedule.model.JobDetailDescriptor;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationReminderScheduleHandler extends AbstractExecutionHandler {

  private static final String MESSAGE = "Failed to set schedule";
  private final SchedulerHandler schedulerHandler;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var newExpireDate = context.get(SignProcessConstant.NEW_EXPIRE_DATE, Date.class);

    if (project.isAutoRemind() && !DateUtil.isOneDayExpire(project.getDetail().getExpireDate())) {
      log.info(
          "Notification reminder channel: {}",
          NotificationChannel.getByChannel(project.getReminderChannel()));
      if (!Objects.nonNull(newExpireDate)) {
        this.setSchedule(project);
      } else {
        this.updateScheduler(project);
      }
    }
    return ExecutionState.NEXT;
  }

  private void setSchedule(Project project) {
    var expression = getExpression(project);
    if (!StringUtils.hasText(expression)) {
      return;
    }
    try {
      var jobDetail = getJob(project);
      this.schedulerHandler.scheduleJob(getTrigger(project, expression, jobDetail), jobDetail);
    } catch (SchedulerException e) {
      log.error(MESSAGE, e);
      throw new IllegalArgumentException(MESSAGE);
    }
  }

  private void updateScheduler(Project project) {
    var expression = getExpression(project);
    if (!StringUtils.hasText(expression)) {
      return;
    }

    try {
      var triggerKey =
          TriggerKey.triggerKey(project.getFlowId(), project.getTemplate().getSignProcess().name());

      if (Objects.nonNull(this.schedulerHandler.getTrigger(triggerKey))) {
        log.info("Updating schedule for update project reminder: {} ", project.getFlowId());

        this.schedulerHandler.updateScheduleJob(
            triggerKey, getTrigger(project, expression, getJob(project)));
      } else {
        log.error("No trigger found to update with identity: {} ", triggerKey.getName());
      }
    } catch (SchedulerException e) {
      log.error(MESSAGE, e);
      throw new IllegalArgumentException(MESSAGE);
    }
  }

  private String getExpression(Project project) {
    // build cron expression
    var reminderOption = NotificationReminderOption.getByOption(project.getReminderOption());
    var expression =
        CronExpressionUtils.buildCronExpression(
            project.getDetail().getExpireDate(), reminderOption);

    if (StringUtils.hasText(expression)) {
      log.info("Setting schedule for {} with expression {}", reminderOption, expression);
      return expression;
    } else {
      log.warn(
          "Unable to set schedule with expire date : {} with the option of reminder: {}",
          DateUtil.toFrenchDate(project.getDetail().getExpireDate()),
          reminderOption);
      return null;
    }
  }

  private Trigger getTrigger(Project project, String expression, JobDetailDescriptor jobDetail) {
    return new CronTriggerDescriptor(
            project.getFlowId(),
            project.getTemplate().getSignProcess().name(),
            expression,
            project.getDetail().getExpireDate())
        .buildTrigger(jobDetail.buildJobDetail());
  }

  private JobDetailDescriptor getJob(Project project) {
    var jobDetail =
        new JobDetailDescriptor(
            project.getFlowId(),
            project.getTemplate().getSignProcess().name(),
            NotificationReminderJob.class);
    jobDetail.setData(new NotificationReminderJobData(project.getFlowId()));
    jobDetail.setStoreDurably(true);
    return jobDetail;
  }
}
