package com.tessi.cxm.pfl.ms15.core.chain;

import com.tessi.cxm.pfl.ms15.core.handler.AnalyseHandler;
import com.tessi.cxm.pfl.ms15.core.handler.CleanDepositPdfFileHandler;
import com.tessi.cxm.pfl.ms15.core.handler.FileManagerHandler;
import com.tessi.cxm.pfl.ms15.core.handler.GetSettingHandler;
import com.tessi.cxm.pfl.ms15.core.handler.UpdateDocumentProcessingHandler;
import com.tessi.cxm.pfl.ms15.core.handler.UpdateFilenameAfterAnalyseHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlowDepositPDFProcessingChain extends ExecutionManager implements InitializingBean {
  private final GetSettingHandler getSettingHandler;
  private final FileManagerHandler fileManagerHandler;
  private final AnalyseHandler analyseHandler;
  private final UpdateFilenameAfterAnalyseHandler updateFilenameAfterAnalyseHandler;
  private final UpdateDocumentProcessingHandler updateDocumentProcessingHandler;
  private final CleanDepositPdfFileHandler cleanDepositPdfFileHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(this.getSettingHandler);
    this.addHandler(this.fileManagerHandler);
    this.addHandler(this.analyseHandler);
    this.addHandler(this.updateFilenameAfterAnalyseHandler);
    this.addHandler(this.fileManagerHandler);
    this.addHandler(this.updateDocumentProcessingHandler);
    this.addHandler(this.fileManagerHandler);
    this.addHandler(this.cleanDepositPdfFileHandler);
  }
}
