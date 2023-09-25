package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.service.ResourceFileService;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CleanResourceFileHandler extends AbstractExecutionHandler {

  private final ResourceFileService resourceFileService;

  public CleanResourceFileHandler(ResourceFileService resourceFileService) {
    this.resourceFileService = resourceFileService;
  }

  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    try {
      var uuid = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
      resourceFileService.deleteResourceFileByFlowId(uuid);
    } catch (Exception e) {
      log.error("Fail to delete resource file :{}", e.getMessage());
      log.error("{0}", e);
    }

    return ExecutionState.NEXT;
  }
}
