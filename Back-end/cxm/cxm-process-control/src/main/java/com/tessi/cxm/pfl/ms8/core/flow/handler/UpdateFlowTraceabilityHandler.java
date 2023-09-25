package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlPreProcessing;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.model.FlowTreatmentFlowRequestWrapper.Production;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.FiligraneDto;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.kafka.CompositionUpdateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.model.kafka.CompositionUpdateFlowTraceabilityModel.Document;
import com.tessi.cxm.pfl.shared.model.kafka.IdentificationUpdateFlowModel;
import com.tessi.cxm.pfl.shared.model.kafka.PreProcessingUpdateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowStatusModel;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatusConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@AllArgsConstructor
public class UpdateFlowTraceabilityHandler extends FlowTraceabilityHandler {

  private static final String MESSAGE_INFO = "FlowTraceability message is produced.";
  private final ModelMapper modelMapper;
  private final StreamBridge streamBridge;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var flowProcessingStep =
        context.get(
            FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, ProcessControlStep.class);

    switch (flowProcessingStep) {
      case IDENTIFICATION:
        this.updateFlowAfterIdentification(context);
        break;
      case PRE_PROCESSING:
        this.updateFlowAfterPreTreatmentStep(context);
        break;
      case PRE_COMPOSITION:
        // Do nothing
        break;
      case COMPOSITION:
        this.updateFlowTraceabilityAfterCompositionStep(context);
        break;
      case SWITCH:
        this.updateFlowSwitchStep(context);
        break;
      default:
        break;
    }

