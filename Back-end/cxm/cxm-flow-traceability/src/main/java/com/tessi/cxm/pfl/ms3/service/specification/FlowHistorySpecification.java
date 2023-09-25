package com.tessi.cxm.pfl.ms3.service.specification;

import com.tessi.cxm.pfl.ms3.entity.BaseEntity_;
import com.tessi.cxm.pfl.ms3.entity.BaseHistoryEvent_;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;


/**
 * The specification for filtering and matching FlowHistory by {@link
 * FlowHistory} properties.
 *
 * @since 10/15/21
 * @author Piseth Khon
 */
public class FlowHistorySpecification {

  private FlowHistorySpecification() {
    //do nothing.
  }

  public static Specification<FlowHistory> containEvent(String event) {
    return SpecificationUtils.getFilterString(BaseHistoryEvent_.event, event);
  }

  public static Specification<FlowHistory> containService(String service) {
    return SpecificationUtils.getFilterString(BaseHistoryEvent_.server, service);
  }

  public static Specification<FlowHistory> containCreatedBy(String createdBy) {
    return SpecificationUtils.getFilterString(BaseEntity_.createdBy, createdBy);
  }

  public static Specification<FlowHistory> containDateTime(String dataTime) {
    return SpecificationUtils.getFilterDate(BaseEntity_.createdAt, dataTime);
  }

  public static Specification<FlowHistory> containByFlowId(long id) {
    return (root, query, criteriaBuilder) -> {
      query.orderBy(criteriaBuilder.asc(root.get(BaseHistoryEvent_.dateTime)));
      return criteriaBuilder.equal(
          root.get(FlowHistory_.flowTraceability).get(FlowTraceability_.id), id);
    };

  }

  /**
   * logic of {@link FlowHistory}.
   *
   * @param filter refer to any string filter for query data base on table column condition
   */
  public static Specification<FlowHistory> getFlowHistorySpecificationFilter(String filter) {
    return Specification.where(
        FlowHistorySpecification.containEvent(filter)
            .or(FlowHistorySpecification.containCreatedBy(filter))
            .or(FlowHistorySpecification.containDateTime(filter))
            .or(FlowHistorySpecification.containService(filter)));
  }
}
