package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import org.springframework.stereotype.Component;

@Component
public class InterviewHardDeleteHandler implements Handler {
  private final InterviewElasticsearchRepository interviewElasticsearchRepository;

  public InterviewHardDeleteHandler(
      InterviewElasticsearchRepository interviewElasticsearchRepository) {
    this.interviewElasticsearchRepository = interviewElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
    this.interviewElasticsearchRepository.deleteById(interview.getId());
  }
}
