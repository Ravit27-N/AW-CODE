package com.tessi.cxm.pfl.ms8.core.flow.portal.pdf;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowIdentificationHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.SaveAutomaticAttachmentHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.SaveAutomaticBackgroundHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.SaveAutomaticSignatureHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFileControlJsonHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowDepositHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ManualReIdentifierFlow extends ExecutionManager implements InitializingBean {

  private final FlowIdentificationHandler flowIdentificationHandler;
  private final SaveAutomaticBackgroundHandler saveAutomaticBackgroundHandler;
  private final SaveAutomaticAttachmentHandler saveAutomaticAttachmentHandler;
  private final SaveAutomaticSignatureHandler saveAutomaticSignatureHandler;
  private final UpdateFileControlJsonHandler updateFileControlJsonHandler;
  private final UpdateFlowDepositHandler updateFlowDepositHandler;

  @Override
  public void afterPropertiesSet() {
    this.addHandler(this.flowIdentificationHandler);
    this.addHandler(this.saveAutomaticBackgroundHandler);
    this.addHandler(this.saveAutomaticAttachmentHandler);
    this.addHandler(this.saveAutomaticSignatureHandler);
    this.addHandler(this.updateFileControlJsonHandler);
    this.addHandler(this.updateFlowDepositHandler);
  }
}
