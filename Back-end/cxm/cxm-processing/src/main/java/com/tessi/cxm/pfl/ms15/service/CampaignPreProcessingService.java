package com.tessi.cxm.pfl.ms15.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.CampaignPreProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import java.util.concurrent.Flow;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Handling process of extracting data from csv file of campaign process.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 31 May 2022
 */
@Service
public class CampaignPreProcessingService {

  private final ExecutionManager executionManager;

  public CampaignPreProcessingService(
      @Qualifier(PreProcessingConstant.CAMPAIGN_EXECUTION_MANAGER_BEAN)
          ExecutionManager executionManager) {
    this.executionManager = executionManager;
  }

  /**
   * Handling process of reading data from csv and map into object of {@link PortalFileFlowDocument}
   *
   * @param request object of {@link CampaignPreProcessingRequest}
   * @return the object of {@link PortalFileFlowDocument}
   */
  @SuppressWarnings("unchecked")
  public FlowProcessingResponse<CampaignPreProcessingResponse> getDocuments(
      CampaignPreProcessingRequest request, String token, String funcKey, String privKey) {
    var context = new ExecutionContext();
    context.put(PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_REQUEST, request);
    context.put(FlowTreatmentConstants.BEARER_TOKEN, token);
    context.put(FlowTreatmentConstants.ID_CREATOR, request.getIdCreator());
    context.put(FlowTreatmentConstants.FUNC_KEY, funcKey);
    context.put(FlowTreatmentConstants.PRIV_KEY, privKey);
    this.executionManager.execute(context);
    final CampaignPreProcessingResponse campaignPreProcessingResponse =
        context.get(
            PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_RESPONSE,
            CampaignPreProcessingResponse.class);
    Map<String, String> attachments =
        ObjectUtils.defaultIfNull(
            context.get(FlowTreatmentConstants.ATTACHMENTS, Map.class),
            new HashMap<String, String>());
    campaignPreProcessingResponse.setAttachments(attachments);
    return new FlowProcessingResponse<>(
        Message.FINISHED, HttpStatus.OK, campaignPreProcessingResponse);
  }
}
