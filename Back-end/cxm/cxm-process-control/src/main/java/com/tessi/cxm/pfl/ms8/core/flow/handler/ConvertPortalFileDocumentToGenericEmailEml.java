package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignAttachment;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConvertPortalFileDocumentToGenericEmailEml extends AbstractExecutionHandler {
  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var portalJson =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (portalJson.getFlow().getType().contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_EMAIL)) {

      final CampaignDepositFlowLaunchRequest camRequest =
          context.get(
              FlowTreatmentConstants.CAMPAIGN_DEPOSIT_FLOW_LAUNCH_REQUEST,
              CampaignDepositFlowLaunchRequest.class);
      final Map<String, String> attachments =
          ObjectUtils.defaultIfNull(
              context.get(FlowTreatmentConstants.ATTACHMENTS, Map.class), new HashMap<>());
      final List<EmailToEmlFileConvertor.GenericEmailEmlBody> genericEmailEmlBodies =
          portalJson.getFlow().getFlowDocuments().stream()
              .map(
                  doc ->
                      new EmailToEmlFileConvertor.GenericEmailEmlBody(
                          doc.getDocUUID(),
                          camRequest.getSenderMail(),
                          doc.getRecipientID().split(","),
                          doc.getEmailObject(),
                          "", // this should be available in the EmailBase64FileHandler
                          camRequest.getSenderName(),
                          new Date(),
                          doc.getProcessing().getDocName(),
                          attachments,
                          new ArrayList<>()))
              .collect(Collectors.toList());
      context.put(EmailToEmlFileConvertor.GENERIC_EMAIL_BODY, genericEmailEmlBodies);
    }
    return ExecutionState.NEXT;
  }
}
