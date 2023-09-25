package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowProcessingHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowSummaryPageJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ResourceExecutionHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowTraceabilityHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UploadPortalDocumentHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.WatermarkHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManualFlowProcessor extends ExecutionManager implements InitializingBean {

  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final ResourceExecutionHandler getResourceExecutionHandler;
  private final WatermarkHandler watermarkHandler;
  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final UpdateFlowTraceabilityHandler updateFlowTraceabilityHandler;
  private final FlowProcessingHandler flowProcessingHandler;
  private final UploadPortalDocumentHandler uploadPortalDocumentHandler;

  private final FlowSummaryPageJsonHandler flowSummaryPageJsonHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(this.getFileControlJsonHandler);
    this.addHandler(this.getResourceExecutionHandler);
    this.addHandler(this.watermarkHandler);
    this.addHandler(this.flowSummaryPageJsonHandler);
    this.addHandler(this.updateFileControlJsonHandler);
    this.addHandler(this.getFileControlJsonHandler);
    this.addHandler(this.updateFlowTraceabilityHandler);
    this.addHandler(this.flowProcessingHandler);
    this.addHandler(this.updateFileControlJsonHandler);
    this.addHandler(this.uploadPortalDocumentHandler);
  }
}
