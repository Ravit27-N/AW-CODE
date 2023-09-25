package com.tessi.cxm.pfl.ms8.core.flow.portal.campaign;

import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowIdentificationStep;
import com.tessi.cxm.pfl.ms8.core.flow.handler.CreateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFileControlJsonHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Handling process of modifying campaign SMS flow after change csv file.
 *
 * @author Vichet CHANN
 * @version 1.4.0
 * @since 26 May 2022
 */
@Component
@RequiredArgsConstructor
public class ManualFlowModifier extends ExecutionManager implements InitializingBean {

  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final CreateFlowTraceabilityHandler createFlowTraceabilityHandler;
  private final FlowIdentificationStep flowIdentificationStep;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(updateFileControlJsonHandler);
    this.addHandler(createFlowTraceabilityHandler);
    this.addHandler(flowIdentificationStep);
  }
}
