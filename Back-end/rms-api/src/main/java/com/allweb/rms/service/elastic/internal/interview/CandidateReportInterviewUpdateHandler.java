package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Result;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ResultRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class CandidateReportInterviewUpdateHandler implements Handler {
  private final InterviewRepository interviewRepository;
  private final ResultRepository resultRepository;
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;

  public CandidateReportInterviewUpdateHandler(
      InterviewRepository interviewRepository,
      ResultRepository resultRepository,
      CandidateElasticsearchRepository candidateElasticsearchRepository) {
    this.interviewRepository = interviewRepository;
    this.resultRepository = resultRepository;
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
    int candidateId = interview.getCandidate().getId();
    Interview candidateInterview =
        this.interviewRepository.findFirst1ByCandidateIdAndIsDeleteFalseOrderByDateTimeDesc(
            candidateId);
    Optional<Result> candidateInterviewResult = Optional.empty();
    if (candidateInterview != null) {
      candidateInterviewResult = this.resultRepository.findByInterviewId(interview.getId());
    }
    this.candidateElasticsearchRepository.updateCandidateReportInterview(
        candidateId, candidateInterview, candidateInterviewResult.orElse(null));
  }
}
