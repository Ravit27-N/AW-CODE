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
class CandidateInterviewUpdateHandler implements Handler {
  private final InterviewRepository interviewRepository;
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;
  private final ResultRepository resultRepository;

  public CandidateInterviewUpdateHandler(
      InterviewRepository interviewRepository,
      CandidateElasticsearchRepository candidateElasticsearchRepository,
      ResultRepository resultRepository) {
    this.interviewRepository = interviewRepository;
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
    this.resultRepository = resultRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Interview interview = (Interview) context.get(ElasticConstants.INTERVIEW_OBJECT_KEY);
    int candidateId = interview.getCandidate().getId();
    int candidateInterviewCount =
        interviewRepository.countByCandidateIdAndIsDeleteIsFalse(candidateId);
    Interview lastInterview =
        this.interviewRepository.findFirst1ByCandidateIdAndIsDeleteFalseOrderByDateTimeDesc(
            candidateId);
    // Result
    Optional<Result> candidateInterviewResult = Optional.empty();
    if (lastInterview != null) {
      candidateInterviewResult = this.resultRepository.findByInterviewId(interview.getId());
    }
    this.candidateElasticsearchRepository.updateCandidateInterview(
        candidateId, lastInterview, candidateInterviewResult.orElse(null), candidateInterviewCount);
  }
}
