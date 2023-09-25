package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class ReminderHardDeleteElasticRequest extends BaseElasticRequest<Reminder> {
  public ReminderHardDeleteElasticRequest(Reminder reminder) {
    super(ElasticConstants.REMINDER_HARD_DELETE_REQUEST_KEY, reminder);
  }
}
