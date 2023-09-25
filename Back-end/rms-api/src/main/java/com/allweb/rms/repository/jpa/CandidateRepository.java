package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.Candidate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
  String FROM_CANDIDATE = " from candidate ca ";
  String JOIN_TABLE =
      " left join candidate_status cs on ca.status_id = cs.id"
          + " left join interview i on ca.id = i.candidate_id and i.id = (select max(id) from interview where candidate_id = ca.id) and is_delete = false "
          + " left join reminder r on ca.id = r.candidate_id and deleted = false"
          + " left join result r2 on i.id = r2.interview_id"
          + " left join candidate_university cu on ca.id = cu.candidate_id "
          + " left join (select id,name from university) u on cu.university_id = u.id ";
  // select fields of find all candidates
  String SELECT_FIELD_IN_FIND_ALL =
      "select  ca.id,ca.salutation,cs.id statusId,ca.firstname,ca.lastname,ca.gpa,ca.gender,ca.email,ca.photo_url as photoUrl,ca.telephone,ca.active,"
          + "ca.priority,ca.created_at as createdAt,ca.updated_at as updatedAt,ca.description,ca.is_deleted as isDeleted,"
          + " (select count(candidate_id) from interview WHERE candidate_id = ca.id and is_delete = false) as countInterview, (select count(candidate_id) from reminder WHERE candidate_id = ca.id and deleted = false) as countReminder,"
          + " cast(json_build_object('id', cs.id,'title',cs.title,'active', cs.active) as json) as candidateStatus, "
          + " case when i.candidate_id is null then '{}' else cast(json_build_object('id',i.id,'title',i.title,'lastInterview',"
          + "cast(i.date_time as timestamp),'result',case when r2.interview_id is null then '{}' else json_build_object('average',round(cast(r2.average as numeric),2)) end) as json) end interviews ,"
          + " CASE WHEN cu.candidate_id is NULL THEN '[]' ELSE cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) end universities";

  // find all candidates not filtered on the text box
  String FIND_ALL_CANDIDATES =
      FROM_CANDIDATE
          + JOIN_TABLE
          + " where ca.is_deleted = ?1 and (cs.title = ?2 or ?2  = '') "
          +
          // check records when clicked on checkboxes
          " and (case when lower(?3) = 'reminder' then r.candidate_id is not null "
          + " when lower(?3) = 'interview' then i.candidate_id is not null "
          + " when lower(?3) = 'both' then i.candidate_id is not null and r.candidate_id is not null else ?3 ='' end )";

  // find all candidates and filters
  String FIND_ALL_CANDIDATES_AND_FILTERS =
      FROM_CANDIDATE
          + JOIN_TABLE
          + " where ca.is_deleted = ?2 and (cs.title = ?3 or ?3  = '') and "
          +
          // filter records list on table
          "(concat(ca.salutation,' ',ca.firstname,' ',ca.lastname) ilike '%'||?1||'%' or ca.telephone ilike '%'||?1||'%' "
          + " or cast(ca.gpa as text) ilike '%'||?1||'%' or to_char(ca.created_at, 'yyyy-MM-dd') ilike '%'||?1||'%' or ca.gender ilike '%'||?1||'%' "
          + " or to_char(i.date_time, 'yyyy-MM-dd') ilike '%'||?1||'%' or ca.priority ilike '%'||?1||'%' or cs.title ilike '%'||?1||'%' or u.name ilike '%'||?1||'%')"
          +
          // check records when clicked on checkboxes
          " and (case when lower(?4) = 'reminder' then r.candidate_id is not null "
          + " when lower(?4) = 'interview' then i.candidate_id is not null "
          + " when lower(?4) = 'both' then i.candidate_id is not null and r.candidate_id is not null else ?4 ='' end )";
  // find all candidates by advanced search
  String FIND_ALL_CANDIDATES_AS_ADVANCED_SEARCH =
      FROM_CANDIDATE
          + JOIN_TABLE
          + " where ca.is_deleted = false and (concat(ca.salutation,' ',ca.firstname,' ',ca.lastname) ilike '%'||cast(?1 as text)||'%' or ?1 is null) "
          + " and  (u.name ilike '%'||cast(?2 as text)||'%' or ?2 is null) "
          + " and (lower(ca.gender) = lower(cast(?3 as text)) or ?3 is null) "
          + " and (ca.gpa >= ?4 or ?4 = 0) and (i.title ilike '%'||cast(?5 as text)||'%' or ?5 is null)";
  // report candidate by search data time of interview
  String REPORT_CANDIDATE =
      " from candidate c "
          + " left join interview i on c.id = i.candidate_id and i.id = (select max(id) from interview where candidate_id = c.id) and is_delete = false "
          + " left join result r on i.id = r.interview_id"
          + " left join candidate_university cu on c.id = cu.candidate_id "
          + " left join university u on cu.university_id = u.id "
          +
          // check by between date or date range and is_delete
          " where c.is_deleted = false and ((to_char(i.date_time,'yyyy-MM-dd') between ?1 and ?2 or to_char(c.created_at,'yyyy-MM-dd') between ?1 and ?2)) and "
          +
          // filter data show on table
          "(?3 = '' or (concat(c.salutation,' ',c.firstname,' ',c.lastname) ilike '%'||?3||'%' or cast(gpa as text) ilike '%'||?3||'%'"
          + "or c.telephone ilike '%'||?3||'%' or r.flexibility ilike '%'||?3||'%' or r.remark ilike '%'||?3||'%' or r.score ilike '%'||?3||'%' or lower(c.gender) = lower(?3)"
          + " or r.oral ilike '%'||?3||'%' or r.english ilike '%'||?3||'%' or r.logical ilike '%'||?3||'%' or i.title ilike '%'||?3||'%' or cast(r.average as text) ilike '%'||?3||'%' "
          + " or to_char(i.date_time,'dd-MM-yyyy HH12:MI AM') ilike '%'||?3||'%' or u.name ilike '%'||?3||'%' or c.priority ilike '%'||?3||'%'))";
  // group by query of find all candidates
  String GROUP_BY_FIND_ALL_CANDIDATES =
      " group by ca.id,cs.id,i.id,i.candidate_id,r.candidate_id,r2.id,cu.candidate_id";

  // find all candidates by deleted is false
  @Query(
      value = SELECT_FIELD_IN_FIND_ALL + FIND_ALL_CANDIDATES + GROUP_BY_FIND_ALL_CANDIDATES,
      countQuery = "select count(DISTINCT ca)" + FIND_ALL_CANDIDATES,
      nativeQuery = true)
  @QueryHints(
      value = {
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "javax.persistence.cache.retrieveMode", value = "USE"),
        @QueryHint(name = "javax.persistence.cache.storeMode", value = "REFRESH")
      })
  Page<Map<String, Object>> findAllCandidates(
      boolean isDelete, String status, String filterReminderOrInterview, Pageable pageable);

  // find all candidates by deleted is false and has filter data
  @Query(
      value =
          SELECT_FIELD_IN_FIND_ALL + FIND_ALL_CANDIDATES_AND_FILTERS + GROUP_BY_FIND_ALL_CANDIDATES,
      countQuery = "select count(DISTINCT ca)" + FIND_ALL_CANDIDATES_AND_FILTERS,
      nativeQuery = true)
  @QueryHints(
      value = {
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "javax.persistence.cache.retrieveMode", value = "USE"),
        @QueryHint(name = "javax.persistence.cache.storeMode", value = "REFRESH")
      })
  Page<Map<String, Object>> findAllCandidatesAndFilter(
      String filter,
      boolean isDeleted,
      String status,
      String filterReminderOrInterview,
      Pageable pageable);

  // advanced search
  @Query(
      value =
          SELECT_FIELD_IN_FIND_ALL
              + FIND_ALL_CANDIDATES_AS_ADVANCED_SEARCH
              + GROUP_BY_FIND_ALL_CANDIDATES,
      countQuery = "select count(DISTINCT ca)" + FIND_ALL_CANDIDATES_AS_ADVANCED_SEARCH,
      nativeQuery = true)
  @QueryHints(
      value = {
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "javax.persistence.cache.retrieveMode", value = "USE"),
        @QueryHint(name = "javax.persistence.cache.storeMode", value = "REFRESH")
      })
  Page<Map<String, Object>> findAllAsAdvancedSearch(
      String name, String from, String gender, float gpa, String position, Pageable pageable);

  // report
  @Query(
      value =
          "select c.id,c.firstname,c.lastname,c.salutation,c.telephone,round(cast(c.gpa as numeric),1) gpa,c.gender,c.active,c.created_at createdAt,c.updated_at updatedAt,c.photo_url as photoUrl,c.email,c.status_id statusId,"
              + " c.priority,"
              + "case when i.candidate_id is null then '{}' else cast(json_build_object('id',i.id,'title',i.title,'description',i.description,'dateTime',cast(i.date_time as timestamp),"
              + " 'result',case when r.interview_id is null then '{}' "
              + " else json_build_object('id',r.id,'score',cast(r.score as json),'average',round(cast(r.average as numeric),2),'oral',oral,'english',english,'logical',logical,'flexibility',flexibility) end ) as json) end interviews, "
              + " CASE WHEN cu.candidate_id is NULL THEN '[]' ELSE cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) end universities"
              + REPORT_CANDIDATE
              + " group by c.id,i.id,i.candidate_id,r.interview_id,r.id,cu.candidate_id",
      countQuery = "select count(DISTINCT c)" + REPORT_CANDIDATE,
      nativeQuery = true)
  @QueryHints(
      value = {
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "javax.persistence.cache.retrieveMode", value = "USE"),
        @QueryHint(name = "javax.persistence.cache.storeMode", value = "REFRESH")
      })
  Page<Map<String, Object>> reportCandidates(
      String from, String to, String filter, Pageable pageable);

  // view detail candidate by id
  @Query(
      value =
          "select ca.id,ca.firstname,ca.lastname,ca.gender, ca.date_of_birth, ca.year_of_experience,ca.email,ca.salutation,ca.photo_url as photoUrl,ca.status_id statusId,ca.telephone,ca.gpa,"
              + " ca.created_at createdAt,ca.updated_at updatedAt,ca.created_by,ca.active,"
              + "ca.priority,ca.description,ca.is_deleted as isDeleted,"
              + " CASE WHEN ca.status_id is NULL THEN '[]' ELSE cast(json_build_object('id', cs.id,'title',cs.title,'active', cs.active) as json) END as candidateStatus,"
              + " CASE WHEN cu.candidate_id is NULL THEN '[]' ELSE cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) end universities"
              + " from candidate ca "
              + " left join candidate_status cs on ca.status_id = cs.id "
              + " left join candidate_university cu on ca.id = cu.candidate_id "
              + " left join university u on cu.university_id = u.id"
              + " where ca.id = ?1"
              + " group by cs.id,ca.id,cu.candidate_id",
      nativeQuery = true)
  Map<String, Object> viewCandidateById(int id);

  // get interview to set in candidate
  @Query(
      value =
          "select cast(json_agg(json_build_object('id',i.id,'title',i.title,'description',i.description,'createdBy',i.created_by,'dateTime', i.date_time at time zone 'utc',"
              + " 'result',case when r.interview_id is null then '{}' else json_build_object('id',r.id,'average',case when r.average is not null then round(cast(r.average as numeric), 2) else (cast(json(r.score)#>>'{quiz,score}' as float) + cast(json(r.score)#>>'{coding,score}' as float) / 2.0) end,"
              + " 'quizScore', jsonb(jsonb(r.score)->'quiz')->'score', 'codingScore', jsonb(jsonb(r.score)->'coding')->'score','oral',oral,'english',english,'logical',logical,'flexibility',flexibility) end )) as json) interviews from interview i "
              + " left join result r on i.id = r.interview_id"
              + " where i.candidate_id = ?1 and is_delete = false",
      nativeQuery = true)
  Object getInterviewsByCandidateId(int id);

  // get activities to set in candidate
  @Query(
      value =
          "select cast(json_agg(json_build_object('id',id,'title',title,'userId',user_id,'description',description,'updatedAt',cast(created_at as timestamp),'updatedAt',cast(updated_at as timestamp))) as json) activities "
              + " from activity where candidate_id = ?1",
      nativeQuery = true)
  Object getActivityByCandidateId(int id);

  @Query(value = "select count(DISTINCT id) from candidate where email = ?1", nativeQuery = true)
  Long validateEmailCandidate(String email);

  @Query(
      value =
          "select id,firstname,lastname,salutation from candidate c "
              + "where case when ?1 = '' then ?1 = '' "
              + " else concat(salutation,' ',firstname,' ',lastname) ilike '%'||?1||'%' end ",
      nativeQuery = true)
  Page<Map<String, Object>> findAllCandidatesOnSelectBox(String filter, Pageable pageable);

  @Query(
      value =
          "select c.id,c.firstname,c.lastname,c.salutation,c.telephone,c.gpa,c.date_of_birth, c.year_of_experience,c.gender,c.photo_url,c.email,c.status_id,"
              + " c.priority,c.created_at,c.updated_at, c.last_modified_by, c.created_by, c.description,c.is_deleted, c.active,"
              + " cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) universities,"
              + "cast(json_build_object('id', cs.id,'title',cs.title,'active', cs.active) as json) as candidate_status"
              + " from candidate_university cu "
              + " left join candidate c on c.id = cu.candidate_id"
              + " left join candidate_status cs on c.status_id = cs.id "
              + " left join university u on u.id = cu.university_id"
              + " where c.id = ?1"
              + " group by c.id,cs.id",
      nativeQuery = true)
  Map<String, Object> findCandidateById(int id);

  @Query(
      value =
          "select c.id,concat(c.salutation,' ',c.firstname,' ',c.lastname) as fullname,c.firstname,c.lastname,c.salutation,c.telephone,c.gpa,c.date_of_birth, c.year_of_experience,c.gender,c.photo_url,c.email,c.status_id,"
              + " c.priority,c.created_at,c.updated_at, c.last_modified_by, c.created_by, c.description,c.is_deleted, c.active,"
              + " cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) universities,"
              + "cast(json_build_object('id', cs.id,'title',cs.title,'active', cs.active) as json) as candidate_status,"
              + "cast(json_agg(DISTINCT jsonb_build_object('id', i.id,'title',i.title)) as json) interviews"
              + " from candidate_university cu"
              + " left join candidate c on c.id = cu.candidate_id"
              + " left join candidate_status cs on c.status_id = cs.id "
              + " left join interview i on c.id=i.candidate_id"
              + " left join university u on u.id = cu.university_id"
              + " where c.id in ?1 and cs.title not in ?2"
              + " group by c.id,cs.id order by c.created_at desc",
      nativeQuery = true)
  Page<Map<String, Object>> findCandidateByIds(
      List<Integer> ids, List<String> candidateStatus, Pageable pageable);

  // For Dashboard
  @Query(value = "select count(1) from candidate c where c.is_deleted = false", nativeQuery = true)
  long countCandidatesByDeletedIsFalse();

  @Query(
      value =
          "select json_agg(countGender.c) from (select json_build_object('name', gender,'number', count(*)) as c from candidate where is_deleted = false group by gender) countGender",
      nativeQuery = true)
  Map<String, Object> countCandidatesGroupByGender();

  @Query(
      value =
          "select json_agg(countStatus.cs) from (select cs.title,json_build_object('label',cs.title,'number', COALESCE(COUNT(c.status_id),0)) as cs "
              + "from candidate_status cs LEFT JOIN candidate c on cs.id = c.status_id and c.is_deleted = false "
              + "where cs.active = true and cs.is_deleted = false  GROUP BY cs.title) countStatus",
      nativeQuery = true)
  Map<String, Object> countCandidatesGroupsByStatus();

  List<Candidate> findByIsDeletedIsFalseAndActiveIsTrueAndGpaGreaterThanEqualOrderByGpaDesc(
      float gpa, Pageable pageable);

  @Query("select c from Candidate c where c.isDeleted = false and c.id = ?1")
  Optional<Candidate> findByIdAndDeletedIsFalse(int id);

  @Query(
      value =
          "SELECT count(a) from activity a left join candidate c on c.id = a.candidate_id where c.is_deleted = false",
      nativeQuery = true)
  long countActivitiesByCandidateDeletedIsFalse();

  @Query(
      value =
          "select c.id, concat(c.salutation,' ',c.firstname,' ',c.lastname) as fullName, c.gender, c.gpa, extract(YEAR FROM age(now(),c.date_of_birth)) AS Age, c.year_of_experience, "
              + "cast(json_agg(DISTINCT jsonb_build_object('id', i.id,'position',i.title,'datetime',i.date_time at time zone 'utc')) as json) interviews,"
              + "cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) universities, s.name as interviewStatus,"
              + "jsonb(jsonb(r.score)->'quiz')->'score' as quiz,jsonb(jsonb(r.score)->'quiz')->'max' as maxQuiz, jsonb(jsonb(r.score)->'coding')->'score' as coding,"
              + "jsonb(jsonb(r.score)->'coding')->'max' as maxCoding, r.english, r.flexibility, r.logical, r.oral, r.average, i.description as remark"
              + " from candidate c "
              + "left join candidate_university cu on c.id = cu.candidate_id "
              + "left join university u on u.id = cu.university_id "
              + "left join interview i on c.id= i.candidate_id "
              + "left join interview_status s on i.interview_status_id = s.id "
              + "left join result r on i.id = r.interview_id "
              + "where i.is_delete=false and c.is_deleted=false and i.date_time between cast(?1 as timestamp) and cast(?2 as timestamp) "
//              + // between ?1 and ?2 and c.firstname like '%'||?4||'%'
                  +  "and c.id=i.candidate_id  and c.id in (?3) and i.title not like '%- Intern%'  "
               // concat(c.salutation,' ',c.firstname,' ',c.lastname) ilike '%'||?4||'%'
          + "group by c.id, concat(c.salutation,' ',c.firstname,' ',c.lastname), c.gender" +
                  ", c.gpa, extract(YEAR FROM age(now(),c.date_of_birth)), c.year_of_experience, " +
                  "s.name,r.score, r.english, r.flexibility, r.logical, r.oral, r.average, i.description", nativeQuery = true)

  Page<Map<String, Object>> findAllCandidatesAdvanceReport(
      Date from, Date to, Set<Integer> candidateIds, Pageable pageable);

  @Query(
          value =
                  "select c.id, concat(c.salutation,' ',c.firstname,' ',c.lastname) as fullName, c.gender, c.gpa, extract(YEAR FROM age(now(),c.date_of_birth)) AS Age, c.year_of_experience, "
                          + "cast(json_agg(DISTINCT jsonb_build_object('id', i.id,'position',i.title,'datetime',i.date_time at time zone 'utc')) as json) interviews,"
                          + "cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) universities, s.name as interviewStatus,"
                          + "jsonb(jsonb(r.score)->'quiz')->'score' as quiz,jsonb(jsonb(r.score)->'quiz')->'max' as maxQuiz, jsonb(jsonb(r.score)->'coding')->'score' as coding,"
                          + "jsonb(jsonb(r.score)->'coding')->'max' as maxCoding, r.english, r.flexibility, r.logical, r.oral, r.average, i.description as remark"
                          + " from candidate c "
                          + "left join candidate_university cu on c.id = cu.candidate_id "
                          + "left join university u on u.id = cu.university_id "
                          + "left join interview i on c.id= i.candidate_id "
                          + "left join interview_status s on i.interview_status_id = s.id "
                          + "left join result r on i.id = r.interview_id "
                          + "where i.is_delete=false and c.is_deleted=false and i.date_time between cast(?1 as timestamp) and cast(?2 as timestamp) "
//              + // between ?1 and ?2 and c.firstname like '%'||?4||'%'
                          +  "and c.id=i.candidate_id  and c.id in (?3) and i.title like '%- Intern%'  "
                          + "group by c.id, concat(c.salutation,' ',c.firstname,' ',c.lastname), c.gender" +
                          ", c.gpa, extract(YEAR FROM age(now(),c.date_of_birth)), c.year_of_experience, " +
                          "s.name,r.score, r.english, r.flexibility, r.logical, r.oral, r.average, i.description", nativeQuery = true)

  Page<Map<String, Object>> findAllCandidatesAdvanceReportIntern(
          Date from, Date to, Set<Integer> candidateIds, Pageable pageable);
