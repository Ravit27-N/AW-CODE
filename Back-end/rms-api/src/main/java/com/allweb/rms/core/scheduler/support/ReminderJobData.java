package com.allweb.rms.core.scheduler.support;

import com.allweb.rms.core.scheduler.AbstractJobData;
import com.allweb.rms.core.scheduler.ErrorMessage;
import com.allweb.rms.core.scheduler.JobData;
import com.allweb.rms.core.scheduler.ReminderConstants;
import org.quartz.JobDataMap;

public class ReminderJobData extends AbstractJobData {

  public ReminderJobData() {
    super();
  }

  public ReminderJobData(JobData jobData) {
    super(jobData);
  }

  public ReminderJobData(JobDataMap jobDataMap) {
    super(jobDataMap);
  }

  @Override
  protected void validateInternal(ErrorMessage errorMessage) {
    if (!this.containsKey(ReminderConstants.REMINDER_ID_KEY)) {
      errorMessage.addErrorMessage("Reminder id is required.");
    }
  }

  public String getReminderId() {
    return this.getData(ReminderConstants.REMINDER_ID_KEY, String.class, "");
  }

  public void setReminderId(String reminderId) {
    this.putData(ReminderConstants.REMINDER_ID_KEY, reminderId);
  }

  public String getUserEmail() {
    return this.getData(ReminderConstants.USER_EMAIL_KEY, String.class, null);
  }

  public void setUserEmail(String email) {
    this.putData(ReminderConstants.USER_EMAIL_KEY, email);
  }
}
