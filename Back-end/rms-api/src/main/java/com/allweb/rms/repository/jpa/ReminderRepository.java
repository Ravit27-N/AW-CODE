package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.Reminder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {

  @Query(
      value =
          "select count(r.id) from reminder r "
              +
              // join clause
              "inner join reminder_type rt on r.reminder_type_id = rt.id left join candidate c on r.candidate_id = c.id left join interview i on r.interview_id = i.id "
              +
              // where clause
              "where r.deleted = false and "
              + "case when r.interview_id is not null then (i.is_delete = false and (select count(i2.id) from interview i2 inner join candidate c2 on i2.candidate_id = c2.id and c2.is_deleted = false and i2.id = r.interview_id) > 0) "
              + "when r.candidate_id is not null then c.is_deleted = false "
              + "else true end",
      nativeQuery = true)
  long countAllByDeleteFalse();

  Optional<Reminder>  findByIdAndDeletedIsFalse(int id);

  void deleteByInterviewId(int interviewId);

  int countByInterviewIdAndActiveIsTrueAndDeletedIsFalseAndIsSendIsFalse(int interviewId);

  int countByCandidateIdAndActiveIsTrueAndDeletedIsFalseAndIsSendIsFalse(int candidateId);

  //  @Query("SELECT r.type, r.")
  //  List<Reminder> findByReminderTypeIdAndInterviewId(String reminderType, int interviewId);

  @Query("SELECT r FROM Reminder r WHERE r.reminderType.id = ?1 and r.interview.id=?2")
  List<Reminder> findByReminderTypeIdAndInterviewId(String reminderType, int interviewId);

  List<Reminder> findByReminderTypeIdAndCandidateId(String reminderType, int candidateId);

  boolean existsByCandidateId(int id);
}
