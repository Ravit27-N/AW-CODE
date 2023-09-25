package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import com.allweb.rms.utils.ReminderType;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
class ReminderInterviewUpdateHandler implements Handler {
  private final ReminderElasticsearchRepository reminderElasticsearchRepository;

  ReminderInterviewUpdateHandler(ReminderElasticsearchRepository reminderElasticsearchRepository) {
    this.reminderElasticsearchRepository = reminderElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
    List<ReminderElasticsearchDocument> reminderElasticDocList =
        this.reminderElasticsearchRepository.findByReminderType(ReminderType.INTERVIEW.getValue());
    List<ReminderElasticsearchDocument> reminders =
        reminderElasticDocList.stream()
            .filter(
                reminderElasticDoc -> {
                  ReminderElasticsearchDocument.Interview candidateInterview =
                      reminderElasticDoc.getInterview();
                  return candidateInterview != null
                      && candidateInterview.getId() != null
                      && candidateInterview.getId().equals(interview.getId());
                })
            .collect(Collectors.toList());
    if (reminders.isEmpty()) {
      return;
    }
    if (interview.isDelete()) {
      this.reminderElasticsearchRepository.deleteAll(reminders);
    } else {
      this.reminderElasticsearchRepository.updateReminderInterviewElasticsearchDocument(
          reminders, interview);
    }
  }
}
