package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.CandidateUniversity;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.repository.jpa.CandidateUniversityRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.service.elastic.Handler;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCandidateUpdateHandler implements Handler {
  private final UniversityRepository universityRepository;
  private final CandidateUniversityRepository candidateUniversityRepository;

  protected BaseCandidateUpdateHandler(
      UniversityRepository universityRepository,
      CandidateUniversityRepository candidateUniversityRepository) {
    this.universityRepository = universityRepository;
    this.candidateUniversityRepository = candidateUniversityRepository;
  }

  protected List<University> getUniversityListByCandidateId(int candidateId) {
    List<CandidateUniversity> candidateUniversityList =
        this.candidateUniversityRepository.findByCandidateId(candidateId);
    List<University> universityList = new ArrayList<>();
    if (!candidateUniversityList.isEmpty()) {
      List<University> universities =
          this.universityRepository.findAllById(
              candidateUniversityList.stream()
                  .map(candidateUniversity -> candidateUniversity.getUniversity().getId())
                  .toList());
      universityList.addAll(universities);
    }
    return universityList;
  }
}
