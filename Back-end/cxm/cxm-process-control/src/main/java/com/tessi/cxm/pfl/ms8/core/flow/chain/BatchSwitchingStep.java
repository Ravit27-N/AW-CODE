package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowSwitchingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.shared.core.chains.BaseExecutionHandlerChains;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchSwitchingStep extends BaseExecutionHandlerChains implements InitializingBean {

  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final FlowSwitchingHandler flowSwitchingHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addExecutionHandler(this.getFileControlJsonHandler);
    this.addExecutionHandler(this.flowSwitchingHandler);
  }

}
