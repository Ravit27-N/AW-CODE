package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowSwitchingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.BaseExecutionHandlerChains;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Handling process of cxm-switch service calling and processing of each step with specific handler.
 *
 * @author Vichet CHANN
 * @since 07 Jun 2022
 * @version 1.5.0
 */
@Component
@RequiredArgsConstructor
public class FlowSwitchingStep extends BaseExecutionHandlerChains implements InitializingBean {

  private final FlowSwitchingHandler flowSwitchingHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addExecutionHandler(flowSwitchingHandler);
    this.addExecutionHandler(updateFlowTraceabilityHandler);
  }
}
