package com.tessi.cxm.pfl.ms8.service.manual;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;

public interface ProcessControlServiceManual {

  ProcessCtrlIdentificationResponse identityLauncher(
      DepositedFlowLaunchRequest launchRequest, String bearerToken);

  ProcessCtrlIdentificationResponse preProcessingLauncher(
      DepositedFlowLaunchRequest launchRequest, String bearerToken);

  void updateFlowTraceabilityStatus(ExecutionContext context);
}
