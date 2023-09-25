package com.tessi.cxm.pfl.ms5.service.specification;


import com.tessi.cxm.pfl.ms5.entity.Client_;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.ProfileDetails;
import com.tessi.cxm.pfl.ms5.entity.ProfileDetails_;
import com.tessi.cxm.pfl.ms5.entity.Profile_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.List;
import javax.persistence.criteria.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileSpecification {

  public static final String STRING_LIKE_FORMAT = "%s%s%s";

  public static Specification<Profile> containName(String name) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")),
            String.format(STRING_LIKE_FORMAT, "%", name.toLowerCase(), "%")));
  }

  public static Specification<Profile> containDisplayName(String displayName) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("displayName")),
            String.format(STRING_LIKE_FORMAT, "%", displayName.toLowerCase(), "%")));
  }

  public static Specification<Profile> containProfileId(List<Long> profileIds) {
    return (root, query, criteriaBuilder) -> root.get(Profile_.ID).in(profileIds);
  }

  public static Specification<Profile> equalFunctionalKey(String functionalKey) {

    return (root, query, criteriaBuilder) -> {
      final Path<ProfileDetails> profileDetails = root.get(Profile_.PROFILE_DETAILS);
      return criteriaBuilder.equal(profileDetails.get(ProfileDetails_.FUNCTIONALITY_KEY), functionalKey.toLowerCase());
    };
  }


  /**
   * Handle the specification for select flow document history by list of created by.
   *
   * @param users refer to list of created by.
   */
  public static Specification<Profile> containsIn(List<String> users) {
    return SpecificationUtils.in(Profile_.createdBy, users);
  }

  public static Specification<Profile> equalClientId(Long clientId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Profile_.CLIENT).get(Client_.ID), clientId);
  }
  public static Specification<Profile> ownerIdNull() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(Profile_.OWNER_ID));
  }

  public static Specification<Profile> equalCreatedBy(String createdBy) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Profile_.CREATED_BY), createdBy);
  }


  public static Specification<Profile> containsClientIds(List<Long> clientIds) {
    return (root, query, criteriaBuilder) -> root.get(Profile_.CLIENT).get(Client_.ID).in(clientIds);
  }

  public static Specification<Profile> containOwnerIds(List<Long> ownerIds) {
    return (root, query, criteriaBuilder) -> root.get(Profile_.OWNER_ID).in(ownerIds);
  }
}
