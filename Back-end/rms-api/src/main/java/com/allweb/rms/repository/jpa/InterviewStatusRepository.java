package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.InterviewStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface InterviewStatusRepository extends JpaRepository<InterviewStatus, Integer> {

  @Query("select s from InterviewStatus s where lower(s.name) like %?1%")
  Page<InterviewStatus> findAllByNameContaining(String filter, Pageable pageable);

  @Query("select s from InterviewStatus s where lower(s.name) like %?1%")
  Optional<InterviewStatus> findByName(String name);

  @Query("select s from InterviewStatus s where s.isActive = true")
  Page<InterviewStatus> findAllByActiveIsTrue(Pageable pageable);
}
