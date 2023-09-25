package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.CandidateUniversity;
import com.allweb.rms.entity.jpa.University;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface CandidateUniversityRepository extends JpaRepository<CandidateUniversity, Integer> {

  @Modifying
  @Query(value = "DELETE FROM candidate_university WHERE candidate_id = ?1", nativeQuery = true)
  void deleteCandidateUniversityByCandidateId(int id);

  @Query(
      value =
          "select cast(json_agg(json_build_object('id',u.id,'name',u.name)) as json)  from candidate_university cu"
              + " left join university u on u.id = cu.university_id"
              + " left join candidate c on cu.candidate_id = c.id"
              + " where cu.candidate_id = ?1",
      nativeQuery = true)
  Map<String, Object> getUniversityByCandidateId(int id);
  @Query(
          value =
                  "select cast(json_agg(json_build_object('id',u.id,'name',u.name)) as json)  from candidate_university cu"
                          + " left join university u on u.id = cu.university_id"
                          + " left join candidate c on cu.candidate_id = c.id"
                          + " where cu.candidate_id = ?1",
          nativeQuery = true)
  Map<String, University> getUniversityByCandidateId123(int id);

  @Query(
      value = "select count(u) from candidate_university u where university_id = ?1",
      nativeQuery = true)
  Long countCandidateByUniversityId(int id);
  List<CandidateUniversity> findByCandidateId(int candidateId);
}
