package com.tessi.cxm.pfl.ms15.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Provide any constant value of this microservice.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 31 May 2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PreProcessingConstant {

  //Bean name
  public static final String CAMPAIGN_EXECUTION_MANAGER_BEAN = "CAMPAIGN_EXECUTION_MANAGER_BEAN";

  //key of execution context
  public static final String CAMPAIGN_PRE_PROCESSING_REQUEST = "CampaignPreProcessingRequest";
  public static final String CAMPAIGN_PRE_PROCESSING_RESPONSE = "CampaignPreProcessingResponse";
  public static final String FILE_PATH = "FILE_PATH";
  public static final String HUB_ATTACHMENT_FILE_RESPONSE = "HUB_ATTACHMENT_FILE_RESPONSE";
  public static final String COMPOSED_PDF_PATH = "composed-pdf";
}
