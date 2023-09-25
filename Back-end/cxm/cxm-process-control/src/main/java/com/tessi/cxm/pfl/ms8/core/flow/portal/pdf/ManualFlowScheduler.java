package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.CleanResourceFileHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.DeleteWatermarkHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowRequestValidationHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ManualFlowRequestScheduleHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.SetUnloadingScheduleJobHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UploadPortalDocumentHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ManualFlowScheduler extends ExecutionManager implements InitializingBean {

  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final SetUnloadingScheduleJobHandler setUnloadingScheduleJobHandler;
  private final FlowRequestValidationHandler flowRequestValidationHandler;
  private final ManualFlowRequestScheduleHandler manualFlowRequestScheduleHandler;
  private final UploadPortalDocumentHandler uploadPortalDocumentHandler;

  private final CleanResourceFileHandler cleanResourceFileHandler;
  private final DeleteWatermarkHandler deleteWatermarkHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(getFileControlJsonHandler);
    this.addHandler(setUnloadingScheduleJobHandler);
    this.addHandler(flowRequestValidationHandler);
    this.addHandler(manualFlowRequestScheduleHandler);
    this.addHandler(uploadPortalDocumentHandler);
    this.addHandler(cleanResourceFileHandler);
    this.addHandler(deleteWatermarkHandler);
  }
}
