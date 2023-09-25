package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowDocumentRequestValidationHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ManualFlowDocumentRequestSchedulerHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.SetUnloadingScheduleJobHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UploadPortalDocumentHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ManualFlowDocumentScheduler extends ExecutionManager implements InitializingBean {

  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final SetUnloadingScheduleJobHandler setUnloadingScheduleJobHandler;
  private final FlowDocumentRequestValidationHandler flowDocumentRequestValidationHandler;
  private final ManualFlowDocumentRequestSchedulerHandler manualFlowRequestScheduleHandler;
  private final UploadPortalDocumentHandler uploadPortalDocumentHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(getFileControlJsonHandler);
    this.addHandler(setUnloadingScheduleJobHandler);
    this.addHandler(flowDocumentRequestValidationHandler);
    this.addHandler(manualFlowRequestScheduleHandler);
    this.addHandler(uploadPortalDocumentHandler);
  }
}
