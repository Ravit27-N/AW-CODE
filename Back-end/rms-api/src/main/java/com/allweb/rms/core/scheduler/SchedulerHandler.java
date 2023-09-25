package com.allweb.rms.core.scheduler;

import com.allweb.rms.core.scheduler.model.JobDetailDescriptor;
import com.allweb.rms.core.scheduler.model.JobTriggerDescriptor;
import java.util.Optional;
import java.util.Set;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class SchedulerHandler {
  private final SchedulerFactoryBean schedulerFactory;

  public SchedulerHandler(
      @Qualifier("schedulerFactoryBean") SchedulerFactoryBean schedulerFactory) {
    this.schedulerFactory = schedulerFactory;
  }

  public void scheduleJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.schedulerFactory.getScheduler().scheduleJob(this.createJobTrigger(jobTriggerDescriptor));
  }

  public void scheduleJob(
      JobTriggerDescriptor jobTriggerDescriptor, JobDetailDescriptor jobDetailDescriptor)
      throws SchedulerException {
    this.schedulerFactory
        .getScheduler()
        .scheduleJob(
            this.createJobDetail(jobDetailDescriptor), this.createJobTrigger(jobTriggerDescriptor));
  }

  public void updateScheduledJob(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    this.schedulerFactory
        .getScheduler()
        .rescheduleJob(
            jobTriggerDescriptor.getTriggerKey(), this.createJobTrigger(jobTriggerDescriptor));
  }

  public boolean unScheduledJob(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    return this.schedulerFactory.getScheduler().unscheduleJob(jobTriggerDescriptor.getTriggerKey());
  }

  public boolean unScheduledJob(TriggerKey triggerKey) throws SchedulerException {
    return this.schedulerFactory.getScheduler().unscheduleJob(triggerKey);
  }

  public boolean unScheduledJob(String triggerIdentity) throws SchedulerException {
    TriggerKey triggerKey = this.findTriggerKey(triggerIdentity);

    if (triggerKey != null) {
      return this.unScheduledJob(triggerKey);
    }
    return false;
  }

  public boolean exists(JobDetailDescriptor jobDetailDescriptor) throws SchedulerException {
    return this.schedulerFactory.getScheduler().checkExists(jobDetailDescriptor.getJobKey());
  }

  public boolean exists(JobKey jobKey) throws SchedulerException {
    return this.schedulerFactory.getScheduler().checkExists(jobKey);
  }

  public boolean exists(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    return this.schedulerFactory.getScheduler().checkExists(jobTriggerDescriptor.getTriggerKey());
  }

  public boolean exists(TriggerKey triggerKey) throws SchedulerException {
    return this.schedulerFactory.getScheduler().checkExists(triggerKey);
  }

  private TriggerKey findTriggerKey(String triggerIdentity) throws SchedulerException {
    Set<TriggerKey> triggerKeys =
        this.schedulerFactory.getScheduler().getTriggerKeys(GroupMatcher.anyGroup());
    Optional<TriggerKey> scheduledTriggerKey =
        triggerKeys.stream()
            .filter(triggerKey -> triggerKey.getName().equals(triggerIdentity))
            .findFirst();
    if (scheduledTriggerKey.isPresent()) {
      return scheduledTriggerKey.get();
    }
    return null;
  }

  public void pauseJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.pauseJob(jobTriggerDescriptor.getTriggerKey());
  }

  public void pauseJob(TriggerKey triggerKey) throws SchedulerException {
    this.schedulerFactory.getScheduler().pauseTrigger(triggerKey);
  }

  public void resumeJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.resumeJob(jobTriggerDescriptor.getTriggerKey());
  }

  public void resumeJob(TriggerKey triggerKey) throws SchedulerException {
    this.schedulerFactory.getScheduler().resumeTrigger(triggerKey);
  }

  public JobDetail buildJobDetail(JobDetailDescriptor jobDetailDescriptor) {
    JobBuilder jobBuilder =
        JobBuilder.newJob(jobDetailDescriptor.getJobClass())
            .withIdentity(jobDetailDescriptor.getId(), jobDetailDescriptor.getGroupName())
            .storeDurably(jobDetailDescriptor.isPersistInDatabasePermanently());
    if (jobDetailDescriptor.getData() != null) {
      jobBuilder = jobBuilder.setJobData(jobDetailDescriptor.getData());
    }
    return jobBuilder.build();
  }

  /**
   * Build a new specific {@link Trigger} described by this object for Quartz's {@link Scheduler}
   * use to fire a specified {@link Job}.
   *
   * <p>The newly created {@link Trigger} will triggers the {@link Job} described by {@link
   * JobTriggerDescriptor} using a provided {@link SimpleScheduleBuilder}.
   *
   * @return {@link Trigger}
   * @see Job * @see JobDescriptor
   * @see Trigger
   */
  public Trigger buildTrigger(JobTriggerDescriptor jobTriggerDescriptor) {
    SimpleScheduleBuilder schedulerBuilder =
        this.getSimpleScheduleBuilder(
            jobTriggerDescriptor.getInterval(), jobTriggerDescriptor.getRepeatCount());

    TriggerBuilder<? extends Trigger> triggerBuilder =
        TriggerBuilder.newTrigger()
            .withIdentity(jobTriggerDescriptor.getId(), jobTriggerDescriptor.getGroupName())
            .withSchedule(schedulerBuilder);
    if (jobTriggerDescriptor.getData() != null) {
      triggerBuilder.usingJobData(jobTriggerDescriptor.getData());
    }
    if (jobTriggerDescriptor.getTriggerOn() == null) {
      throw new IllegalStateException("Trigger date is not specified.");
    }
    triggerBuilder.startAt(jobTriggerDescriptor.getTriggerOn().toDate());

    return triggerBuilder.build();
  }

  private SimpleScheduleBuilder getSimpleScheduleBuilder(long interval, int repeatCount) {
    return SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInMilliseconds(interval)
        .withRepeatCount(repeatCount);
  }

  private JobDetail createJobDetail(JobDetailDescriptor jobDetailDescriptor)
      throws SchedulerException {
    boolean exist =
        this.schedulerFactory.getScheduler().checkExists(jobDetailDescriptor.getJobKey());
    if (!exist) {
      return jobDetailDescriptor.buildJobDetail();
    }
    throw new ObjectAlreadyExistsException(
        this.schedulerFactory.getScheduler().getJobDetail(jobDetailDescriptor.getJobKey()));
  }

  private Trigger createJobTrigger(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    boolean exist =
        this.schedulerFactory.getScheduler().checkExists(jobTriggerDescriptor.getTriggerKey());
    if (!exist) {
      return jobTriggerDescriptor.buildTrigger();
    }
    throw new ObjectAlreadyExistsException(
        this.schedulerFactory.getScheduler().getTrigger(jobTriggerDescriptor.getTriggerKey()));
  }
}
