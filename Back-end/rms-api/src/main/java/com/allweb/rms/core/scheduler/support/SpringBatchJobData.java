package com.allweb.rms.core.scheduler.support;

import static com.allweb.rms.core.scheduler.ReminderConstants.ALLOW_REPORT_SENDING_STATE_KEY;
import static com.allweb.rms.core.scheduler.ReminderConstants.BATCH_JOB_NAME_KEY;

import com.allweb.rms.core.scheduler.AbstractJobData;
import com.allweb.rms.core.scheduler.ErrorMessage;
import com.allweb.rms.core.scheduler.JobData;
import org.quartz.JobDataMap;

public class SpringBatchJobData extends AbstractJobData {
  public SpringBatchJobData() {
    super();
  }

  public SpringBatchJobData(JobData jobData) {
    super(jobData);
  }

  public SpringBatchJobData(JobDataMap jobDataMap) {
    super(jobDataMap);
  }

  @Override
  protected void validateInternal(ErrorMessage errorMessage) {
    if (!this.containsKey(BATCH_JOB_NAME_KEY)) {
      errorMessage.addErrorMessage("Batch job name is required.");
    }
    if (!this.containsKey(ALLOW_REPORT_SENDING_STATE_KEY)) {
      errorMessage.addErrorMessage("Batch report sending state allowance is not set.");
    }
  }

  public String getJobName() {
    return this.getData(BATCH_JOB_NAME_KEY).toString();
  }

  public void setJobName(String jobName) {
    this.putData(BATCH_JOB_NAME_KEY, jobName);
  }

  public boolean allowReportSendingState() {
    return Boolean.valueOf(this.getData(ALLOW_REPORT_SENDING_STATE_KEY).toString());
  }

  public void allowReportSendingState(boolean allowance) {
    this.putData(ALLOW_REPORT_SENDING_STATE_KEY, String.valueOf(allowance));
  }
}
