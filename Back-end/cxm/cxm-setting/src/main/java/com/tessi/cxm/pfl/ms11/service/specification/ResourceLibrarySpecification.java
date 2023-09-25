package com.tessi.cxm.pfl.ms11.service.specification;

import com.tessi.cxm.pfl.ms11.entity.BaseEntity_;
import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary;
import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary_;
import com.tessi.cxm.pfl.ms11.entity.ResourceTypeTranslate;
import com.tessi.cxm.pfl.ms11.entity.ResourceTypeTranslate_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceLibrarySpecification {
  public static final String DATE_FORMAT_FORMAT = "YYYY-MM-DD";

  public static Specification<ResourceLibrary> containsOwnerIds(List<Long> ownerIds) {
    return (root, query, cb) -> root.get(ResourceLibrary_.ownerId).in(ownerIds);
  }

  public static Specification<ResourceLibrary> containLabel(String label) {
    return SpecificationUtils.getFilterString(ResourceLibrary_.label, label);
  }

  public static Specification<ResourceLibrary> containFileName(String fileName) {
    return SpecificationUtils.getFilterString(ResourceLibrary_.fileName, fileName);
  }

  public static Specification<ResourceLibrary> containCreatedAt(String createdAt) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.function(
                "to_char",
                String.class,
                root.get(BaseEntity_.createdAt),
                criteriaBuilder.literal(DATE_FORMAT_FORMAT)),
            String.format(SpecificationUtils.STRING_LIKE_FORMAT, "%", createdAt, "%"));
  }

  public static Specification<ResourceLibrary> containPageNumber(String pageNumber) {
    return (root, query, builder) ->
        builder.like(
            root.get(ResourceLibrary_.pageNumber).as(String.class),
            SpecificationUtils.getStringLikeFormat(pageNumber));
  }

  public static Specification<ResourceLibrary> containFileSize(String fileSize) {
    return (root, query, builder) ->
        builder.like(
            root.get(ResourceLibrary_.fileSize).as(String.class),
            SpecificationUtils.getStringLikeFormat(fileSize));
  }

  public static Specification<ResourceLibrary> containType(String type) {
    return SpecificationUtils.getFilterString(ResourceLibrary_.type, type);
  }

  public static Specification<ResourceLibrary> containByFilter(
      String filter, Specification<ResourceLibrary> specification) {
    if (StringUtils.isNotEmpty(filter)) {
      specification =
          specification.and(
              containPageNumber(filter)
                  .or(containLabel(filter))
                  .or(containType(filter))
                  .or(containFileName(filter))
                  .or(containFileSize(filter))
                  .or(containCreatedAt(filter)));
    }
    return specification;
  }

  public static Specification<ResourceLibrary> containByTypes(
      List<String> types, Specification<ResourceLibrary> specification) {
    if (CollectionUtils.isEmpty(types)) {
      return specification;
    }
    return specification.and(SpecificationUtils.in(ResourceLibrary_.type, types));
  }

  public static Specification<ResourceLibrary> containFileIds(List<String> fileIds) {
    return (root, query, cb) -> root.get(ResourceLibrary_.fileId).in(fileIds);
  }

  public static Specification<ResourceLibrary> joinTranslate(String language, List<Long> ownerIds) {
    return (root, query, criteriaBuilder) -> {
      ListJoin<ResourceLibrary, ResourceTypeTranslate> join =
          root.join(ResourceLibrary_.resourceTypeTranslate, JoinType.INNER);
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(criteriaBuilder.equal(join.get(ResourceTypeTranslate_.language), language));
      predicates.add(root.get(ResourceLibrary_.ownerId).in(ownerIds));
      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
