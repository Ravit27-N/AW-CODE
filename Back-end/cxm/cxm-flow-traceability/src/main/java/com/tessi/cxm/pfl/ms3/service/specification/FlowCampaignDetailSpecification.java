package com.tessi.cxm.pfl.ms3.service.specification;

import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail;
import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import java.util.List;

public class FlowCampaignDetailSpecification {
  private FlowCampaignDetailSpecification() {}
  public static Specification<FlowCampaignDetail> containsFlowIds(List<Long> flowIds) {
    return (root, query, cb) -> {
      final Path<FlowCampaignDetail> flowCampaignDetail = root.join(
          FlowCampaignDetail_.FLOW_TRACEABILITY);
      return cb.in(flowCampaignDetail.get(FlowTraceability_.ID)).value(flowIds);
    };
  }
}
