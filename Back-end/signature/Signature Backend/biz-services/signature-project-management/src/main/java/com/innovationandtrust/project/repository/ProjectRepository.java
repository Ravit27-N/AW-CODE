package com.innovationandtrust.project.repository;

import com.innovationandtrust.project.model.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** ProjectRepository has findProjectInfoById, and completeProject method. */
public interface ProjectRepository
    extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

  @EntityGraph(value = "FindProjectByInfo", type = EntityGraphType.FETCH)
  Optional<Project> findProjectInfoById(@Param("id") Long id);

  @Query("select new Project(p.id, p.name, p.status) from Project as p where p.id in :id")
  Optional<Project> findProjectById(@Param("id") Long id);

  @Modifying
  @Query("update Project set status = :status where id = :id")
  void completeProject(@Param("id") Long id, @Param("status") String status);

  @Query("select p.status from Project as p where p.createdBy in :userIds or p.assignedTo in :userIds")
  List<String> findAllStatusesByUserIds(@Param("userIds") List<Long> userIds);

  @Override
  Page<Project> findAll(Specification<Project> spec, Pageable pageable);

  @Modifying
  @Query("update Project set step = :step where id = :id")
  void updateStep(@Param("step") String step, @Param("id") Long id);

  @Modifying
  @Query(
      "update Project set status = :status, modifiedAt = current_timestamp , modifiedBy = :userId where id = :id")
  void updateStatus(
      @Param("status") String status, @Param("userId") Long userId, @Param("id") Long id);
}
