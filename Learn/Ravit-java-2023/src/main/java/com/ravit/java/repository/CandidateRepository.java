package com.ravit.java.repository;

import com.ravit.java.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>,
    JpaSpecificationExecutor<Candidate> {

}
