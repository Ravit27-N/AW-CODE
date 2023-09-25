package com.tessi.cxm.pfl.ms8.core.flow.portal.campaign;

import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowIdentificationStep;
import com.tessi.cxm.pfl.ms8.core.flow.handler.CreateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.CreateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualFlowInitializer extends ExecutionManager implements InitializingBean {

  private final CreateFileControlJsonHandler createFileControlJsonHandler;
  private final CreateFlowTraceabilityHandler createFlowTraceabilityHandler;

  private final FlowIdentificationStep flowIdentificationStep;

  @Override
  public void afterPropertiesSet() throws Exception {
    // To create json file on cxm-file-control-management service
    this.addHandler(createFileControlJsonHandler);
    // To create flow in cxm-flow-traceability in microservice
    this.addHandler(createFlowTraceabilityHandler);
    // Step to process flow with cxm-identification service
    this.addHandler(flowIdentificationStep);
  }
}
