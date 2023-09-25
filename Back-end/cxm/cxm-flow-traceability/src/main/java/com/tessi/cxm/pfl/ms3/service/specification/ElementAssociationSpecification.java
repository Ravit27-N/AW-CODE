package com.tessi.cxm.pfl.ms3.service.specification;

import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.ElementAssociation_;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * The specification for filtering and matching Element Association by {@link ElementAssociation}
 * properties.
 *
 * @since 11/02/21
 * @author Piseth Khon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementAssociationSpecification {

  /**
   * Handle the specification for match {@link ElementAssociation} by {@link FlowDocument} id.
   *
   * @param id refer to {@link FlowDocument} identity.
   * @return {@link Specification} of {@link ElementAssociation}.
   */
  public static Specification<ElementAssociation> containFlowDocumentId(long id) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(ElementAssociation_.flowDocument).get(FlowDocument_.id), id);
  }
}
