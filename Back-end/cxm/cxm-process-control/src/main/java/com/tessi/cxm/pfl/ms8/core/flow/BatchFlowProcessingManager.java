package com.tessi.cxm.pfl.ms8.core.flow;

import com.tessi.cxm.pfl.ms8.core.flow.chain.BatchFlowProcessInitializationStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.BatchSwitchingStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowCompositionStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowIdentificationStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowPreCompositionStrep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowPreProcessingStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowProcessInitializationStep;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BatchFlowProcessingManager extends ExecutionManager implements InitializingBean {
  private final BatchFlowProcessInitializationStep flowProcessInitializationStep;
  private final FlowIdentificationStep flowIdentificationStep;
  private final FlowPreProcessingStep flowPreProcessingStep;
  private final FlowPreCompositionStrep flowPreCompositionStrep;
  private final FlowCompositionStep flowCompositionStep;
  private final BatchSwitchingStep batchSwitchingStep;
  @Override
  public void afterPropertiesSet() {
    this.addHandler(this.flowProcessInitializationStep);
    this.addHandler(this.flowIdentificationStep);
    this.addHandler(this.flowPreProcessingStep);
    this.addHandler(this.flowPreCompositionStrep);
    this.addHandler(this.flowCompositionStep);
    this.addHandler(this.batchSwitchingStep);
  }
}
