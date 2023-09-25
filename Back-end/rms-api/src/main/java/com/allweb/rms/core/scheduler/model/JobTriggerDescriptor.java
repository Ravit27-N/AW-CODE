package com.allweb.rms.core.scheduler.model;

import com.allweb.rms.core.scheduler.JobData;
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
public class JobTriggerDescriptor {
  @Getter private final String id;

  @Getter private final String groupName;

  @Setter @Getter private String description;

  @Setter private JobData data;

  @Getter private DateTimeInfo triggerOn;

  @Getter private long interval = 0;

  @Getter private int repeatCount = 0;
  private JobKey jobKey;

  public Date triggerOn(DateTimeInfo dateTimeInfo) {
    this.triggerOn = dateTimeInfo;
    return dateTimeInfo.toDate();
  }

  public JobDataMap getData() {
    if (this.data != null) {
      return this.data.getJobDataMap();
    }
    return null;
  }

  public TriggerKey getTriggerKey() {
    return new TriggerKey(this.id, this.groupName);
  }

  /**
   * Specify a repeat interval in milliseconds.
   *
   * @param intervalInMillis the number of seconds at which the trigger should repeat.
   * @return the updated SimpleScheduleBuilder
   * @see SimpleTrigger#getRepeatInterval()
   * @see {@link SimpleTrigger#withRepeatCount(int)}
   */
  public void setIntervalInMilliseconds(long intervalInMillis) {
    this.interval = intervalInMillis;
  }

  /**
   * Specify a repeat interval in seconds - which will then be multiplied by 1000 to produce
   * milliseconds.
   *
   * @param intervalInSeconds the number of seconds at which the trigger should repeat.
   * @return the updated SimpleScheduleBuilder
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
   * @return the updated SimpleScheduleBuilder
   * @see SimpleTrigger#getRepeatInterval()
   * @see #withRepeatCount(int)
   */
  public void setIntervalInMinutes(int intervalInMinutes) {
    this.interval = intervalInMinutes * DateBuilder.MILLISECONDS_IN_MINUTE;
  }

  /**
   * Specify a repeat interval in minutes - which will then be multiplied by 60 * 60 * 1000 to
   * produce milliseconds.
   *
   * @param intervalInHours the number of seconds at which the trigger should repeat.
   * @return the updated SimpleScheduleBuilder
   * @see SimpleTrigger#getRepeatInterval()
   * @see #withRepeatCount(int)
   */
  public void setIntervalInHours(int intervalInHours) {
    this.interval = intervalInHours * DateBuilder.MILLISECONDS_IN_HOUR;
  }

  /**
   * Specify a the number of time the trigger will repeat - total number of firings will be this
   * number + 1.
   *
   * @param triggerRepeatCount the number of seconds at which the trigger should repeat.
   * @return the updated SimpleScheduleBuilder
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
   * <p>The newly created {@link Trigger} will triggers the {@link Job} described by {@link
   * JobTriggerDescriptor#getJobDescriptor()} using a {@link
   * SimpleScheduleBuilder#simpleSchedule()}.
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
   * <p>The newly created {@link Trigger} will triggers the {@link Job} described by {@link
   * JobTriggerDescriptor#getJobDescriptor()} using a provided {@link SimpleScheduleBuilder}.
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
    if (this.getData() != null) {
      triggerBuilder.usingJobData(this.getData());
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
