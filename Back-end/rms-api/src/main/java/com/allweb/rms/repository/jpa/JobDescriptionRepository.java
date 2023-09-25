package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.JobDescription;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Integer> {

  /*
   *
   * Repository interface of JobDescription
   *
   * > Do business logic ....
   *
   * */

  Optional<JobDescription> findByIdAndActiveIsTrue(int id); // return optional value

  Page<JobDescription> getAllByActiveIn(
      Collection<@NotNull Boolean> active,
      Pageable pageable); // return page that find in active collection

  @Query(
      "select new JobDescription (j.id,j.title,j.description,j.filename,j.active,j.createdAt,j.updatedAt) from JobDescription j where j.active in ?2 and lower(j.title) like %?1%")
  Page<JobDescription> getAllByFilterAndActive(
      String filter,
      Collection<@NotNull Boolean> active,
      Pageable pageable); // return page that find in active collection and filter

  @Query(
      "select new JobDescription (j.id,j.title,j.description,j.filename,j.active,j.createdAt,j.updatedAt) from JobDescription j where lower(j.title) like %?1%")
  Page<JobDescription> getAllByFilter(
      String filter, Pageable pageable); // return page that find by filter

  JobDescription findByTitle(String title);
}
