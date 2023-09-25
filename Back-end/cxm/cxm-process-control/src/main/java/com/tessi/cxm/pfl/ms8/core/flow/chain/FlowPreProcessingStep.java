package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowPreProcessingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.BaseExecutionHandlerChains;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class FlowPreProcessingStep extends BaseExecutionHandlerChains implements InitializingBean {
  private final FlowPreProcessingHandler flowPreProcessingHandler;
  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() {
    this.addExecutionHandler(this.flowPreProcessingHandler);
    this.addExecutionHandler(this.updateFileControlJsonHandler);
    this.addExecutionHandler(this.updateFlowTraceabilityHandler);
  }
}
