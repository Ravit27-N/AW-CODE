package com.innovationandtrust.project.service.specification;

import static com.innovationandtrust.utils.commons.CommonValidations.listIsNotEmpty;
import static com.innovationandtrust.utils.commons.CommonValidations.ok;

import com.innovationandtrust.project.model.entity.AbstractBaseEntity_;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.Project_;
import com.innovationandtrust.project.model.entity.Signatory_;
import com.innovationandtrust.share.utils.SpecUtils;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** ProjectSpecification use for query, or filter from project table. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectSpecification {

  /**
   * Search project by name.
   *
   * @param filter refers to the name that client wants to search
   * @return Project
   */
  public static Specification<Project> searchByName(String filter) {
    if (!StringUtils.hasText(filter)) {
      return null;
    }
    return (root, query, cb) ->
        cb.or(
            cb.like(
                AdvancedFilter.replaceSpaces(cb, root.get(Project_.NAME)),
                SpecUtils.likeQuery(filter)));
  }

  /**
   * Search project by created by.
   *
   * @param createdBy refers to id of project creator
   * @return Project
   */
  public static Specification<Project> searchByCreatedBy(Long createdBy) {
    if (!ok(createdBy)) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get(AbstractBaseEntity_.CREATED_BY), createdBy);
  }

  /**
   * Search project with the list of created by.
   *
   * @param createdBy refers to a list of project creator id
   * @return Project
   */
  public static Specification<Project> searchByCreatedBy(List<Long> createdBy) {
    if (!listIsNotEmpty(createdBy)) {
      return null;
    }
    return (root, query, cb) -> root.get(AbstractBaseEntity_.CREATED_BY).in(createdBy);
  }

  /**
   * Search project with the list of userid to.
   *
   * @param userIds refers to a list of user id created projects or assigned projects
   * @return Project Specification
   */
  public static Specification<Project> searchByAssignedToOrCreatedBy(List<Long> userIds) {
    if (!listIsNotEmpty(userIds)) {
      return null;
    }
    return (root, query, cb) ->
        cb.or(
            root.get(Project_.ASSIGNED_TO).in(userIds),
            root.get(AbstractBaseEntity_.CREATED_BY).in(userIds));
  }

  public static Specification<Project> searchByNameAndSignatoryName(String search) {
    if (!StringUtils.hasText(search)) {
      return null;
    }
    return (root, query, cb) -> {
      root.join(Project_.SIGNATORIES);
      return cb.or(
          cb.like(
              AdvancedFilter.replaceSpaces(cb, root.get(Project_.NAME)),
              SpecUtils.likeQuery(search)),
          cb.like(
              AdvancedFilter.replaceSpaces(
                  cb, root.get(Project_.SIGNATORIES).get(Signatory_.FIRST_NAME)),
              SpecUtils.likeQuery(search)),
          cb.like(
              AdvancedFilter.replaceSpaces(
                  cb, root.get(Project_.SIGNATORIES).get(Signatory_.LAST_NAME)),
              SpecUtils.likeQuery(search)),
          cb.like(
              AdvancedFilter.replaceSpaces(
                  cb,
                  cb.concat(
                      root.get(Project_.SIGNATORIES).get(Signatory_.FIRST_NAME),
                      root.get(Project_.SIGNATORIES).get(Signatory_.LAST_NAME))),
              SpecUtils.likeQuery(search)),
          cb.like(
              AdvancedFilter.replaceSpaces(
                  cb,
                  cb.concat(
                      root.get(Project_.SIGNATORIES).get(Signatory_.LAST_NAME),
                      root.get(Project_.SIGNATORIES).get(Signatory_.FIRST_NAME))),
              SpecUtils.likeQuery(search)));
    };
  }

  /**
   * Filter project by step.
   *
   * @param filterSteps refers to a list of steps
   * @return Project
   */
  public static Specification<Project> filterBySteps(List<String> filterSteps) {
    if (!listIsNotEmpty(filterSteps)) {
      return null;
    }
    return (root, query, cb) -> root.get(Project_.STEP).in(filterSteps);
  }

  /**
   * Filter project by status.
   *
   * @param status refers to project status
   * @return Project
   */
  public static Specification<Project> filterByStatus(String status) {
    if (!StringUtils.hasText(status)) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get(Project_.STATUS), status);
  }

  /**
   * Filter project by signatory's email.
   *
   * @param signatoryEmail this email is end-user.
   * @return list of projects
   */
  public static Specification<Project> filterBySignatoryDocument(
      String signatoryEmail, List<String> statuses, String inviteStatus) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(root.get(Project_.SIGNATORIES).get(Signatory_.EMAIL), signatoryEmail),
            cb.or(root.get(Project_.SIGNATORIES).get(Signatory_.DOCUMENT_STATUS).in(statuses)),
            cb.equal(
                root.get(Project_.SIGNATORIES).get(Signatory_.INVITATION_STATUS), inviteStatus));
  }
}
