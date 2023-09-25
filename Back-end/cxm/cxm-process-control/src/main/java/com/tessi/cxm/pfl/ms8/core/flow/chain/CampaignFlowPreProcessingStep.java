package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.CampaignPreProcessingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.BaseExecutionHandlerChains;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignFlowPreProcessingStep extends BaseExecutionHandlerChains
    implements InitializingBean {

  private final CampaignPreProcessingHandler preProcessingHandler;
  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() throws Exception {

    this.addExecutionHandler(preProcessingHandler);
    this.addExecutionHandler(updateFileControlJsonHandler);
    this.addExecutionHandler(updateFlowTraceabilityHandler);
  }
}
