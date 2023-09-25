package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import com.allweb.rms.utils.ReminderType;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
class ReminderUpdateHandler extends BaseReminderHandler implements Handler {
  private final ModelMapper modelMapper;
  private final ReminderElasticsearchRepository reminderElasticsearchRepository;

  ReminderUpdateHandler(
      ModelMapper modelMapper, ReminderElasticsearchRepository reminderElasticsearchRepository) {
    this.modelMapper = modelMapper;
    this.reminderElasticsearchRepository = reminderElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Reminder reminder = (Reminder) context.get(ElasticConstants.REMINDER_OBJECT_KEY);
    Optional<ReminderElasticsearchDocument> reminderElasticDoc =
        this.reminderElasticsearchRepository.findById(reminder.getId());
    if (!reminderElasticDoc.isPresent()) {
      return;
    }
    if (reminder.isSend()) {
      this.reminderElasticsearchRepository.deleteById(reminder.getId());
      return;
    }
    ReminderElasticsearchDocument reminderElasticsearchDocument =
        this.modelMapper.map(reminder, ReminderElasticsearchDocument.class);
    reminderElasticsearchDocument.setReminderType(reminder.getReminderType().getId());
    if (ReminderType.INTERVIEW.getValue().equals(reminder.getReminderType().getId())) {
      reminderElasticsearchDocument.setCandidate(null);
      reminderElasticsearchDocument.setInterview(this.getReminderInterview(reminder));
    } else if (ReminderType.SPECIAL.getValue().equals(reminder.getReminderType().getId())) {
      reminderElasticsearchDocument.setInterview(null);
      reminderElasticsearchDocument.setCandidate(this.getReminderCandidate(reminder));
    }
    this.reminderElasticsearchRepository.save(reminderElasticsearchDocument);
  }
}