package com.techno.ms2.quartzscheduling.service.specification;


import com.techno.ms2.quartzscheduling.entity.Candidate;
import com.techno.ms2.quartzscheduling.entity.Candidate_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateSpecification {

  public static Specification<Candidate> findByCandidateId(Long candidateId) {
    return (root, query, cb) -> cb.equal(root.get(Candidate_.ID), candidateId);
  }


}
