package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowPreProcessingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ResourceExecutionHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualFlowAnalyzer extends ExecutionManager implements InitializingBean {
  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;
  private final ResourceExecutionHandler resourceExecutionHandler;
  private final FlowPreProcessingHandler flowPreProcessingHandler;
  private final GetFileControlJsonHandler getFileControlJsonHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandler(this.updateFileControlJsonHandler);
    this.addHandler(this.updateFlowTraceabilityHandler);
    this.addHandler(this.resourceExecutionHandler);
    this.addHandler(this.flowPreProcessingHandler);
    this.addHandler(this.getFileControlJsonHandler);
    this.addHandler(this.updateFileControlJsonHandler);
    this.addHandler(this.getFileControlJsonHandler);
  }
}
