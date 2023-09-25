package com.tessi.cxm.pfl.ms15.core.chain;

import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.core.handler.CampaignAttachmentHandler;
import com.tessi.cxm.pfl.ms15.core.handler.LoadFileHandler;
import com.tessi.cxm.pfl.ms15.core.handler.ReadCampaignCsvFileHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Handling step of reading file CSV of campaign.
 *
 * @author Vichet CHANN
 * @since 01 Jun 2022
 * @version 1.5.0
 */
@Component(PreProcessingConstant.CAMPAIGN_EXECUTION_MANAGER_BEAN)
@RequiredArgsConstructor
public class CampaignPreProcessingChain extends ExecutionManager implements InitializingBean {

  private final LoadFileHandler loadFileHandler;
  private final CampaignAttachmentHandler campaignAttachmentHandler;
  private final ReadCampaignCsvFileHandler readCampaignCsvFileHandler;

  @Override
  public void afterPropertiesSet() {
    // To load file from cxm-file-manager
    this.addHandler(loadFileHandler);
    // To add attachments.
    this.addHandler(campaignAttachmentHandler);
    // To read data from CSV file and map data to specific class
    this.addHandler(readCampaignCsvFileHandler);
  }
}
