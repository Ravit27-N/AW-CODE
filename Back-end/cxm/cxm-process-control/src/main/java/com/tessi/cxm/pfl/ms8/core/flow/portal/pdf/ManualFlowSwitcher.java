package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowSwitchingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UploadPortalDocumentHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualFlowSwitcher extends ExecutionManager implements InitializingBean {

  private final GetFileControlJsonHandler fileControlJsonHandler;
  private final UploadPortalDocumentHandler uploadPortalDocumentHandler;
  private final FlowSwitchingHandler flowSwitchingHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(fileControlJsonHandler);
    this.addHandler(uploadPortalDocumentHandler);
    this.addHandler(flowSwitchingHandler);
    this.addHandler(updateFlowTraceabilityHandler);
  }
}
