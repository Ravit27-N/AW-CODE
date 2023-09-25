package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.Project;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@SecurityRequirement(name = "bearer")
@RepositoryRestResource
public interface ProjectRepository extends JpaRepository<Project, Integer> {
  @Query(value = "SELECT count(1) from project where lower(name) = lower(?1)", nativeQuery = true)
  long validateName(String name);

  @Query(
      value = "SELECT count(1) from project where lower(name)=lower(?2) and id != ?1",
      nativeQuery = true)
  long validateNameOnUpdate(int id, String name);

  @Query("select p from Project p where p.active = true")
  Page<Project> findAllByActiveIsTrue(Pageable pageable);

  @Query("select p from Project p where p.isDeleted = false and lower(p.name) like %?1%")
  Page<Project> findAllByNameContaining(String filter, Pageable pageable);

  @Query("select p from Project p where p.isDeleted = true and lower(p.name) like %?1%")
  Page<Project> findAllByNameContainingAndDeletedIsTrue(String filter, Pageable pageable);

  @Query("select p from Project p where p.isDeleted = false")
  Page<Project> findAllByDeletedIsFalse(Pageable pageable);

  @Query("select p from Project p where p.isDeleted = true")
  Page<Project> findAllByDeletedIsTrue(Pageable pageable);
}
