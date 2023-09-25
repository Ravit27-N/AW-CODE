package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowValidationProcessingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualProcessFlowValidation extends ExecutionManager implements InitializingBean {
  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final FlowValidationProcessingHandler flowValidationProcessingHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(this.getFileControlJsonHandler);
    this.addHandler(this.flowValidationProcessingHandler);
    this.addHandler(this.updateFlowTraceabilityHandler);
  }
}
