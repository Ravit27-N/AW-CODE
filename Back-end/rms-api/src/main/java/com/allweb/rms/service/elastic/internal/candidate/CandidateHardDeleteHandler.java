package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import org.springframework.stereotype.Component;

@Component
class CandidateHardDeleteHandler implements Handler {
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;

  CandidateHardDeleteHandler(CandidateElasticsearchRepository candidateElasticsearchRepository) {
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    this.candidateElasticsearchRepository.deleteById(candidate.getId());
  }
}
