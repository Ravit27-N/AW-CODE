package com.innovationandtrust.process.chain.handler.expired;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.job.UpdateProjectStatusJob;
import com.innovationandtrust.process.model.UpdateProjectStatusJobData;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.schedule.handler.SchedulerHandler;
import com.innovationandtrust.utils.schedule.model.JobDetailDescriptor;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectExpiredScheduleHandler extends AbstractExecutionHandler {

  public static final String EXPIRE = "EXPIRE-";
  public static final String URGENT = "URGENT-";

  private final SettingProperties settingProperties;

  private final SchedulerHandler schedulerHandler;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var newExpireDate = context.get(SignProcessConstant.NEW_EXPIRE_DATE, Date.class);

    if (!Objects.nonNull(newExpireDate)) {
      this.setScheduler(project, EXPIRE);
    } else {
      this.updateScheduler(project, EXPIRE);
    }

    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void setScheduler(Project project, String type) {
    try {
      log.info("Setting schedule for update {}project: {} ", type, project.getFlowId());

      this.schedulerHandler.scheduleJob(getTrigger(project, type), getJob(project, type));
    } catch (SchedulerException e) {
      String message = "Failed to set schedule...";
      log.error(message, e);
      throw new IllegalArgumentException(message);
    }
  }

  private void updateScheduler(Project project, String type) {
    try {
      var triggerKey =
          TriggerKey.triggerKey(
              project.getFlowId(), type + project.getTemplate().getSignProcess().name());

      if (Objects.nonNull(this.schedulerHandler.getTrigger(triggerKey))) {
        log.info("Updating schedule for update {}project: {} ", type, project.getFlowId());

        this.schedulerHandler.updateScheduleJob(triggerKey, getTrigger(project, type));
      } else {
        log.error(
            "No trigger found to update with identity: {}{} ",
            triggerKey.getGroup(),
            triggerKey.getName());
      }
    } catch (SchedulerException e) {
      String message = "Failed to update schedule...";
      log.error(message, e);
      throw new IllegalArgumentException(message);
    }
  }

  private JobDetailDescriptor getJob(Project project, String type) {
    var jobDetail =
        new JobDetailDescriptor(
            project.getFlowId(),
            type + project.getTemplate().getSignProcess().name(),
            UpdateProjectStatusJob.class);
    jobDetail.setData(new UpdateProjectStatusJobData(project.getFlowId(), type));
    return jobDetail;
  }

  private SimpleTrigger getTrigger(Project project, String type) {
    var trigger =
        TriggerBuilder.newTrigger()
            .withIdentity(project.getFlowId(), type + project.getTemplate().getSignProcess().name())
            .startAt(project.getDetail().getExpireDate())
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0));
    if (Objects.equals(type, URGENT)) {
      var urgentTime = settingProperties.getUrgentProject();
      log.info("Setting project to be urgent {}h before expiration date", urgentTime);
      var calendar = Calendar.getInstance();
      calendar.setTime(project.getDetail().getExpireDate());
      calendar.add(Calendar.HOUR, -urgentTime);
      trigger.startAt(calendar.getTime());
    }
    return trigger.build();
  }

  private boolean checkUrgent(Project project) {
    var calendar = Calendar.getInstance();
    calendar.setTime(project.getDetail().getExpireDate());
    calendar.add(Calendar.HOUR, -settingProperties.getUrgentProject());
    // If expiration date less than urgent project setting hours
    return !Objects.equals(ProjectStatus.URGENT.name(), project.getStatus())
        && (new Date()).before(calendar.getTime());
  }
}
