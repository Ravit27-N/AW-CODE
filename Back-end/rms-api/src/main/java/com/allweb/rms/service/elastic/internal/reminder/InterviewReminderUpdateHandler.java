package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import com.allweb.rms.utils.ReminderType;
import org.springframework.stereotype.Component;

@Component
class InterviewReminderUpdateHandler implements Handler {
  private final ReminderRepository reminderRepository;
  private final InterviewElasticsearchRepository interviewElasticsearchRepository;

  InterviewReminderUpdateHandler(
      ReminderRepository reminderRepository,
      InterviewElasticsearchRepository interviewElasticsearchRepository) {
    this.reminderRepository = reminderRepository;
    this.interviewElasticsearchRepository = interviewElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Reminder reminder = (Reminder) context.get(ElasticConstants.REMINDER_OBJECT_KEY);
    if (ReminderType.INTERVIEW.getValue().equals(reminder.getReminderType().getId())) {
      Interview interview = reminder.getInterview();
      if (interview == null) {
        return;
      }
      String currentOperation = context.get(ElasticConstants.OPERATION_KEY).toString();
      if (ElasticConstants.DELETE_OPERATION.equals(currentOperation)) {
        this.interviewElasticsearchRepository.decrementReminderCount(interview.getId(), 1);
      } else if (ElasticConstants.INSERT_OPERATION.equals(currentOperation)
          && (!reminder.isSend() && !reminder.isDeleted() && reminder.isActive())) {
        this.interviewElasticsearchRepository.incrementReminderCount(interview.getId(), 1);
      } else {
        int reminderCount =
            this.reminderRepository
                .countByInterviewIdAndActiveIsTrueAndDeletedIsFalseAndIsSendIsFalse(
                    interview.getId());
        this.interviewElasticsearchRepository.updateInterviewReminderCount(
            interview.getId(), reminderCount);
      }
    }
  }
}
