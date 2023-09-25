package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ProcessingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.model.FlowTreatmentFlowRequestWrapper.Production;
import com.tessi.cxm.pfl.shared.model.ResponseDocumentProcessingPortal;
import com.tessi.cxm.pfl.ms8.service.restclient.ProcessingFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.FiligraneDto;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class FlowProcessingHandler extends AbstractExecutionHandler {
  private static final String START_PROCESSING_LOG_MESSAGE =
      "Preprocessing the flow \"{}\" for documents and metadata.";
  private static final String END_PROCESSING_LOG_MESSAGE =
      "Documents and metadata of flow \"{}\" have been split and extracted successfully.";
  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final ProcessingFeignClient processingFeignClient;

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final Production production =
        context.get(FlowTreatmentConstants.FLOW_PRODUCTION, Production.class);
    var depositedFlowLaunchRequest =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    log.info(START_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());
    var flowDeposited =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    var portalFlowFileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    var composedFileId = context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class);
    var validation = context.get(FlowTreatmentConstants.IS_TO_VALIDATED, Boolean.class);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    BackgroundPage backgroundPage =
        context.get(ProcessControlConstants.BACKGROUND_DTO, BackgroundPage.class);
    Attachments attachments =
        context.get(ProcessControlConstants.ATTACHMENT_DTO, Attachments.class);
    FiligraneDto filigrane = context.get(ProcessControlConstants.FILIGRANE_DTO, FiligraneDto.class);
    String signature = context.get(ProcessControlConstants.SIGNATURE_RESOURCE, String.class);
    log.info(START_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());
    ProcessingRequest processingRequest =
        new ProcessingRequest(
            portalFlowFileControl, backgroundPage, attachments, filigrane, signature);
    var response =
        this.processingFeignClient.processing(
            flowDeposited.getIdCreator(),
            composedFileId,
            processingRequest,
            funcKey,
            privKey,
            ProcessControlExecutionContextUtils.getBearerToken(context));

    if (Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
      var processingDocuments =
          this.objectMapper.convertValue(
              response.getData(), ResponseDocumentProcessingPortal.class);

      context.put(FlowTreatmentConstants.PORTAL_DOCUMENT, processingDocuments);
      context.put(FlowTreatmentConstants.COMPOSED_FILE_ID, processingDocuments.getComposedFileId());
      context.put(FlowTreatmentConstants.FLOW_UUID, depositedFlowLaunchRequest.getUuid());

      log.info(END_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());

      updatePortalProductionFlow(
          production,
          portalFlowFileControl,
          processingDocuments.getDocumentProcessing(),
          validation);

      context.put(
          FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL_PROCESSING, portalFlowFileControl);
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
          ProcessControlStep.COMPOSITION);
      return ExecutionState.NEXT;
    }
    throw new ExecutionException("Fail to analyse document from go2pdf server.");
  }

  private void updatePortalProductionFlow(
      Production production,
      PortalFlowFileControl portalFlowFileControl,
      List<PortalFileDocumentProcessing> documentProcessing,
      boolean validation) {
    IntStream.range(0, documentProcessing.size())
        .forEach(
            index -> {
              updateProcessing(
                  portalFlowFileControl.getFlow().getFlowDocuments().get(index).getProcessing(),
                  documentProcessing.get(index));
              updateProduction(
                  portalFlowFileControl.getFlow().getFlowDocuments().get(index).getProduction(),
                  production,
                  validation);
            });
  }

  void updateProduction(
      PortalFileDocumentProduction portalFileDocumentProduction,
      Production production,
      boolean validation) {
    portalFileDocumentProduction.setArchiving(production.getArchiving());
    portalFileDocumentProduction.setColor(production.getColor());
    portalFileDocumentProduction.setRecto(production.getRecto());
    portalFileDocumentProduction.setUrgency(production.getUrgency());
    portalFileDocumentProduction.setValidation(String.valueOf(validation));
  }

  void updateProcessing(
      PortalFileDocumentProcessing portalFileDocumentProcessing,
      PortalFileDocumentProcessing documentProcessing) {
    this.modelMapper.map(documentProcessing, portalFileDocumentProcessing);
  }
}
