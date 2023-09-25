package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.repository.WatermarkRepository;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteWatermarkHandler extends AbstractExecutionHandler {
  private final WatermarkRepository watermarkRepository;
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  protected ExecutionState execute(ExecutionContext context) {
    try {
      var uuid = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
      watermarkRepository.findByFlowId(uuid).ifPresent(watermarkRepository::delete);
    } catch (Exception e) {
      log.error("Fail to delete watermark :{}", e.getMessage());
      log.error("{0}", e);
    }

    return ExecutionState.NEXT;
  }
}
