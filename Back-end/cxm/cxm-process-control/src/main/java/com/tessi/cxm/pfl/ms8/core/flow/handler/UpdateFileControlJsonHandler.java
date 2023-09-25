package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlIdentificationStep;
import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlPreProcessing;
import com.cxm.tessi.pfl.shared.flowtreatment.Processing;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.CampaignPreProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.dto.FlowSummaryPage;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.Document;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.FiligraneDto;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction.Attachment;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileTracing;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateFileControlJsonHandler extends AbstractExecutionHandler {

  private static final String MESSAGE_INFORMATION = "Json file control is updated.";

  private final ModelMapper modelMapper;
  private final FileCtrlMngtFeignClient fileCtrlMngtFeignClient;

  public UpdateFileControlJsonHandler(
      ModelMapper modelMapper, FileCtrlMngtFeignClient fileCtrlMngtFeignClient) {
    this.modelMapper = modelMapper;
    this.fileCtrlMngtFeignClient = fileCtrlMngtFeignClient;
  }

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var flowProcessingStep =
        context.get(
            FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.class);

    switch (flowProcessingStep) {
      case ACQUISITION:
        this.updateJsonAfterAcquisitionStep(context);
        break;
      case IDENTIFICATION:
        this.updateJsonAfterIdentification(context);
        break;
      case PRE_PROCESSING:
        this.updateJsonAfterFinishedPreTreatment(context);
        break;
      case PRE_COMPOSITION:
        break;
      case COMPOSITION:
        this.updateJsonAfterFinishedComposition(context);
        break;
      default:
        // do nothing
    }

    return ExecutionState.NEXT;
  }

  private void updateJsonAfterAcquisitionStep(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var bearerToken = ProcessControlExecutionContextUtils.getBearerToken(context);
    var portalFlowFileControl =
        this.fileCtrlMngtFeignClient.getPortalJsonFileControl(
            depositedFlowLaunchRequest.getUuid(), bearerToken);
    portalFlowFileControl.setFileName(depositedFlowLaunchRequest.getFileName());
    this.fileCtrlMngtFeignClient.updatePortalJsonFile(
        portalFlowFileControl, ProcessControlStep.ACQUISITION.getValue(), bearerToken);
  }

  private void updateJsonAfterIdentification(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var bearerToken = ProcessControlExecutionContextUtils.getBearerToken(context);

    log.info("Updating Json file control after flow model is identified.");

    var updateJsonRequest =
        ProcessCtrlIdentificationStep.builder()
            .type(depositedFlowLaunchRequest.getFlowType())
            .modelName(context.get(FlowTreatmentConstants.MODEL_NAME, String.class))
            .modelType(context.get(FlowTreatmentConstants.MODEL_TYPE, String.class))
            .channel(context.get(FlowTreatmentConstants.CHANNEL, String.class))
            .subChannel(context.get(FlowTreatmentConstants.SUB_CHANNEL, String.class))
            .fullName(context.get(FlowTreatmentConstants.USER_FULL_NAME, String.class))
            .build();

    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      this.fileCtrlMngtFeignClient.updateJsonAfterIdentificationStep(
          updateJsonRequest, depositedFlowLaunchRequest.getUuid(), bearerToken);
    } else {
      this.fileCtrlMngtFeignClient.updatePortalJsonAfterIdentificationStep(
          updateJsonRequest, depositedFlowLaunchRequest.getUuid(), bearerToken);
    }

    log.info(MESSAGE_INFORMATION);
  }

  private void updateJsonAfterFinishedPreTreatment(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);

    log.info("Updating json file after finished pre-treatment step.");

    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      var updateJsonRequest =
          context.get(FlowTreatmentConstants.DOCUMENT, ProcessCtrlPreProcessing.class);
      this.fileCtrlMngtFeignClient.updateJsonAfterFinishedPreTreatment(
          updateJsonRequest,
          context.get(FlowTreatmentConstants.FLOW_UUID, String.class),
          ProcessControlExecutionContextUtils.getBearerToken(context));

    } else {
      this.updateJsonAfterFinishedPreTreatmentPortal(context);
    }

    log.info(MESSAGE_INFORMATION);
  }

  private void updateJsonAfterFinishedPreTreatmentPortal(ExecutionContext context) {
    log.info("Updating portal json file after finished pre-treatment step.");
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    var portalFlowFileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (!PortalDepositType.isPortalDepositCampaignType(depositedFlowLaunchRequest.getFlowType())) {
      PortalDocumentResponse portalDocumentResponse =
          context.get(FlowTreatmentConstants.PORTAL_DOCUMENT, PortalDocumentResponse.class);

      if (portalDocumentResponse != null) {
        this.populatePortalFlowFileTracing(
            portalFlowFileControl.getFlow(), portalDocumentResponse.getDocument());
      }
    } else {
      CampaignPreProcessingResponse response =
          context.get(FlowTreatmentConstants.PORTAL_DOCUMENT, CampaignPreProcessingResponse.class);
      if (response != null) {
        this.modelMapper.map(response, portalFlowFileControl.getFlow());
      }
    }

    log.info("Updating portal json file after finished pre-treatment step.");

    if (portalFlowFileControl
            .getFlow()
            .getType()
            .contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS)
        || portalFlowFileControl
            .getFlow()
            .getType()
            .contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_EMAIL)) {
      this.fileCtrlMngtFeignClient.updateJsonAfterFinishedPreTreatmentCampaignSMS(
          portalFlowFileControl.getFlow(),
          depositedFlowLaunchRequest.getUuid(),
          ProcessControlExecutionContextUtils.getBearerToken(context));

    } else {

      PortalFlowFileTracing flow = portalFlowFileControl.getFlow();

      setBackgroundPageToPortalFileControl(flow, context);
      setAttachmentToPortalFileControl(flow, context);
      setSignatureToPortalFileControl(flow,context);
      setWatermarkToPortalFileControl(flow,context);

      this.fileCtrlMngtFeignClient.updateJsonAfterFinishedPreTreatmentPortal(
          flow,
          depositedFlowLaunchRequest.getUuid(),
          ProcessControlExecutionContextUtils.getBearerToken(context));
    }

    log.info(MESSAGE_INFORMATION);
  }

  private void updateJsonAfterFinishedComposition(ExecutionContext context) {
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    if (FlowTreatmentConstants.PORTAL_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      if (!PortalDepositType.isPortalDepositCampaignType(
          depositedFlowLaunchRequest.getFlowType())) {
        this.updateDocumentProcessing(context);
      } else {
        this.updateCampaignDocumentProcessing(context);
      }

    } else {
      var composedFlowFileProcessingList =
          new ArrayList<FileDocumentProcessing>(
              context.get(FlowTreatmentConstants.FLOW_FILE_DOCUMENT_PROCESSING, List.class));

      if (!composedFlowFileProcessingList.isEmpty()) {
        composedFlowFileProcessingList.forEach(
            composedFlowFileProcessing ->
                this.fileCtrlMngtFeignClient.updateJsonAfterFinishedComposition(
                    this.modelMapper.map(composedFlowFileProcessing, Processing.class),
                    depositedFlowLaunchRequest.getUuid(),
                    ProcessControlExecutionContextUtils.getBearerToken(context)));
      }
    }
  }

  private void populatePortalFlowFileTracing(
      PortalFlowFileTracing portalFlowFileTracing, Document document) {

    portalFlowFileTracing.setNbDocuments(document.getNbDocuments());
    portalFlowFileTracing.setNbPages(document.getNbPages());
    portalFlowFileTracing.setNbDocumentsKO(document.getNbDocumentsKO());

    List<PortalFileFlowDocument> portalFlowDocuments = new ArrayList<>();
    document
        .getFlowDocuments()
        .forEach(
            fileFlowDocument -> {
              PortalFileFlowDocument portalFlowDocument =
                  modelMapper.map(fileFlowDocument, PortalFileFlowDocument.class);

              portalFlowDocument.setDocUUID(fileFlowDocument.getUuid());
              portalFlowDocument.setAnalysis(fileFlowDocument.getAnalyse());
              portalFlowDocuments.add(portalFlowDocument);
            });

    portalFlowFileTracing.setFlowDocuments(portalFlowDocuments);
  }

  private void updateDocumentProcessing(ExecutionContext context) {
    log.info("Updating portal json file after finished treatment step.");
    final PortalFlowFileControl portalFlowFileControl =
        context.get(
            FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL_PROCESSING,
            PortalFlowFileControl.class);
    this.fileCtrlMngtFeignClient.updateJsonAfterFinishedTreatmentPortal(
        portalFlowFileControl.getFlow(),
        portalFlowFileControl.getUuid(),
        ProcessControlExecutionContextUtils.getBearerToken(context));

    log.info(MESSAGE_INFORMATION);
  }

  private void updateCampaignDocumentProcessing(ExecutionContext context) {
    List<PortalFileDocumentProcessing> processing =
        context.get(FlowTreatmentConstants.FLOW_FILE_DOCUMENT_PROCESSING, List.class);
    if (log.isDebugEnabled()) {
      log.debug("Flow processing: {}", processing);
    }

    var portalJson =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    IntStream.range(0, processing.size())
        .forEach(
            index ->
                this.updateProcessing(
                    portalJson.getFlow().getFlowDocuments().get(index).getProcessing(),
                    processing.get(index)));
    var portalFileFlow =
        this.fileCtrlMngtFeignClient.updatePortalJsonFile(
            portalJson,
            ProcessControlStep.COMPOSITION.getValue(),
            this.getToken(context, FlowTreatmentConstants.BEARER_TOKEN));
    context.put(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, portalFileFlow);
  }

  private void updateProcessing(
      PortalFileDocumentProcessing portalFileDocumentProcessing,
      PortalFileDocumentProcessing documentProcessing) {
    log.info("Update processing source: {}", documentProcessing);
    log.info("Update processing destination: {}", portalFileDocumentProcessing);
    this.modelMapper.map(
        documentProcessing,
        portalFileDocumentProcessing == null
            ? new PortalFileDocumentProcessing()
            : portalFileDocumentProcessing);
  }

  /** Set backgroundPage to json file. */
  private void setBackgroundPageToPortalFileControl(
      PortalFlowFileTracing portalFlowFileControl, ExecutionContext context) {
    BackgroundPage backgroundPage =
        context.get(ProcessControlConstants.BACKGROUND_DTO, BackgroundPage.class);
    /*    if (ObjectUtils.isEmpty(backgroundPage)) {
      return;
    }*/
    portalFlowFileControl
        .getFlowDocuments()
        .forEach(
            portalFileFlowDocument ->
                portalFileFlowDocument
                    .getProduction()
                    .setBackgroundPage(
                        ObjectUtils.defaultIfNull(backgroundPage, new BackgroundPage())));
  }

  private void setAttachmentToPortalFileControl(
      PortalFlowFileTracing portalFlowFileControl, ExecutionContext context) {
    Integer resourcePageSize =
        ObjectUtils.defaultIfNull(
            context.get(ProcessControlConstants.RESOURCE_PAGE_SIZE, Integer.class), 0);
    FlowSummaryPage flowSummaryPage =
        context.get(ProcessControlConstants.FLOW_SUM_PAGE, FlowSummaryPage.class);
    if (resourcePageSize >= 1 && Objects.nonNull(flowSummaryPage)) {
      int nbDocuments = flowSummaryPage.getNbDocuments();
      int nbPages = flowSummaryPage.getNbPages();
      int multiPageSize = nbDocuments * resourcePageSize;
      int pageNumber = nbPages + multiPageSize;
      portalFlowFileControl.setNbPages(String.valueOf(pageNumber));
    }
    Attachment attachment =
        context.get(ProcessControlConstants.FLOW_DOC_PRODUCTION_ATTACHMENT, Attachment.class);
    portalFlowFileControl
        .getFlowDocuments()
        .forEach(
            portalFileFlowDocument -> {
              int eachPagesNum = Integer.parseInt(portalFileFlowDocument.getNbPages());
              if (Objects.nonNull(flowSummaryPage)) {
                Map<String, Integer> documents = flowSummaryPage.getDocuments();
                eachPagesNum = documents.get(portalFileFlowDocument.getUuid()) + resourcePageSize;
              }
              portalFileFlowDocument.setNbPages(String.valueOf(eachPagesNum));
              portalFileFlowDocument.getProduction().setAttachment(attachment);
            });
  }

  private void setSignatureToPortalFileControl(
      PortalFlowFileTracing flow, ExecutionContext context) {
    String signature =
        ObjectUtils.defaultIfNull(
            context.get(ProcessControlConstants.SIGNATURE_RESOURCE, String.class), "");
    flow.getFlowDocuments().forEach(document -> document.getProduction().setSignature(signature));
  }

  private void setWatermarkToPortalFileControl(
      PortalFlowFileTracing flow, ExecutionContext context) {
    FiligraneDto filigraneDto =
        ObjectUtils.defaultIfNull(
            context.get(ProcessControlConstants.FILIGRANE_DTO, FiligraneDto.class),
            new FiligraneDto());
    String watermark = ObjectUtils.defaultIfNull(filigraneDto.getText(), "");
    flow.getFlowDocuments().forEach(document -> document.getProduction().setWatermark(watermark));
  }
}
