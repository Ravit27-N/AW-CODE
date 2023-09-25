package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.util.FlowTreatmentUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import org.springframework.util.Assert;

public abstract class FlowTraceabilityHandler extends AbstractExecutionHandler {

  protected String buildFlowName(ExecutionContext context) {
    Assert.notNull(
        context.get(FlowTreatmentConstants.FLOW_NAME), "FLOW_UUID is required and cannot be null!");
    Assert.notNull(
        context.get(FlowTreatmentConstants.FLOW_TYPE), "FLOW_TYPE is required and cannot be null!");

    return this.buildFlowName(
        context.get(FlowTreatmentConstants.FLOW_NAME, String.class),
        context.get(FlowTreatmentConstants.FLOW_TYPE, String.class));
  }

  protected String buildFlowName(String flowName, String flowType) {
    return flowName.concat(".").concat(FlowTreatmentUtils.getFlowTypeExtension(flowType));
  }
}