    return ExecutionState.NEXT;
  }

  private void updateFlowAfterIdentification(ExecutionContext context) {
    log.info("Producing message to update FlowTraceability after flow model is identified.");
    var subChannel = context.get(FlowTreatmentConstants.SUB_CHANNEL, String.class);
    var flowModelIdentification =
        new IdentificationUpdateFlowModel(
            FlowTraceabilityStatus.IN_PROCESS.getValue(),
            context.get(FlowTreatmentConstants.USERNAME, String.class),
            context.get(FlowTreatmentConstants.CHANNEL, String.class),
            subChannel == null ? "" : subChannel,
            ComputerSystemProduct.getDeviceId(),
            context.get(FlowTreatmentConstants.FLOW_UUID, String.class));
    if (!context
        .get(FlowTreatmentConstants.FLOW_TYPE, String.class)
        .contains(FlowTreatmentConstants.PORTAL_DEPOSIT)) {
      flowModelIdentification.setDepositType(FlowTreatmentConstants.PORTAL_DEPOSIT);
    }

    // add campaign batch details from version 4.0 (BatchTreatment_ENI_V4.0.drawio)
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(
        depositedFlowLaunchRequest.getDepositType())) {
      flowModelIdentification.setDepositType(FlowTreatmentConstants.BATCH_DEPOSIT);
    }
    this.streamBridge.send(
        KafkaUtils.UPDATE_FLOW_AFTER_IDENTIFICATION_STEP_TOPIC, flowModelIdentification);
    log.info(MESSAGE_INFO);
  }

  private void updateFlowAfterPreTreatmentStep(ExecutionContext context) {
    var depositType =
        context
            .get(
                FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
                DepositedFlowLaunchRequest.class)
            .getDepositType();
    if (FlowTreatmentConstants.PORTAL_DEPOSIT.equalsIgnoreCase(depositType)) {
      this.updatePortalFlowAfterPreTreatmentStep(context);
    } else {

      log.info("Producing message to update FlowTraceability after finished pre-treatment step.");

      var flowModelPreTreatment = new PreProcessingUpdateFlowTraceabilityModel();
      flowModelPreTreatment.setFileId(context.get(FlowTreatmentConstants.FLOW_UUID, String.class));
      flowModelPreTreatment.setStatus(FlowTraceabilityStatus.TREATMENT.getValue());
      flowModelPreTreatment.setServer(ComputerSystemProduct.getDeviceId());
      flowModelPreTreatment.setCreatedBy(
          context.get(FlowTreatmentConstants.USERNAME, String.class));
      List<FileFlowDocument> flowDocuments = new ArrayList<>();
      var document = context.get(FlowTreatmentConstants.DOCUMENT, ProcessCtrlPreProcessing.class);
      document
          .getFlowDocuments()
          .forEach(
              doc -> {
                doc.setOffset("1");
                doc.setNbPages("1");
                flowDocuments.add(doc);
              });
      flowModelPreTreatment.setFlowDocuments(flowDocuments);
      flowModelPreTreatment.setNbDocuments(document.getNbDocuments());
      flowModelPreTreatment.setNbDocumentsKo(document.getNbDocumentsKo());
      flowModelPreTreatment.setNbPages(document.getNbPages());

      // update batch status history to treatment from version 4.0 (BatchTreatment_ENI_V4.0.drawio)
      if (FlowTreatmentConstants.BATCH_DEPOSIT.equalsIgnoreCase(depositType)) {
        flowModelPreTreatment.setStatus(FlowTraceabilityStatus.TREATMENT.getValue());
        flowModelPreTreatment.setFlowType(depositType);
      }
      // Produce kafka message to update flow and create flow document.
      this.streamBridge.send(
          KafkaUtils.UPDATE_FLOW_AFTER_PRE_TREATMENT_STEP_TOPIC, flowModelPreTreatment);

      log.info(MESSAGE_INFO);
    }
  }

  private void updateFlowTraceabilityAfterCompositionStep(ExecutionContext context) {

    var flowTraceabilityModel = new CompositionUpdateFlowTraceabilityModel();

    flowTraceabilityModel.setFileId(context.get(FlowTreatmentConstants.FLOW_UUID, String.class));
    flowTraceabilityModel.setServer(ComputerSystemProduct.getDeviceId());
    var modifiedBy = context.get(FlowTreatmentConstants.USERNAME, String.class);
    flowTraceabilityModel.setCreatedBy(modifiedBy);
    List<Document> documents = new ArrayList<>();
    log.info(
        "Producing message to update document of FlowTraceability after finished composition step.");
    var depositRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    if (depositRequest.getDepositType().equals(FlowTreatmentConstants.BATCH_DEPOSIT)) {

      // update batch status history to treatment from version 4.0 (BatchTreatment_ENI_V4.0.drawio)
      flowTraceabilityModel.setStatus(FlowTraceabilityStatus.TREATMENT.getValue());
      var composedFile = context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class);
      var htmlContent =
          context.get(
              FlowTreatmentConstants.BASE64_NAME.concat("_".concat(composedFile)), String.class);
      flowTraceabilityModel.setHtmlContent(htmlContent);
      var composedFlowFileProcessingList =
          new ArrayList<FileDocumentProcessing>(
              context.get(FlowTreatmentConstants.FLOW_FILE_DOCUMENT_PROCESSING, List.class));

      composedFlowFileProcessingList.forEach(
          composedFlowFileProcessing ->
              documents.add(
                  Document.builder()
                      .size(Integer.parseInt(composedFlowFileProcessing.getSize()))
                      .docId(composedFlowFileProcessing.getDocUuid())
                      .serverName(composedFlowFileProcessing.getServerName())
                      .docName(composedFlowFileProcessing.getDocName())
                      .status(flowTraceabilityModel.getStatus())
                      .modifiedBy(modifiedBy)
                      .build()));
    }

    if (PortalDepositType.isPortalDepositCampaignType(depositRequest.getFlowType())) {
      boolean isSetSchedule = context.get(FlowTreatmentConstants.IS_SET_SCHEDULE, Boolean.class);
      if (isSetSchedule) {
        flowTraceabilityModel.setDateSchedule(
            context.get(ProcessControlConstants.DATE_SCHEDULE, Date.class));
        flowTraceabilityModel.setStatus(FlowTraceabilityStatus.SCHEDULED.getValue());
      } else {
        flowTraceabilityModel.setStatus(FlowTraceabilityStatus.IN_PROCESS.getValue());
      }
      var portalJson =
          context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);

      portalJson
          .getFlow()
          .getFlowDocuments()
          .forEach(
              doc -> {
                var processing = doc.getProcessing();
                var isAnalysisOk = doc.getAnalysis().equals("OK");
                var status = "";
                if (!isAnalysisOk) {
                  status = FlowDocumentStatusConstant.IN_ERROR;
                } else {
                  status =
                      isSetSchedule
                          ? FlowDocumentStatusConstant.SCHEDULED
                          : FlowDocumentStatusConstant.IN_PROGRESS;
                }
                documents.add(
                    Document.builder()
                        .size(Integer.parseInt(processing.getSize()))
                        .docId(FilenameUtils.getBaseName(processing.getDocName()))
                        .modifiedBy(modifiedBy)
                        .status(status)
                        .docName(processing.getDocName())
                        .build());
              });
    }
    flowTraceabilityModel.setDocuments(documents);
    this.streamBridge.send(
        KafkaUtils.UPDATE_FLOW_AFTER_COMPOSITION_STEP_TOPIC, flowTraceabilityModel);
    log.info("Document of FlowTraceability message is produced.");
  }

  private void updatePortalFlowAfterPreTreatmentStep(ExecutionContext context) {

    var response =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    final Production production =
        context.get(FlowTreatmentConstants.FLOW_PRODUCTION, Production.class);
    log.info("Producing message to update FlowTraceability after finished pre-treatment step.");

    var flowModelPreTreatment = new PreProcessingUpdateFlowTraceabilityModel();
    flowModelPreTreatment.setFileId(response.getUuid());
    flowModelPreTreatment.setServer(ComputerSystemProduct.getDeviceId());
    flowModelPreTreatment.setCreatedBy(response.getUserName());
    // Condition for PDF flow deposit
    var isPdfPortal = PortalDepositType.isPortalDepositPdfType(response.getFlow().getType());
    if (isPdfPortal) {
      flowModelPreTreatment.setStatus(FlowTraceabilityStatus.TREATMENT.getValue());
      flowModelPreTreatment.setSubChannel(this.mappingUrgencyToSubChannel(production.getUrgency()));
      final BackgroundPage backgroundPage =
          context.get(ProcessControlConstants.BACKGROUND_DTO, BackgroundPage.class);

      final Attachments attachments = context.get(ProcessControlConstants.ATTACHMENT_DTO,
          Attachments.class);

      final String signature = context.get(ProcessControlConstants.SIGNATURE_RESOURCE,
          String.class);
      final FiligraneDto filigraneDto =
          context.get(ProcessControlConstants.FILIGRANE_DTO, FiligraneDto.class);

      if (Objects.nonNull(backgroundPage)) {
        flowModelPreTreatment.setBackgroundPage(backgroundPage);
      }
      if (Objects.nonNull(attachments)) {
        flowModelPreTreatment.setAttachments(attachments);
      }
      if (Objects.nonNull(signature)) {
        flowModelPreTreatment.setSignature(signature);
      }
      if (Objects.nonNull(filigraneDto)) {
        String watermark = Objects.requireNonNullElse(filigraneDto.getText(), "");
        flowModelPreTreatment.setWatermark(watermark);
      }
    }

    if (PortalDepositType.isPortalDepositCampaignType(response.getFlow().getType())) {
      flowModelPreTreatment.setStatus(FlowTraceabilityStatus.TREATMENT.getValue());
      flowModelPreTreatment.setFlowType(response.getFlow().getType());
    }

    List<FileFlowDocument> flowDocuments = new ArrayList<>();
    response
        .getFlow()
        .getFlowDocuments()
        .forEach(
            doc -> {
              var fileFlow = this.modelMapper.map(doc, FileFlowDocument.class);
              if (isPdfPortal) {
                fileFlow.setSubChannel(this.mappingUrgencyToSubChannel(production.getUrgency()));
                fileFlow.getProduction().setColor(production.getColor());
                //                if(!Objects.isNull(backgroundPage)){
                //                  fileFlow.getProduction().setBackgroundPage(backgroundPage);
                //                }
              }
              flowDocuments.add(fileFlow);
            });
    flowModelPreTreatment.setFlowDocuments(flowDocuments);
    flowModelPreTreatment.setNbDocuments(response.getFlow().getNbDocuments());
    flowModelPreTreatment.setNbDocumentsKo(response.getFlow().getNbDocumentsKO());
    flowModelPreTreatment.setNbPages(response.getFlow().getNbPages());

    // Produce kafka message to update flow and create flow document.
    this.streamBridge.send(
        KafkaUtils.UPDATE_FLOW_AFTER_PRE_TREATMENT_STEP_TOPIC_PORTAL, flowModelPreTreatment);

    log.info(MESSAGE_INFO);
  }

  private void updateFlowSwitchStep(ExecutionContext context) {
    var updateFlowStatus =
        context.get(FlowTreatmentConstants.SWITCH_FLOW_DOCS_KAFKA, UpdateFlowStatusModel.class);
    var flowType = context.get(FlowTreatmentConstants.FLOW_TYPE, String.class);
    // Pdf flow
    if (PortalDepositType.isPortalDepositPdfType(flowType)) {
      updateFlowStatus
          .getDocuments()
          .forEach(doc -> doc.setSubChannel(this.mappingUrgencyToSubChannel(doc.getPostage())));
      var unloadingDate = context.get(FlowTreatmentConstants.FORCE_UNLOADING_DATE, Date.class);
      if (Objects.nonNull(unloadingDate)) {
        updateFlowStatus.setUnloadingDate(unloadingDate);
      }
      this.streamBridge.send(KafkaUtils.UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC, updateFlowStatus);
    }
    // Campaign flow
    if (PortalDepositType.isPortalDepositCampaignType(flowType)) {
      this.streamBridge.send(
          KafkaUtils.CAMPAIGN_FLOW_DOCUMENT_STATUS_CHANGE_TOPIC, updateFlowStatus);
    }
    // Batch flow
    if (PortalDepositType.isBatchDepositType(flowType)) {
      this.streamBridge.send(KafkaUtils.UPDATE_FLOW_AFTER_SWITCH_STEP_TOPIC, updateFlowStatus);
    }
  }

  private String mappingUrgencyToSubChannel(String urgency) {
    if (!StringUtils.hasText(urgency)) {
      return "";
    }
    if (urgency.equalsIgnoreCase("R1 avec AR")) {
      return "Reco AR";
    }
    return urgency;
  }
}
