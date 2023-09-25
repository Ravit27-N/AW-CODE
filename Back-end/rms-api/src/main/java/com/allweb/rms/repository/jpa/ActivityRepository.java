package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.dto.ActivityResponse;
import com.allweb.rms.entity.jpa.Activity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

  @Query(
      value =
          "select new com.allweb.rms.entity.dto.ActivityResponse(a,c.id,concat(a.candidate.salutation,' ',a.candidate.firstname,' ',a.candidate.lastname),cs) from Activity a "
              + "left join Candidate c "
              + "on a.candidate.id = c.id "
              + "left join CandidateStatus cs "
              + "on cs.id = c.candidateStatus.id "
              + "where ( lower(a.title) like %?1% "
              + "or lower(a.description) like %?1%  "
              + "or lower(a.userId) like %?1% "
              + "or lower(concat(a.candidate.salutation,' ',a.candidate.firstname,' ',a.candidate.lastname)) like %?1% "
              + "or function('TO_CHAR',a.createdAt,'dd-MM-yyyy') like %?1% "
              + "or lower(c.lastname) like %?1% ) and c.isDeleted = false ")
  Page<ActivityResponse> fetchAllByFilteringField(String filter, Pageable pageable);

  @Query(
      "select new com.allweb.rms.entity.dto.ActivityResponse(a,c.id,concat(a.candidate.salutation,' ',a.candidate.firstname,' ',a.candidate.lastname),cs) from Activity a "
          + "left join Candidate c "
          + "on a.candidate.id = c.id "
          + "left join CandidateStatus cs "
          + "on cs.id = c.candidateStatus.id where c.isDeleted = false")
  Page<ActivityResponse> getAll(Pageable pageable);

  @Query(
      "select new com.allweb.rms.entity.dto.ActivityResponse(a,c.id,concat(a.candidate.salutation,' ',a.candidate.firstname,' ',a.candidate.lastname),cs) from Activity a "
          + "left join Candidate c "
          + "on a.candidate.id = c.id "
          + "left join CandidateStatus cs "
          + "on cs.id = c.candidateStatus.id where a.id = ?1 and c.isDeleted = false ")
  Optional<ActivityResponse> fetchById(int id);
}
