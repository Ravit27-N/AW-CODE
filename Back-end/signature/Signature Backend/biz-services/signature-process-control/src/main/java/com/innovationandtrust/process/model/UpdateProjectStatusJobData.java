package com.innovationandtrust.process.model;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.utils.schedule.job.JobData;
import lombok.Getter;
import org.quartz.JobDataMap;

public record UpdateProjectStatusJobData(@Getter String flowId, @Getter String group) implements JobData {
  @Override
  public JobDataMap getJobDataMap() {
    var data = new JobDataMap();
    data.put(SignProcessConstant.FLOW_ID, flowId);
    data.put(SignProcessConstant.JOB_GROUP, group);
    return data;
  }
}
