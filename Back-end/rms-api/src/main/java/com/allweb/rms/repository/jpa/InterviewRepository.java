package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.dto.AdvanceReportResponse;
import com.allweb.rms.entity.dto.InterviewResponse;
import com.allweb.rms.entity.jpa.Interview;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface InterviewRepository extends JpaRepository<Interview, Integer> {
  String VALUES_SELECTED =
      "new com.allweb.rms.entity.dto.InterviewResponse(i,i.interviewStatus.name,i.candidate.id,concat(i.candidate.salutation,' ',i.candidate.firstname,' ',i.candidate.lastname), i.candidate.photoUrl, count(r.id),rs.interview.id)";
  String GROUP_BY =
      "group by i.id, rs.interview.id, i.candidate.firstname, i.candidate.lastname, i.candidate.salutation, i.candidate.photoUrl, i.interviewStatus.name";
  String LEFT_JOIN =
      "left join Result rs on i.id = rs.interview.id left join Reminder r on i.id = r.id ";

  @Query(
      "select "
          + VALUES_SELECTED
          + " from Interview i "
          + LEFT_JOIN
          + "where i.id = ?1 and i.isDelete = false and i.candidate.isDeleted = false "
          + GROUP_BY)
  Optional<InterviewResponse> getInterviewResponse(int id);

  List<Interview> getAllByTitle(String title);

  @Query(
      value =
          "select count(i) from interview i LEFT JOIN candidate c on i.candidate_id = c.id WHERE c.is_deleted = false and i.is_delete = false",
      nativeQuery = true)
  long countAllByDeleteFalseAndCandidateDeleteFalse();

  // report interview on graph by year
  @Query(
      value =
          "WITH months AS ( SELECT generate_series(1, 12) num), "
              + "graph as (select extract(month from i.date_time) m ,s.\"name\" \"name\", coalesce(COUNT(i.interview_status_id), 0) isCount \n"
              + "from interview i "
              + "left join interview_status s on i.interview_status_id = s.id \n"
              + "left join candidate c on i.candidate_id = c.id "
              + " where cast(extract(year from i.date_time) as text) = ?1 and i.is_delete = false and c.is_deleted = false "
              + "group by s.name,extract(month from i.date_time) order by extract(month from i.date_time)),countMonth as (select t.name,jsonb_agg(case when t.num = g.m then isCount else 0 end) sMonth from (select DISTINCT  m.num,dataInterviewStatus.name from months m ,(select * from graph) dataInterviewStatus group by dataInterviewStatus.name,m.num order by m.num) t\n"
              + "left join graph g on t.num = g.m and t.name = g.name\n"
              + "group by t.name)\n"
              + "select jsonb_agg(t.allCount) graph from (select jsonb_build_object('label',is2.name,'data',case when sMonth is null then '[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]' else smonth end) allCount from interview_status is2\n"
              + "left join countMonth cm on cm.name = is2.name\n"
              + "where is2.is_active = true "
              + "group by is2.name,sMonth) t;",
      nativeQuery = true)
  Object reportInterviewsOnGraphStatusByYear(String year);

  @Query(
      value =
          "with months as ( select generate_series(1, 12) num), \n"
              + "total as (select jsonb_build_object('data',jsonb_agg(dataInterview.isCount),'label','Total') from (\n"
              + "with months as ( select generate_series(1, 12) num) select coalesce(COUNT(i), 0) isCount from months m left join interview i on m.num = extract(month from date_time) and cast(extract(year from date_time) as text) = ?1 and i.is_delete = false and (select c.is_deleted from candidate c where id = i.candidate_id) = false  group by num order by num) dataInterview )\n"
              + "select * from total",
      nativeQuery = true)
  Object reportInterviewOnGraphMonthlyByYear(String year);

  Integer countByCandidateIdAndIsDeleteIsFalse(int candidateId);

  Interview findFirst1ByCandidateIdAndIsDeleteFalseOrderByDateTimeDesc(int candidateId);

  Interview findByIdAndIsDeleteFalseAndCandidateIsDeletedIsFalse(int candidateId);

  @Query(
      value = "select distinct i.candidate_id from interview i where i.is_delete=false",
      nativeQuery = true)
  List<Integer> findByCandidateIdAndIsDeleteIsFalse();

  @Query(
      value =
          "select new com.allweb.rms.entity.dto.AdvanceReportResponse(c.id,i.title,s.name) from Interview i "
              + "left join Candidate c on i.candidate.id = c.id "
              + "left join InterviewStatus s on i.interviewStatus.id = s.id "
              + "where i.isDelete=false and c.isDeleted=false and i.dateTime between cast(?1 as timestamp) and cast(?2 as timestamp) ")
//              + // between ?1 and ?2
//              "and i.createdAt=(select max(i.createdAt) from Interview i where c.id= i.candidate.id)")
  ArrayList<AdvanceReportResponse> findAllPosition(Date from, Date to);
}
