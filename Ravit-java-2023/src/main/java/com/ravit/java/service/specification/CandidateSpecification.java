package com.ravit.java.service.specification;

import com.ravit.java.model.Candidate;
import com.ravit.java.model.Candidate_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateSpecification {

  public static Specification<Candidate> findByCandidateId(Long candidateId) {
    return (root, query, cb) -> cb.equal(root.get(Candidate_.ID), candidateId);
  }


}
