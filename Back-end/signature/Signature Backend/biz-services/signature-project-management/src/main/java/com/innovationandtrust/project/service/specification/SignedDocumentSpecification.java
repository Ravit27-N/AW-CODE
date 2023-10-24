package com.innovationandtrust.project.service.specification;

import com.innovationandtrust.project.model.entity.Signatory_;
import com.innovationandtrust.project.model.entity.SignedDocument;
import com.innovationandtrust.project.model.entity.SignedDocument_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/** SignedDocumentSpecification use for query, or filter from signed_document table. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignedDocumentSpecification {
  public static Specification<SignedDocument> findAllBySignatoryId(Long signatoryId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(SignedDocument_.SIGNATORY).get(Signatory_.ID), signatoryId);
  }
}
