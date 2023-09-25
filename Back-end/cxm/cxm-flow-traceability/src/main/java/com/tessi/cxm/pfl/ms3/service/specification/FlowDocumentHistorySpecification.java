package com.tessi.cxm.pfl.ms3.service.specification;

import com.tessi.cxm.pfl.ms3.entity.BaseEntity_;
import com.tessi.cxm.pfl.ms3.entity.BaseHistoryEvent_;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory_;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * The specification for filtering and matching FlowDocumentHistory by {@link FlowDocumentHistory}
 * properties.
 *
 * @author Sokhour LACH
 */
public class FlowDocumentHistorySpecification {

  private FlowDocumentHistorySpecification() {
    // do nothing.
  }

  public static Specification<FlowDocumentHistory> containEvent(String event) {
    return SpecificationUtils.getFilterString(BaseHistoryEvent_.event, event);
  }

  public static Specification<FlowDocumentHistory> containService(String service) {
    return SpecificationUtils.getFilterString(BaseHistoryEvent_.server, service);
  }

  public static Specification<FlowDocumentHistory> containCreatedBy(String createdBy) {
    return SpecificationUtils.getFilterString(BaseEntity_.createdBy, createdBy);
  }

  public static Specification<FlowDocumentHistory> containDateTime(String dataTime) {
    return SpecificationUtils.getFilterDate(BaseEntity_.createdAt, dataTime);
  }

  public static Specification<FlowDocumentHistory> containByFlowDocumentId(long id) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get(FlowDocumentHistory_.flowDocument).get(FlowDocument_.id), id);
  }

  /**
   * get flow document history by filter.
   *
   * @param filter refer to any string filter for query data that base on table column condition
   * @return object of {@link FlowDocumentHistory}
   */
  public static Specification<FlowDocumentHistory> getFlowDocumentHistorySpecificationFilter(
      String filter) {
    return Specification.where(
        FlowDocumentHistorySpecification.containEvent(filter)
            .or(FlowDocumentHistorySpecification.containCreatedBy(filter))
            .or(FlowDocumentHistorySpecification.containDateTime(filter))
            .or(FlowDocumentHistorySpecification.containService(filter)));
  }

  /**
   * Handle the specification for select flow document history by list of created by.
   *
   * @param users refer to list of created by.
   */
  public static Specification<FlowDocumentHistory> contains(List<String> users) {
    return SpecificationUtils.in(BaseEntity_.createdBy, users);
  }
}
