package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.CreateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.CreateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.BaseExecutionHandlerChains;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class FlowProcessInitializationStep extends BaseExecutionHandlerChains
    implements InitializingBean {
  private final CreateFileControlJsonHandler createFileControlJsonHandler;
  private final CreateFlowTraceabilityHandler createFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() {
    this.addExecutionHandler(this.createFileControlJsonHandler);
    this.addExecutionHandler(this.createFlowTraceabilityHandler);
  }
}
