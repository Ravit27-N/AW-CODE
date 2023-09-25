package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlPreProcessing;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.service.restclient.ProcessingFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.Document;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class FlowPreProcessingHandler extends AbstractExecutionHandler {
  private static final String START_PROCESSING_LOG_MESSAGE =
      "Preprocessing the flow \"{}\" for documents and metadata.";
  private static final String END_PROCESSING_LOG_MESSAGE =
      "Documents and metadata of flow \"{}\" have been split and extracted successfully.";
  private final ObjectMapper objectMapper;
  private final ProcessingFeignClient processingFeignClient;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    log.info("--- Start PreProcessing Flow ---");
    context.put(
        FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
        ProcessControlStep.PRE_PROCESSING);

    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      return this.preprocessBatchDeposit(context);
    } else {
      return this.preprocessPortalDeposit(context);
    }
  }

  private ExecutionState preprocessBatchDeposit(ExecutionContext context) {
    log.info("--- Start preprocessBatchDeposit ---");
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    log.info(START_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
      var response =
              this.processingFeignClient.getDocument(
                      context.get(FlowTreatmentConstants.MODEL_NAME, String.class),
                      depositedFlowLaunchRequest.getFlowType(),
                      depositedFlowLaunchRequest.getFileId(),
                      depositedFlowLaunchRequest.getIdCreator(),
                      context.get(FlowTreatmentConstants.CHANNEL, String.class),
                      context.get(FlowTreatmentConstants.SUB_CHANNEL, String.class),
                      funcKey,
                      privKey,
                      ProcessControlExecutionContextUtils.getBearerToken(context));
      log.info("response = '" + response + "'");
      if (Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
        var preProcessedDocumentDetails =
                this.objectMapper.convertValue(response.getData(), ProcessCtrlPreProcessing.class);

        context.put(FlowTreatmentConstants.DOCUMENT, preProcessedDocumentDetails);

        log.info(END_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());
        log.info("--- End PreProcessing Flow ---");
        return ExecutionState.NEXT;
      }
    log.info("--- End PreProcessing Flow ---");
    return ExecutionState.END;
  }

  private ExecutionState preprocessPortalDeposit(ExecutionContext context) {
    log.info("--- Start preprocessPortalDeposit ---");
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    log.info(START_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    final Attachments attachments = context.get(ProcessControlConstants.ATTACHMENT_DTO, Attachments.class);
    var response =
        this.processingFeignClient.getPortalDocument(new PreProcessingRequest(attachments),
            context.get(FlowTreatmentConstants.MODEL_NAME, String.class),
            depositedFlowLaunchRequest.getFlowType(),
            depositedFlowLaunchRequest.getFileId(),
            depositedFlowLaunchRequest.getIdCreator(),
            funcKey,
            privKey,
            ProcessControlExecutionContextUtils.getBearerToken(context));
    log.info("response = '" + response + "'");
    if (Message.FINISHED.equalsIgnoreCase(response.getMessage())) {
      var preProcessedDocumentDetails =
          this.objectMapper.convertValue(response.getData(), PortalDocumentResponse.class);

      context.put(FlowTreatmentConstants.PORTAL_DOCUMENT, preProcessedDocumentDetails);

      log.info(END_PROCESSING_LOG_MESSAGE, depositedFlowLaunchRequest.getUuid());
      context.put(
          FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP,
          ProcessControlStep.PRE_PROCESSING);
      context.put(
          FlowTreatmentConstants.DOCUMENT_VALIDATION,
          validateDocumentsKO(preProcessedDocumentDetails.getDocument()));
      return ExecutionState.NEXT;
    }
    return ExecutionState.END;
  }

  private boolean validateDocumentsKO(Document documentResponse) {
    return documentResponse.getFlowDocuments().stream()
        .noneMatch(doc -> doc.getAnalyse().equalsIgnoreCase(HttpStatus.OK.getReasonPhrase()));
  }
}
