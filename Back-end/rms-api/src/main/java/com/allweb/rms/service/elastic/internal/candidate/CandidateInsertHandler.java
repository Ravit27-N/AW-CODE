package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateStatusRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
class CandidateInsertHandler implements Handler {
  private final ModelMapper modelMapper;
  private final UniversityRepository universityRepository;
  private final CandidateStatusRepository candidateStatusRepository;
  private final CandidateElasticsearchRepository candidateElasticsearchRepository;

  CandidateInsertHandler(
      ModelMapper modelMapper,
      UniversityRepository universityRepository,
      CandidateStatusRepository candidateStatusRepository,
      CandidateElasticsearchRepository candidateElasticsearchRepository) {
    this.modelMapper = modelMapper;
    this.universityRepository = universityRepository;
    this.candidateStatusRepository = candidateStatusRepository;
    this.candidateElasticsearchRepository = candidateElasticsearchRepository;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    List<Integer> universityIds =
        (List<Integer>) context.get(ElasticConstants.UNIVERSITY_ID_LIST_KEY);
    Optional<CandidateStatus> candidateStatus =
        this.candidateStatusRepository.findById(candidate.getCandidateStatus().getId());
    List<University> universityList = this.universityRepository.findAllById(universityIds);
    CandidateElasticsearchDocument candidateElasticDoc =
        this.modelMapper.map(candidate, CandidateElasticsearchDocument.class);
    candidateElasticDoc.setId(candidate.getId());
    candidateElasticDoc.setFirstName(candidate.getFirstname());
    candidateElasticDoc.setLastName(candidate.getLastname());
    candidateElasticDoc.setInterviewCount(0);
    candidateElasticDoc.setReminderCount(0);
    candidateElasticDoc.setCandidateStatus(
        candidateStatus
            .map(
                candidateStatus1 ->
                    modelMapper.map(
                        candidateStatus1, CandidateElasticsearchDocument.CandidateStatus.class))
            .orElse(new CandidateElasticsearchDocument.CandidateStatus()));
    candidateElasticDoc.setUniversities(
        universityList.stream()
            .map(
                university ->
                    modelMapper.map(university, com.allweb.rms.entity.elastic.University.class))
            .collect(Collectors.toList()));
    this.candidateElasticsearchRepository.save(candidateElasticDoc);
  }
}