//   +  "and c.id=i.candidate_id  and c.id in (?3) and i.title like '%- Intern%'  "
//  @Query(
//      value =
//          "select c.id, concat(c.salutation,' ',c.firstname,' ',c.lastname) as fullName, c.gender, c.gpa, extract(YEAR FROM age(now(),c.date_of_birth)) AS Age, c.year_of_experience, "
//              + "cast(json_agg(DISTINCT jsonb_build_object('id', i.id,'position',i.title,'datetime',i.date_time at time zone 'utc')) as json) interviews,"
//              + "cast(json_agg(DISTINCT jsonb_build_object('id',u.id,'name',u.name)) as json) universities, s.name as interviewStatus,"
//              + "jsonb(jsonb(r.score)->'quiz')->'score' as quiz,jsonb(jsonb(r.score)->'quiz')->'max' as maxQuiz, jsonb(jsonb(r.score)->'coding')->'score' as coding,"
//              + "jsonb(jsonb(r.score)->'coding')->'max' as maxCoding, r.english, r.flexibility, r.logical, r.oral, r.average, i.description as remark"
//              + " from candidate c "
//              + "left join candidate_university cu on c.id = cu.candidate_id "
//              + "left join university u on u.id = cu.university_id "
//              + "left join interview i on c.id= i.candidate_id "
//              + "left join interview_status s on i.interview_status_id = s.id "
//              + "left join result r on i.id = r.interview_id "
//              + "where i.is_delete=false and c.is_deleted=false and i.date_time between cast(?1 as timestamp) and cast(?2 as timestamp) "
//              + // between ?1 and ?2 and c.firstname like '%'||?4||'%'
//              "and i.created_at=(select i.created_at from interview i where c.id=i.candidate_id) and c.id in (?3) "
//              + // concat(c.salutation,' ',c.firstname,' ',c.lastname) like '%'||?4||'%'
//              "group by c.id, concat(c.salutation,' ',c.firstname,' ',c.lastname), c.gender, c.gpa, extract(YEAR FROM age(now(),c.date_of_birth)), c.year_of_experience,s.name,r.score, "
//              + "r.english, r.flexibility, r.logical, r.oral, r.average, i.description",
//      nativeQuery = true)
//  List<Map<String, Object>> findAllCandidatesAdvanceReport(
//      Date from, Date to, Set<Integer> candidateIds);

  @Query(
      value =
          "SELECT c FROM Candidate c INNER JOIN Interview i ON c.id = i.candidate.id "
              + "WHERE i.id = 4")
  Optional<Candidate> findCandidateByInterviewId(@Param("id") int id);
}
