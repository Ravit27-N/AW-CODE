package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.Result;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface ResultRepository extends JpaRepository<Result, Integer> {

  @Query(
      value =
          "select id, created_at createdAt, updated_at updatedAt, average, english, flexibility, logical, oral, remark, score from result r where interview_id = ?",
      nativeQuery = true)
  Map<String, Object> findResultByInterviewId(int interviewId);

  @Query(
      value =
          "select count(r) from result r "
              + "LEFT JOIN interview i on r.interview_id = i.id "
              + "LEFT JOIN candidate c on c.id = i.candidate_id"
              + " WHERE c.is_deleted = false and i.is_delete = false",
      nativeQuery = true)
  long countAllByCandidateDeleteFalseAndInterviewDeleteFalse();

  int countByInterviewId(int interviewId);

  Optional<Result> findByInterviewId(int interviewId);
}
