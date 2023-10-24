package com.innovationandtrust.utils.schedule.model;

import com.innovationandtrust.utils.schedule.job.JobData;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

@RequiredArgsConstructor
public class JobTriggerDescriptor implements JobData {

  @Getter private final String id;

  @Getter private final String groupName;

  @Setter @Getter private String description;

  @Setter private JobData data;

  @Getter private DateTimeSchedule triggerOn;

  @Getter private long interval = 0;

  @Getter private int repeatCount = 0;
  private JobKey jobKey;

  public Date triggerOn(DateTimeSchedule dateTimeSchedule) {
    this.triggerOn = dateTimeSchedule;
    return dateTimeSchedule.toDate();
  }

  public Date triggerOn(DateTimeSchedule dateTimeSchedule, ZoneId zoneId) {
    this.triggerOn = dateTimeSchedule;
    return dateTimeSchedule.toDate(zoneId);
  }

  @Override
  public JobDataMap getJobDataMap() {
    if (this.data != null) {
      return this.data.getJobDataMap();
    }
    return new JobDataMap();
  }

  public TriggerKey getTriggerKey() {
    return new TriggerKey(this.id, this.groupName);
  }

  /**
   * Specify a repeat interval in milliseconds.
   *
   * @param intervalInMillis the number of seconds at which the trigger should repeat.
   * @see SimpleTrigger#getRepeatInterval()
   */
  public void setIntervalInMilliseconds(long intervalInMillis) {
    this.interval = intervalInMillis;
  }

  /**
   * Specify a repeat interval in seconds - which will then be multiplied by 1000 to produce
   * milliseconds.
   *
   * @param intervalInSeconds the number of seconds at which the trigger should repeat.
   * @see SimpleTrigger#getRepeatInterval()
   */
  public void setIntervalInSeconds(int intervalInSeconds) {
    this.interval = intervalInSeconds * 1000L;
  }

  /**
   * Specify a repeat interval in minutes - which will then be multiplied by 60 * 1000 to produce
   * milliseconds.
   *
   * @param intervalInMinutes the number of seconds at which the trigger should repeat.
   * @see SimpleTrigger#getRepeatInterval()
   */
  public void setIntervalInMinutes(int intervalInMinutes) {
    this.interval = intervalInMinutes * DateBuilder.MILLISECONDS_IN_MINUTE;
  }

  /**
   * Specify a repeat interval in minutes - which will then be multiplied by 60 * 60 * 1000 to
   * produce milliseconds.
   *
   * @param intervalInHours the number of seconds at which the trigger should repeat.
   * @see SimpleTrigger#getRepeatInterval()
   */
  public void setIntervalInHours(int intervalInHours) {
    this.interval = intervalInHours * DateBuilder.MILLISECONDS_IN_HOUR;
  }

  /**
   * Specify much time the trigger will repeat - the total number of firings will be this number +
   * 1.
   *
   * @param triggerRepeatCount the number of seconds at which the trigger should repeat.
   * @see SimpleTrigger#getRepeatCount()
   */
  public void setRepeatCount(int triggerRepeatCount) {
    this.repeatCount = triggerRepeatCount;
  }

  private SimpleScheduleBuilder getSimpleScheduleBuilder() {
    return SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInMilliseconds(this.interval)
        .withRepeatCount(this.repeatCount)
        .withMisfireHandlingInstructionNextWithRemainingCount(); // missFired is discarded.
  }

  /**
   * Build a new specific {@link Trigger} described by this object for Quartz's {@link Scheduler}
   * use to fire a specified {@link Job}.
   *
   * <p>The newly created {@link Trigger} will trigger the {@link Job} described by {@code
   * JobTriggerDescriptor#buildTrigger()} using a {@link SimpleScheduleBuilder#simpleSchedule()}.
   *
   * @see Job * @see JobDescriptor
   * @see Trigger
   * @return {@link Trigger}
   */
  public Trigger buildTrigger() {
    return this.buildTrigger(this.getSimpleScheduleBuilder());
  }

  /**
   * Build a new specific {@link Trigger} described by this object for Quartz's {@link Scheduler}
   * use to fire a specified {@link Job}.
   *
   * <p>The newly created {@link Trigger} will trigger the {@link Job} described by {@link
   * JobTriggerDescriptor#buildTrigger()} using a provided {@link SimpleScheduleBuilder}.
   *
   * @param schedulerBuilder The {@link ScheduleBuilder} that will be used to define the Trigger's
   *     schedule.
   * @see Job * @see JobDescriptor
   * @see Trigger
   * @return {@link Trigger}
   */
  public Trigger buildTrigger(SimpleScheduleBuilder schedulerBuilder) {
    TriggerBuilder<? extends Trigger> triggerBuilder =
        TriggerBuilder.newTrigger()
            .withIdentity(this.id, this.groupName)
            .withSchedule(schedulerBuilder);
    if (!this.getJobDataMap().isEmpty()) {
      triggerBuilder.usingJobData(this.getJobDataMap());
    }
    if (this.getTriggerOn() == null) {
      throw new IllegalStateException("Trigger date is not specified.");
    }
    triggerBuilder.startAt(this.getTriggerOn().toDate());
    if (this.jobKey != null) {
      triggerBuilder.forJob(this.jobKey);
    }

    return triggerBuilder.build();
  }

  public void forJob(JobKey jobKey) {
    this.jobKey = jobKey;
  }

  public void forJob(JobDetailDescriptor jobDetailDescriptor) {
    this.jobKey = jobDetailDescriptor.getJobKey();
  }
}
