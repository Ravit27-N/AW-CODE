package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import com.allweb.rms.utils.ReminderType;
import org.springframework.stereotype.Component;

@Component
class CandidateReminderUpdateHandler implements Handler {
  private final ReminderRepository reminderRepository;
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;

  CandidateReminderUpdateHandler(
      ReminderRepository reminderRepository,
      CandidateElasticsearchRepository candidateElasticsearchRepository) {
    this.reminderRepository = reminderRepository;
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Reminder reminder = (Reminder) context.get(ElasticConstants.REMINDER_OBJECT_KEY);
    if (ReminderType.SPECIAL.getValue().equals(reminder.getReminderType().getId())) {
      Candidate candidate = reminder.getCandidate();
      if (candidate == null) {
        return;
      }
      String currentOperation = context.get(ElasticConstants.OPERATION_KEY).toString();
      if (ElasticConstants.DELETE_OPERATION.equals(currentOperation)) {
        this.candidateElasticsearchRepository.decrementCandidateReminderCount(candidate.getId(), 1);
      } else if (ElasticConstants.INSERT_OPERATION.equals(currentOperation)
          && (!reminder.isSend() && !reminder.isDeleted() && reminder.isActive())) {
        this.candidateElasticsearchRepository.incrementCandidateReminderCount(candidate.getId(), 1);
      } else {
        int reminderCount =
            this.reminderRepository
                .countByCandidateIdAndActiveIsTrueAndDeletedIsFalseAndIsSendIsFalse(
                    candidate.getId());
        this.candidateElasticsearchRepository.updateCandidateReminderCount(
            candidate.getId(), reminderCount);
      }
    }
  }
}
