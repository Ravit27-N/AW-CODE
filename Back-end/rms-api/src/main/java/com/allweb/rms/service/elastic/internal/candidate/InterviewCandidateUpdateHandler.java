package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class InterviewCandidateUpdateHandler implements Handler {
  private final InterviewElasticsearchRepository interviewElasticsearchRepository;

  InterviewCandidateUpdateHandler(
      InterviewElasticsearchRepository interviewElasticsearchRepository) {
    this.interviewElasticsearchRepository = interviewElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    List<InterviewElasticsearchDocument> candidateInterviewList =
        this.interviewElasticsearchRepository.findByCandidateId(candidate.getId());
    if (!candidateInterviewList.isEmpty()) {
      if (context.contains(ElasticConstants.OPERATION_KEY)
          && ElasticConstants.DELETE_OPERATION.equals(
              context.get(ElasticConstants.OPERATION_KEY))) {
        this.interviewElasticsearchRepository.updateInterviewCandidate(
            candidateInterviewList, null);
        return;
      }
      this.interviewElasticsearchRepository.updateInterviewCandidate(
          candidateInterviewList, candidate);
    }
  }
}
