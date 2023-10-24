package com.innovationandtrust.utils.schedule.handler;

import com.innovationandtrust.utils.schedule.constant.SchedulerConstant;
import com.innovationandtrust.utils.schedule.model.JobDetailDescriptor;
import com.innovationandtrust.utils.schedule.model.JobTriggerDescriptor;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Slf4j
public class SchedulerHandler {

  private final Scheduler scheduler;

  public SchedulerHandler(
      @Qualifier(SchedulerConstant.SCHEDULER_FACTORY_BEAN)
          SchedulerFactoryBean schedulerFactoryBean) {
    this.scheduler = schedulerFactoryBean.getScheduler();
  }

  public void scheduleJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.scheduler.scheduleJob(this.createJobTrigger(jobTriggerDescriptor));
  }

  public void scheduleJob(
      JobTriggerDescriptor jobTriggerDescriptor, JobDetailDescriptor jobDetailDescriptor)
      throws SchedulerException {
    this.scheduler.scheduleJob(
        this.createJobDetail(jobDetailDescriptor), this.createJobTrigger(jobTriggerDescriptor));
  }

  public void scheduleJob(Trigger trigger, JobDetailDescriptor jobDetailDescriptor)
      throws SchedulerException {
    this.scheduler.scheduleJob(this.createJobDetail(jobDetailDescriptor), trigger);
  }

  public void updateScheduledJob(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    this.scheduler.rescheduleJob(
        jobTriggerDescriptor.getTriggerKey(), this.createJobTrigger(jobTriggerDescriptor));
  }

  public void updateScheduleJob(TriggerKey triggerKey, Trigger trigger) throws SchedulerException {
    this.scheduler.rescheduleJob(triggerKey, trigger);
  }

  public boolean unScheduledJob(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    return this.scheduler.unscheduleJob(jobTriggerDescriptor.getTriggerKey());
  }

  public boolean unScheduledJob(TriggerKey triggerKey) {
    try {
      if (Objects.nonNull(this.getTrigger(triggerKey))) {
        return this.scheduler.unscheduleJob(triggerKey);
      } else {
        log.error(
            "No trigger found to un-schedule with identity: {}{} ",
            triggerKey.getGroup(),
            triggerKey.getName());
      }
    } catch (SchedulerException ex) {
      log.error("Error un-schedule job: ", ex);
      throw new IllegalArgumentException("Error un-schedule job");
    }

    return false;
  }

  public boolean unScheduledJob(String triggerIdentity) throws SchedulerException {
    TriggerKey triggerKey = this.findTriggerKey(triggerIdentity);

    if (triggerKey != null) {
      return this.unScheduledJob(triggerKey);
    }
    return false;
  }

  public boolean exists(JobDetailDescriptor jobDetailDescriptor) throws SchedulerException {
    return this.scheduler.checkExists(jobDetailDescriptor.getJobKey());
  }

  public boolean exists(JobKey jobKey) throws SchedulerException {
    return this.scheduler.checkExists(jobKey);
  }

  public boolean exists(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    return this.scheduler.checkExists(jobTriggerDescriptor.getTriggerKey());
  }

  public boolean exists(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.checkExists(triggerKey);
  }

  private TriggerKey findTriggerKey(String triggerIdentity) throws SchedulerException {
    return this.scheduler.getTriggerKeys(GroupMatcher.anyGroup()).stream()
        .filter(triggerKey -> triggerKey.getName().equals(triggerIdentity))
        .findFirst()
        .orElse(null);
  }

  public void pauseJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.pauseJob(jobTriggerDescriptor.getTriggerKey());
  }

  public void pauseJob(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.pauseTrigger(triggerKey);
  }

  public void resumeJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.resumeJob(jobTriggerDescriptor.getTriggerKey());
  }

  public void resumeJob(TriggerKey triggerKey) throws SchedulerException {
    this.scheduler.resumeTrigger(triggerKey);
  }

  public JobDetail buildJobDetail(JobDetailDescriptor jobDetailDescriptor) {
    return jobDetailDescriptor.buildJobDetail();
  }

  /**
   * Build a new specific {@link Trigger} described by this object for Quartz's {@link Scheduler}
   * use to fire a specified {@link Job}.
   *
   * <p>The newly created {@link Trigger} will trigger the {@link Job} described by {@link
   * JobTriggerDescriptor} using a provided {@link SimpleScheduleBuilder}.
   *
   * @return {@link Trigger}
   * @see Job * @see JobDescriptor
   * @see Trigger
   */
  public Trigger buildTrigger(JobTriggerDescriptor jobTriggerDescriptor) {
    return jobTriggerDescriptor.buildTrigger();
  }

  private JobDetail createJobDetail(JobDetailDescriptor jobDetailDescriptor)
      throws SchedulerException {
    boolean exist = this.scheduler.checkExists(jobDetailDescriptor.getJobKey());
    if (!exist) {
      return jobDetailDescriptor.buildJobDetail();
    }
    throw new ObjectAlreadyExistsException(
        this.scheduler.getJobDetail(jobDetailDescriptor.getJobKey()));
  }

  private Trigger createJobTrigger(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    boolean exist = this.scheduler.checkExists(jobTriggerDescriptor.getTriggerKey());
    if (!exist) {
      return jobTriggerDescriptor.buildTrigger();
    }
    throw new ObjectAlreadyExistsException(
        this.scheduler.getTrigger(jobTriggerDescriptor.getTriggerKey()));
  }

  public Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
    return this.scheduler.getTrigger(triggerKey);
  }
}
