package com.tessi.cxm.pfl.ms8.core.flow.portal.campaign;

import com.tessi.cxm.pfl.ms8.core.flow.chain.CampaignEmailEmlProcessingStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.CampaignFlowPreProcessingStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowCompositionStep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowPreCompositionStrep;
import com.tessi.cxm.pfl.ms8.core.flow.chain.FlowSwitchingStep;
import com.tessi.cxm.pfl.ms8.core.flow.handler.CampaignScheduleHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.DraftFileResourceHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.FlowCampaignBeforeLaunchHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.UpdateFlowCampaignDetailsHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Handling process of launching campaign flow automatically after call from cxm-campaign service.
 *
 * @author Vichet CHANN
 * @since 02 Jun 2022
 * @version 1.5.0
 */
@Component
@RequiredArgsConstructor
public class CampaignProcessingLauncher extends ExecutionManager implements InitializingBean {

  private final FlowCampaignBeforeLaunchHandler flowCampaignBeforeLaunchHandler;
  private final CampaignFlowPreProcessingStep preProcessingStep;
  private final FlowPreCompositionStrep preCompositionStrep;
  private final FlowCompositionStep compositionStep;
  private final DraftFileResourceHandler draftFileResourceHandler;
  private final CampaignEmailEmlProcessingStep campaignEmailEmlProcessingStep;

  private final UpdateFlowCampaignDetailsHandler updateFlowCampaignDetailsHandler;
  private final CampaignScheduleHandler campaignScheduleHandler;
  private final FlowSwitchingStep switchingStep;

  @Override
  public void afterPropertiesSet() throws Exception {
    // To prepare require data before identify flow
    this.addHandler(flowCampaignBeforeLaunchHandler);
    // Step to process flow with cxm-pre-processing service
    this.addHandler(preProcessingStep);
    // Step to process flow with cxm-pre-composition service
    this.addHandler(preCompositionStrep);
    // Step to process flow with cxm-composition service
    this.addHandler(compositionStep);
    // step to process upload text file
    this.addHandler(draftFileResourceHandler);
    // step to process upload eml file
    this.addHandler(campaignEmailEmlProcessingStep);
    // step to update flow campaign html template after cxm-composition
    this.addHandler(updateFlowCampaignDetailsHandler);
    // step to process set schedule
    this.addHandler(campaignScheduleHandler);
    // Step to process flow with cxm-switch
    this.addHandler(switchingStep);
  }
}
