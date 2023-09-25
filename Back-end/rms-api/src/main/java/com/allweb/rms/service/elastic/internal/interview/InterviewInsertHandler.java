package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.repository.jpa.ResultRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
class InterviewInsertHandler implements Handler {
  private final InterviewElasticsearchRepository interviewElasticsearchRepository;
  private final ResultRepository resultRepository;
  private final ModelMapper modelMapper;

  InterviewInsertHandler(
      InterviewElasticsearchRepository interviewElasticsearchRepository,
      ResultRepository resultRepository,
      ModelMapper modelMapper) {
    this.interviewElasticsearchRepository = interviewElasticsearchRepository;
    this.resultRepository = resultRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public void handle(ChainContext context) {
    Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
    int reminderCount = 0;
    if (context.contains(ElasticConstants.REMINDER_OBJECT_KEY)) {
      Reminder reminder = (Reminder) context.get(ElasticConstants.REMINDER_OBJECT_KEY);
      if (reminder.getInterview().getId() == interview.getId()) {
        reminderCount = 1;
      }
    }
    int resultCount = this.resultRepository.countByInterviewId(interview.getId());
    InterviewElasticsearchDocument interviewElasticDoc =
        this.modelMapper.map(interview, InterviewElasticsearchDocument.class);
    interviewElasticDoc.setHasResult(resultCount > 0);
    interviewElasticDoc.setDeleted(interview.isDelete());
    interviewElasticDoc.setReminderCount(reminderCount);
    this.interviewElasticsearchRepository.save(interviewElasticDoc);
  }
}
