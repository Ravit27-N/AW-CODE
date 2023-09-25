package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.CleanResourceFileHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.DeleteWatermarkHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.GetFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ManualFlowRequestToValidateHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UploadPortalDocumentHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ManualFlowValidation extends ExecutionManager implements InitializingBean {

  private final GetFileControlJsonHandler getFileControlJsonHandler;
  private final ManualFlowRequestToValidateHandler manualFlowRequestToValidateHandler;
  private final UploadPortalDocumentHandler uploadPortalDocumentHandler;

  private final CleanResourceFileHandler cleanResourceFileHandler;
  private final DeleteWatermarkHandler deleteWatermarkHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(getFileControlJsonHandler);
    this.addHandler(manualFlowRequestToValidateHandler);
    this.addHandler(uploadPortalDocumentHandler);
    this.addHandler(cleanResourceFileHandler);
    this.addHandler(deleteWatermarkHandler);
  }
}
