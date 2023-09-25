package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class ReminderUpdateElasticRequest extends BaseElasticRequest<Reminder> {
  public ReminderUpdateElasticRequest(Reminder reminder) {
    super(ElasticConstants.REMINDER_UPDATE_REQUEST_KEY, reminder);
  }
}
