package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowPreCompositionHandler;
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
public class FlowPreCompositionStrep extends BaseExecutionHandlerChains
    implements InitializingBean {
  private final FlowPreCompositionHandler flowPreCompositionHandler;
  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() {
    this.addExecutionHandler(this.flowPreCompositionHandler);
    this.addExecutionHandler(this.updateFileControlJsonHandler);
    this.addExecutionHandler(this.updateFlowTraceabilityHandler);
  }
}
