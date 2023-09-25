package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowDocumentValidationProcessingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualProcessFlowDocumentValidation extends ExecutionManager implements
    InitializingBean {
  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final FlowDocumentValidationProcessingHandler flowDocumentValidationProcessingHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(this.getFileControlJsonHandler);
    this.addHandler(this.flowDocumentValidationProcessingHandler);
    this.addHandler(this.updateFlowTraceabilityHandler);
  }

}
