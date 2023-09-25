package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.elastic.CandidateReportElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
class CandidateReportCandidateInsertHandler implements Handler {
  private final ModelMapper modelMapper;
  private final UniversityRepository universityRepository;
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;

  CandidateReportCandidateInsertHandler(
      ModelMapper modelMapper,
      UniversityRepository universityRepository,
      CandidateElasticsearchRepository candidateElasticsearchRepository) {
    this.modelMapper = modelMapper;
    this.universityRepository = universityRepository;
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    List<Integer> universityIds =
        (List<Integer>) context.get(ElasticConstants.UNIVERSITY_ID_LIST_KEY);
    List<University> universityList = this.universityRepository.findAllById(universityIds);
    CandidateReportElasticsearchDocument candidateReportElasticDoc =
        this.modelMapper.map(candidate, CandidateReportElasticsearchDocument.class);
    candidateReportElasticDoc.setId(candidate.getId());
    candidateReportElasticDoc.setFirstName(candidate.getFirstname());
    candidateReportElasticDoc.setLastName(candidate.getLastname());
    candidateReportElasticDoc.setUniversities(
        universityList.stream()
            .map(
                university ->
                    modelMapper.map(university, com.allweb.rms.entity.elastic.University.class))
            .collect(Collectors.toList()));
    this.candidateElasticsearchRepository.saveCandidateReportElasticDocument(
        candidateReportElasticDoc);
  }
}
