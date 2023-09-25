package com.tessi.cxm.pfl.ms8.service.manual;

import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowIdentificationStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowPreProcessingStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowProcessInitializationStep;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FlowProcessingManagerManual extends ExecutionManager implements InitializingBean {

  private final FlowProcessInitializationStep flowProcessInitializationStep;
  private final FlowIdentificationStep flowIdentificationStep;
  private final FlowPreProcessingStep flowPreProcessingStep;
  private final List<AbstractExecutionHandler> executionHandlers;
  private List<Integer> numberOfBean;

  @Override
  public void afterPropertiesSet() {
    executionHandlers.add(flowProcessInitializationStep);
    executionHandlers.add(flowIdentificationStep);
    executionHandlers.add(flowPreProcessingStep);
    numberOfBean.forEach(number -> this.addHandler(executionHandlers.get(number)));
  }

  public void setNumberOfBean(List<Integer> numberOfBean) {
    this.numberOfBean = numberOfBean;
  }
}
