package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import org.springframework.stereotype.Component;

@Component
class ReminderInterviewHardDeleteHandler implements Handler {
  private final ReminderElasticsearchRepository reminderElasticsearchRepository;

  ReminderInterviewHardDeleteHandler(
      ReminderElasticsearchRepository reminderElasticsearchRepository) {
    this.reminderElasticsearchRepository = reminderElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    if (context.contains(ElasticConstants.INTERVIEW_OBJECT_KEY)) {
      Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
      this.reminderElasticsearchRepository.deleteByInterviewId(interview.getId());
    }
  }
}
