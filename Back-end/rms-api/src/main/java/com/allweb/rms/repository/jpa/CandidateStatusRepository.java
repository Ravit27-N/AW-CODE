package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.CandidateStatus;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@SecurityRequirement(name = "bearer")
@RepositoryRestResource
public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Integer> {

  @Query(
      value =
          "select id, created_at, created_by, updated_at, last_modified_by, active, description, is_deletable, is_deleted, title from candidate_status "
              + " where case when lower(?1) = 'active' then active = true and is_deleted = false "
              + " when lower(?1) = 'inactive' then active = false and is_deleted = false "
              + " when lower(?1) = 'deleted' then is_deleted = true else ?1 = 'all' end",
      countQuery =
          "select count(id) from candidate_status "
              + " where case when lower(?1) = 'active' then active = true and is_deleted = false "
              + " when lower(?1) = 'inactive' then active = false and is_deleted = false "
              + " when lower(?1) = 'deleted' then is_deleted = true else ?1 = 'all' end ",
      nativeQuery = true)
  Page<CandidateStatus> findAllByFilterStatus(String status, Pageable pageable);

  @Query(
      value =
          "select id, created_at, created_by, updated_at, last_modified_by, active, description, is_deletable, is_deleted, title from candidate_status "
              + " where (case when lower(?1) = 'active' then active = true and is_deleted = false "
              + " when lower(?1) = 'inactive' then active = false and is_deleted = false "
              + " when lower(?1) = 'deleted' then is_deleted = true else ?1 = 'all' end) and (title ilike '%'||?2||'%') ",
      countQuery =
          "select count(id) from candidate_status "
              + " where (case when lower(?1) = 'active' then active = true and is_deleted = false "
              + " when lower(?1) = 'inactive' then active = false and is_deleted = false "
              + " when lower(?1) = 'deleted' then is_deleted = true else ?1 = 'all' end) and (title ilike '%'||?2||'%') ",
      nativeQuery = true)
  Page<CandidateStatus> findAllByFilterStatusAndFilterAll(
      String status, String filter, Pageable pageable);

  @Query(value = "select count(id) from candidate where status_id = ?1", nativeQuery = true)
  Integer getCandidateByStatusIdOnDeleteStatus(int id);

  @Query(
      "select cs from CandidateStatus cs where cs.active = true and cs.isDeleted = false and cs.id = ?1")
  Optional<CandidateStatus> findByIdAndActiveIsTrueAndDeletedIsFalse(int id);

  @Query(
      value =
          "SELECT jsonb_agg(json_build_object('id', cs.id,'title', cs.title)) FROM candidate_status cs LEFT JOIN mail_configuration mc on cs.id = mc.candidate_status_id where (cs.active = true and is_deleted = false and mc.id is null) and (cs.title ilike '%'||?1||'%' or ?1 = '')",
      nativeQuery = true)
  JsonNode findAllByMailConfigurationNotUsed(String filter);

  @Query(
      value =
          "SELECT id, created_at, created_by, last_modified_by, updated_at, active, description, is_deletable, is_deleted, title from candidate_status"
              + " where is_deleted = false and active = true and (title ilike '%'||?1||'%' or ?1 = '')",
      nativeQuery = true)
  List<CandidateStatus> findAllNoPagination(String filter);

  @Query(
      value = "SELECT count(1) from candidate_status where lower(title) = lower(?1)",
      nativeQuery = true)
  long validateTitle(String title);

  @Query(
      value = "SELECT count(1) from candidate_status where lower(title) = lower(?2) and id != ?1",
      nativeQuery = true)
  long validateTitleOnUpdate(int id, String title);
}
