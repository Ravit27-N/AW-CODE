package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.repository.jpa.ResultRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import org.springframework.stereotype.Component;

@Component
class InterviewUpdateHandler implements Handler {
  private final InterviewElasticsearchRepository interviewElasticsearchRepository;
  private final ResultRepository resultRepository;

  InterviewUpdateHandler(
      InterviewElasticsearchRepository interviewElasticsearchRepository,
      ResultRepository resultRepository) {
    this.interviewElasticsearchRepository = interviewElasticsearchRepository;
    this.resultRepository = resultRepository;
  }


  @Override
  public void handle(ChainContext context) {
    Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
    int resultCount = this.resultRepository.countByInterviewId(interview.getId());
    this.interviewElasticsearchRepository.updateInterviewElasticsearch(interview, resultCount);
  }
}
