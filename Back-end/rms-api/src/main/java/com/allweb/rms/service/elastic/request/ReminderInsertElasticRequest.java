package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class ReminderInsertElasticRequest extends BaseElasticRequest<Reminder> {
  public ReminderInsertElasticRequest(Reminder reminder) {
    super(ElasticConstants.REMINDER_INSERT_REQUEST_KEY, reminder);
  }
}
