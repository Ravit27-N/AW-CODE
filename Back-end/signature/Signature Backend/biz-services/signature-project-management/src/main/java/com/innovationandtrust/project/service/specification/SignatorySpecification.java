package com.innovationandtrust.project.service.specification;

import com.innovationandtrust.project.model.entity.Project_;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.model.entity.Signatory_;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.utils.SpecUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** SignatorySpecification use for query, or filter from signatory table. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignatorySpecification {

  /**
   * Search signatory by name.
   *
   * @param filter refers to signatory name
   * @return Signatory
   */
  public static Specification<Signatory> searchByName(String filter) {
    if (!StringUtils.hasText(filter)) {
      return null;
    }
    return (root, query, cb) ->
        cb.or(
            cb.like(
                cb.lower(
                    cb.concat(root.get(Signatory_.FIRST_NAME), root.get(Signatory_.LAST_NAME))),
                SpecUtils.likeQuery(filter)),
            cb.like(cb.lower(root.get(Signatory_.FIRST_NAME)), SpecUtils.likeQuery(filter)),
            cb.like(cb.lower(root.get(Signatory_.LAST_NAME)), SpecUtils.likeQuery(filter)));
  }

  public static Specification<Signatory> findAllByProjectId(Long projectId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Signatory_.PROJECT).get(Project_.ID), projectId);
  }

  public static Specification<Signatory> findByIds(List<Long> ids) {
    return (root, query, cb) -> root.get(Signatory_.ID).in(ids);
  }

  /**
   * Filter project by signatory's email.
   *
   * @param signatoryEmail this email is end-user.
   * @return list of projects
   */
  public static Specification<Signatory> filterByDocumentStatus(
      String signatoryEmail, List<String> statuses, String inviteStatus) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(root.get(Signatory_.EMAIL), signatoryEmail),
            cb.or(root.get(Signatory_.DOCUMENT_STATUS).in(statuses)),
            cb.equal(root.get(Signatory_.INVITATION_STATUS), inviteStatus),
            cb.notEqual(
                root.get(Signatory_.PROJECT).get(Project_.STATUS), ProjectStatus.ABANDON.name()));
  }
}
