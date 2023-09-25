package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateUniversityRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class CandidateReportCandidateUpdateHandler extends BaseCandidateUpdateHandler {
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;

  CandidateReportCandidateUpdateHandler(
      CandidateElasticsearchRepository candidateElasticsearchRepository,
      UniversityRepository universityRepository,
      CandidateUniversityRepository candidateUniversityRepository) {
    super(universityRepository, candidateUniversityRepository);
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    if (candidate.isDeleted()) {
      this.candidateElasticsearchRepository.deleteCandidateReportByCandidateId(candidate.getId());
    } else {
      List<University> universityList = this.getUniversityListByCandidateId(candidate.getId());
      this.candidateElasticsearchRepository.updateCandidateReport(candidate, universityList);
    }
  }
}
