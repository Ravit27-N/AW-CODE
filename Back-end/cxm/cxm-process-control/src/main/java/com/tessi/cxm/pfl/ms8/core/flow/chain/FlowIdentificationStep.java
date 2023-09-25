package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowIdentificationHandler;
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
public class FlowIdentificationStep extends BaseExecutionHandlerChains implements InitializingBean {
  private final FlowIdentificationHandler flowIdentificationHandler;
  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() {
    this.addExecutionHandler(this.flowIdentificationHandler);
    this.addExecutionHandler(this.updateFileControlJsonHandler);
    this.addExecutionHandler(this.updateFlowTraceabilityHandler);
  }
}
