package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.dto.MailTemplateDTO;
import com.allweb.rms.entity.jpa.MailTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface MailTemplateRepository extends JpaRepository<MailTemplate, Integer> {
  String VALUE_SELECTED =
      "new com.allweb.rms.entity.dto.MailTemplateDTO(m.id,m.subject,m.body,m.active,m.deleted,m.isDeletable,m.createdAt,m.updatedAt)";

  Optional<MailTemplate> findByIdAndDeletedIsFalse(int id);

  int deleteByIdAndIdNotIn(int id, Collection<Integer> id2);

  @Query(
      "select "
          + VALUE_SELECTED
          + " from MailTemplate m where m.active = false and lower(m.subject) like %?1% and m.deleted = false ")
  Page<MailTemplateDTO> findAllByActiveIsFalseAndFilter(String filter, Pageable pageable);

  Page<MailTemplate> findAllByActiveIsFalseAndDeletedIsFalse(Pageable pageable);

  @Query(
      "select "
          + VALUE_SELECTED
          + " from MailTemplate m where m.active=true and lower(m.subject) like %?1% and m.deleted = false ")
  Page<MailTemplateDTO> findAllByActiveIsTrueWithFilter(String filter, Pageable pageable);

  Page<MailTemplate> findAllByActiveIsTrueAndDeletedIsFalse(Pageable pageable);

  @Query(
      "select "
          + VALUE_SELECTED
          + " from MailTemplate m where m.deleted = true and lower(m.subject) like %?1%")
  Page<MailTemplateDTO> findAllByDeletedIsTrueWithFilter(String filter, Pageable pageable);

  Page<MailTemplate> findAllByDeletedIsTrue(Pageable pageable);

  @Query("select " + VALUE_SELECTED + " from MailTemplate m where lower(m.subject) like %?1% ")
  Page<MailTemplateDTO> findAllBySubject(String subject, Pageable pageable);

  @Query(
      "select "
          + VALUE_SELECTED
          + " from MailTemplate m where m.deleted = false and lower(m.subject) = ?1")
  Optional<MailTemplateDTO> findBySubjectAndDeletedIsFalse(String subject);
}
