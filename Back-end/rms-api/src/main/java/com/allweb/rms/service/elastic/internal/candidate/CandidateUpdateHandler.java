package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateStatusRepository;
import com.allweb.rms.repository.jpa.CandidateUniversityRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;import org.springframework.stereotype.Component;

@Component
@Slf4j
class CandidateUpdateHandler extends BaseCandidateUpdateHandler {
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;
  private final CandidateStatusRepository candidateStatusRepository;

  CandidateUpdateHandler(
      CandidateElasticsearchRepository candidateElasticsearchRepository,
      CandidateStatusRepository candidateStatusRepository,
      UniversityRepository universityRepository,
      CandidateUniversityRepository candidateUniversityRepository) {
    super(universityRepository, candidateUniversityRepository);
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
    this.candidateStatusRepository = candidateStatusRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    Optional<CandidateStatus> candidateStatus =
        this.candidateStatusRepository.findById(candidate.getCandidateStatus().getId());
    List<University> universityList = this.getUniversityListByCandidateId(candidate.getId());
          this.candidateElasticsearchRepository.updateCandidate(
                  candidate, candidateStatus.orElse(null), universityList);
  }
}
