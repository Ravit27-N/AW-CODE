package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import org.springframework.stereotype.Component;

@Component
class ReminderHardDeleteHandler implements Handler {
  private final ReminderElasticsearchRepository reminderElasticsearchRepository;

  ReminderHardDeleteHandler(ReminderElasticsearchRepository reminderElasticsearchRepository) {
    this.reminderElasticsearchRepository = reminderElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Reminder reminder = (Reminder) context.get(ElasticConstants.REMINDER_OBJECT_KEY);
    this.reminderElasticsearchRepository.deleteById(reminder.getId());
  }
}
