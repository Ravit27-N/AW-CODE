package com.tessi.cxm.pfl.ms8.core.flow.portal.campaign;

import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowSwitchingStep;
import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowCampaignBeforeLaunchHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignSchedulerLauncher extends ExecutionManager implements InitializingBean {
  private final FlowCampaignBeforeLaunchHandler flowCampaignBeforeLaunchHandler;

  private final FlowSwitchingStep switchingStep;

  @Override
  public void afterPropertiesSet() throws Exception {
    // To prepare require data before identify flow
    this.addHandler(flowCampaignBeforeLaunchHandler);
    // Step to process flow with cxm-switch
    this.addHandler(switchingStep);
  }
}
