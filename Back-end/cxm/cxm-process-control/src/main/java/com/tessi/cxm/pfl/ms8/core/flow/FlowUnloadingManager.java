package com.tessi.cxm.pfl.ms8.core.flow;

import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowUnloadingValidationHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.LoadAndDestroyJobInfoHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.ProcessScheduleJobHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlowUnloadingManager extends ExecutionManager implements InitializingBean {

  private final FlowUnloadingValidationHandler flowUnloadingValidationHandler;
  private final LoadAndDestroyJobInfoHandler loadAndDestroyJobInfoHandler;
  private final ProcessScheduleJobHandler processScheduleJobHandler;
  /**
   * Invoked by the containing {@code BeanFactory} after it has set all bean properties and
   * satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
   *
   * <p>This method allows the bean instance to perform validation of its overall configuration and
   * final initialization when all bean properties have been set.
   *
   * @throws Exception in the event of misconfiguration (such as failure to set an essential
   *     property) or if initialization fails for any other reason
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    this.addHandler(this.flowUnloadingValidationHandler);
    this.addHandler(loadAndDestroyJobInfoHandler);
    this.addHandler(processScheduleJobHandler);
  }
}
