package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.dto.DemandResponse;
import com.allweb.rms.entity.jpa.Demand;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface DemandRepository extends JpaRepository<Demand, Integer> {

  @Query(
      value =
          "SELECT de.id,de.project_id,de.created_at,de.last_modified_by,de.updated_at,de.created_by,de.active,de.nb_candidates,de.nb_required "
              + ",de.dead_line,de.experience_level,de.is_deleted,de.job_description_id,de.status,cast(json_build_object('id',p.id,'name',p.name) as json) as project "
              + ",cast(json_build_object('id',job.id,'title',job.title) as json) as job_description "
              + "FROM demand de "
              + "LEFT JOIN project p ON de.project_id = p.id "
              + "LEFT JOIN job_description job ON de.job_description_id = job.id WHERE de.id =?1",
      nativeQuery = true)
  Map<String, Object> findDemandById(int id);

  @Query(
      value = "SELECT count(d) from demand d where d.project_id=?1 and d.job_description_id=?2",
      nativeQuery = true)
  int checkValidateCreateDemand(int project_id, int description_id);

  @Query(
      value =
          "SELECT count(d) from demand d where d.project_id= ?1 and d.job_description_id= ?2 and d.id!=?3",
      nativeQuery = true)
  int checkValidateUpdateDemand(int project_id, int description_id, int demand_id);

  @Query(
      "SELECT new com.allweb.rms.entity.dto.DemandResponse(de,p.id,p.name,job.id,job.title) from Demand de "
          + "left join Project p "
          + "on de.project.id=p.id "
          + "left join JobDescription job "
          + "on de.jobDescription.id=job.id where de.isDeleted=false and de.active=true "
          + "order by de.deadLine asc")
  Page<DemandResponse> findByActiveIsTrue(Pageable pageable);

  @Query(
      "SELECT new com.allweb.rms.entity.dto.DemandResponse(de,p.id,p.name,job.id,job.title) from Demand de "
          + "left join Project p "
          + "on de.project.id=p.id "
          + "left join JobDescription job "
          + "on de.jobDescription.id=job.id where de.isDeleted=false"
          + " order by de.createdAt desc")
  Page<DemandResponse> getAll(Pageable pageable);

  @Query(
      "SELECT new com.allweb.rms.entity.dto.DemandResponse(de,p.id,p.name,job.id,job.title) from Demand de "
          + "left join Project p "
          + "on de.project.id=p.id "
          + "left join JobDescription job "
          + "on de.jobDescription.id=job.id where de.isDeleted=true"
          + " order by de.createdAt desc")
  Page<DemandResponse> getAllByDeletedIsTrue(Pageable pageable);

  @Query(
      "SELECT new com.allweb.rms.entity.dto.DemandResponse(de,p.id,p.name,job.id,job.title) from Demand de "
          + "left join Project p "
          + "on de.project.id=p.id "
          + "left join JobDescription job "
          + "on de.jobDescription.id=job.id where "
          + "(lower(p.name) like %?1% or lower(job.title) like %?1% or lower(job.description) like %?1%)"
          + "and de.isDeleted=false")
  Page<DemandResponse> fetchAllByFilteringField(String filter, Pageable pageable);

  @Query(
      "SELECT new com.allweb.rms.entity.dto.DemandResponse(de,p.id,p.name,job.id,job.title) from Demand de "
          + "left join Project p "
          + "on de.project.id=p.id "
          + "left join JobDescription job "
          + "on de.jobDescription.id=job.id where "
          + "(lower(p.name) like %?1% or lower(p.description) like %?1% or lower(job.title) like %?1% or lower(job.description) like %?1%)"
          + "and de.isDeleted=true")
  Page<DemandResponse> fetchAllByFilteringFieldAndDeletedIsTrue(String filter, Pageable pageable);

  @Query(
      value = "select de.nb_candidates from Demand de where de.id=?1 and de.is_deleted=false",
      nativeQuery = true)
  String findNbCandidate(int id);

  @Query(value = "select de.nbRequired from Demand de where de.id=?1")
  int findNbRequired(int id);

  @Query(value = "select de.status from Demand de where de.id=?1", nativeQuery = true)
  boolean findStatus(int id);

  @Query(value = "select de.nb_candidates from demand de", nativeQuery = true)
  ArrayList<String> findAllNbCandidate();

  @Query(
      value = "select count(de.project_id) from demand de where de.project_id = ?1",
      nativeQuery = true)
  int findProjectNameById(int id);
}
