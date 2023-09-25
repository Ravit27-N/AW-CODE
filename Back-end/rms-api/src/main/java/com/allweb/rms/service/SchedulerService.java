package com.allweb.rms.service;

import com.allweb.rms.core.scheduler.SchedulerHandler;
import com.allweb.rms.core.scheduler.model.JobDetailDescriptor;
import com.allweb.rms.core.scheduler.model.JobTriggerDescriptor;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

  private final SchedulerHandler quartzSchedulerUtils;

  public SchedulerService(SchedulerHandler quartzSchedulerUtils) {
    this.quartzSchedulerUtils = quartzSchedulerUtils;
  }

  public void scheduleJob(
      JobTriggerDescriptor jobTriggerDescriptor, JobDetailDescriptor jobDetailDescriptor)
      throws SchedulerException {
    this.quartzSchedulerUtils.scheduleJob(jobTriggerDescriptor, jobDetailDescriptor);
  }

  public void scheduleJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.quartzSchedulerUtils.scheduleJob(jobTriggerDescriptor);
  }

  public void updateScheduledJob(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    this.quartzSchedulerUtils.updateScheduledJob(jobTriggerDescriptor);
  }

  public boolean unScheduledJob(JobTriggerDescriptor jobTriggerDescriptor)
      throws SchedulerException {
    return this.quartzSchedulerUtils.unScheduledJob(jobTriggerDescriptor);
  }

  public boolean unScheduledJob(TriggerKey triggerKey) throws SchedulerException {
    return this.quartzSchedulerUtils.unScheduledJob(triggerKey);
  }

  public boolean unScheduledJob(String triggerIdentity) throws SchedulerException {
    return this.quartzSchedulerUtils.unScheduledJob(triggerIdentity);
  }

  public void pauseJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.quartzSchedulerUtils.pauseJob(jobTriggerDescriptor);
  }

  public void pauseJob(TriggerKey triggerKey) throws SchedulerException {
    this.quartzSchedulerUtils.pauseJob(triggerKey);
  }

  public void resumeJob(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    this.quartzSchedulerUtils.resumeJob(jobTriggerDescriptor);
  }

  public void resumeJob(TriggerKey triggerKey) throws SchedulerException {
    this.quartzSchedulerUtils.resumeJob(triggerKey);
  }

  public boolean exists(JobDetailDescriptor jobDetailDescriptor) throws SchedulerException {
    return this.quartzSchedulerUtils.exists(jobDetailDescriptor.getJobKey());
  }

  public boolean exists(JobKey jobKey) throws SchedulerException {
    return this.quartzSchedulerUtils.exists(jobKey);
  }

  public boolean exists(JobTriggerDescriptor jobTriggerDescriptor) throws SchedulerException {
    return this.quartzSchedulerUtils.exists(jobTriggerDescriptor.getTriggerKey());
  }

  public boolean exists(TriggerKey triggerKey) throws SchedulerException {
    return this.quartzSchedulerUtils.exists(triggerKey);
  }
}
