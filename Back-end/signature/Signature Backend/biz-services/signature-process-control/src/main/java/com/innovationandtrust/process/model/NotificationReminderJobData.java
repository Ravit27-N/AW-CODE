package com.innovationandtrust.process.model;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.utils.schedule.job.JobData;
import lombok.Getter;
import org.quartz.JobDataMap;

public record NotificationReminderJobData(@Getter String flowId) implements JobData {
  @Override
  public JobDataMap getJobDataMap() {
    var data = new JobDataMap();
    data.put(SignProcessConstant.FLOW_ID, flowId);
    return data;
  }
}
