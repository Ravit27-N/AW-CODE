package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.constant.WatermarkPosition;
import com.tessi.cxm.pfl.ms8.service.WatermarkService;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.FiligraneDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WatermarkHandler extends AbstractExecutionHandler {
  private final WatermarkService watermarkService;
  private final ModelMapper modelMapper;
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var flowId =
        context
            .get(
                FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
                DepositedFlowLaunchRequest.class)
            .getUuid();
    this.watermarkService
        .findWatermarkByFlowId(flowId)
        .map(watermark -> this.modelMapper.map(watermark, FiligraneDto.class))
        .ifPresent(
            filigraneDto -> {
              String positionValue = WatermarkPosition.getValueByKey(filigraneDto.getPosition());
              if (positionValue != null) {
                filigraneDto.setPosition(positionValue);
                context.put(ProcessControlConstants.FILIGRANE_DTO, filigraneDto);
              }
            });
    return ExecutionState.NEXT;
  }
}
