package com.tessi.cxm.pfl.ms8.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.core.flow.FlowUnloadingManager;
import com.tessi.cxm.pfl.ms8.dto.FlowUnloadingPayload;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
@AllArgsConstructor
public class FlowUnloadingService {

  private final FlowUnloadingManager flowUnloadingManager;

  public void unloadFlow(FlowUnloadingPayload payload, Date dateFireTime, boolean forceUnloading)
      throws RuntimeException {
    var context = this.getFlowUnloadingContext(payload);
    context.put(FlowTreatmentConstants.FORCE_UNLOADING_DATE, dateFireTime);
    context.put(ProcessControlConstants.ALLOW_FORCE_UNLOADING, forceUnloading);
    try {
      this.flowUnloadingManager.execute(context);
    } catch (Exception exception) {
      log.error("Failed to execute", exception);
    }
  }

  public void unloadFlow(FlowUnloadingPayload payload, Date dateFireTime) throws RuntimeException {
    unloadFlow(payload, dateFireTime, false);
  }

  public void forceUnloadFlow(long clientId, Date dateFireTime) throws RuntimeException {
    unloadFlow(
        new FlowUnloadingPayload(clientId, new SharedClientUnloadDTO(), new ArrayList<>()),
        dateFireTime,
        true);
  }
  private ExecutionContext getFlowUnloadingContext(FlowUnloadingPayload unloadPayload) {
    ExecutionContext context = new ExecutionContext();
    context.put(FlowTreatmentConstants.CLIENT_ID, unloadPayload.getClientId());
    context.put(ProcessControlConstants.FLOW_UNLOADING_PAYLOAD, unloadPayload);
    return context;
  }
}
