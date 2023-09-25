package com.tessi.cxm.pfl.ms8.model;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.ms8.constant.Go2pdfBackgroundPosition;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileTracing;
import com.tessi.cxm.pfl.shared.model.ProcessingResponse;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/** Class for handle response information base on step. */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DepositStepResponse implements Serializable {

  private AcquisitionResponse processControlRequest; // step 1
  private ProcessCtrlIdentificationResponse processControlResponse; // step 2
  private AnalyzeResponse analyzeResponse; // step 3
  private ProcessingResponse treatmentResponse; // step 4
  private SwitchFlowResponse switchResponse; // step 5

  /**
   * Build response data from json based on the specific step.
   *
   * @param fileControl RefreshScope to the object of {@link PortalFlowFileControl}
   * @param step refers the step to build data
   */
  public DepositStepResponse(PortalFlowFileControl fileControl, int step) {
    switch (step) {
      case 5:
        this.switchResponse = buildSwitchFlowRes(fileControl);
        this.treatmentResponse = buildDocumentProcessing(fileControl);
        this.analyzeResponse = buildAnalyzeRes(fileControl.getFlow());
        this.processControlResponse = buildIdentifyRes(fileControl.getFlow());
        this.processControlRequest = buildDepositFlowReq(fileControl);
        break;
      case 4:
        this.treatmentResponse = buildDocumentProcessing(fileControl);
        this.analyzeResponse = buildAnalyzeRes(fileControl.getFlow());
        this.processControlResponse = buildIdentifyRes(fileControl.getFlow());
        this.processControlRequest = buildDepositFlowReq(fileControl);
        break;
      case 3:
        this.analyzeResponse = buildAnalyzeRes(fileControl.getFlow());
        this.processControlResponse = buildIdentifyRes(fileControl.getFlow());
        this.processControlRequest = buildDepositFlowReq(fileControl);
        break;
      case 2:
        this.processControlResponse = buildIdentifyRes(fileControl.getFlow());
        this.processControlRequest = buildDepositFlowReq(fileControl);
        break;
      case 1:
        this.processControlRequest = buildDepositFlowReq(fileControl);
        break;
      default:
        break;
        // do nothing
    }
  }

  /** Method build information for step 1. */
  private AcquisitionResponse buildDepositFlowReq(PortalFlowFileControl flowFileControl) {
    return AcquisitionResponse.builder()
        .customer(flowFileControl.getCustomer())
        .depositType(flowFileControl.getDepositType())
        .connector("")
        .fileName(flowFileControl.getFileName())
        .extension(flowFileControl.getExtension())
        .size(Long.valueOf(flowFileControl.getSize()))
        .fileId(flowFileControl.getUuid())
        .flowType(flowFileControl.getFlow().getType())
        .uuid(flowFileControl.getUuid())
        .serviceId(flowFileControl.getServiceId())
        .idCreator(flowFileControl.getUserId())
        .depositDate(flowFileControl.getDepositDate())
        .build();
  }

  /** Method build information for step 2. */
  private ProcessCtrlIdentificationResponse buildIdentifyRes(
      PortalFlowFileTracing flowFileControl) {
    return new ProcessCtrlIdentificationResponse(
        flowFileControl.getModelName(),
        flowFileControl.getModelType(),
        flowFileControl.getChannel(),
        flowFileControl.getSubChannel(),
        new BackgroundPage(),
        new Attachments(),
        "");
  }

  /** Method for build information for step 3. */
  private AnalyzeResponse buildAnalyzeRes(PortalFlowFileTracing flowFileControl) {
    return new AnalyzeResponse(flowFileControl);
  }

  /** Method build information for step 4. */
  private ProcessingResponse buildDocumentProcessing(PortalFlowFileControl flowFileControl) {
    final var portalFileDocumentProcessings =
        flowFileControl.getFlow().getFlowDocuments().stream()
            .map(PortalFileFlowDocument::getProcessing)
            .collect(Collectors.toList());
    ProcessingResponse processingResponse = new ProcessingResponse();
    processingResponse.setDocumentProcessing(portalFileDocumentProcessings);
    flowFileControl.getFlow().getFlowDocuments().stream()
        .findFirst()
        .ifPresent(
            document -> {
              BackgroundPage backgroundPageDto = new BackgroundPage();
              BackgroundPage backgroundPage = document.getProduction().getBackgroundPage();
              if (backgroundPage != null) {
                if (StringUtils.isNotBlank(backgroundPage.getPositionFirst())
                    && isFilePresent(backgroundPage.getBackgroundFirst())) {
                  backgroundPageDto.setBackgroundFirst(
                      FilenameUtils.getName(backgroundPage.getBackgroundFirst()));
                  backgroundPageDto.setPositionFirst(BackgroundPosition.FIRST_PAGE.value);
                }
                if (StringUtils.isNotBlank(backgroundPage.getPosition())
                    && isFilePresent(backgroundPage.getBackground())) {
                  final String positionKey =
                      Go2pdfBackgroundPosition.getKeyByValue(backgroundPage.getPosition()).getKey();
                  backgroundPageDto.setBackground(
                      FilenameUtils.getName(backgroundPage.getBackground()));
                  backgroundPageDto.setPosition(positionKey);
                }
                if (StringUtils.isNotBlank(backgroundPage.getPositionLast())
                    && isFilePresent(backgroundPage.getPositionLast())) {
                  backgroundPageDto.setBackgroundLast(
                      FilenameUtils.getName(backgroundPage.getBackgroundLast()));
                  backgroundPageDto.setPositionLast(BackgroundPosition.LAST_PAGE.value);
                }
                processingResponse.setBackgroundPage(backgroundPageDto);
              }

              var signature = document.getProduction().getSignature();
              var waterwark =
                  StringUtils.defaultIfBlank(document.getProduction().getWatermark(), "");
              processingResponse.setSignature(signature);
              processingResponse.setWatermark(waterwark);
            });
    return processingResponse;
  }

  private boolean isFilePresent(String filename) {
    return FilenameUtils.isExtension(filename, "pdf");
  }

  /** Method build information for step 5. */
  private SwitchFlowResponse buildSwitchFlowRes(PortalFlowFileControl flowFileControl) {
    return new SwitchFlowResponse(flowFileControl);
  }

  @Setter
  @Getter
  @AllArgsConstructor
  private static class AnalyzeResponse implements Serializable {

    @JsonProperty("NbDocuments")
    private String nbDocuments;

    @JsonProperty("NbPages")
    private String nbPages;

    @JsonProperty("NbDocumentsKO")
    private String nbDocumentsKo;

    @JsonProperty("DOCUMENT")
    private List<PortalFileFlowDocument> flowDocuments;

    public AnalyzeResponse(PortalFlowFileTracing portalFlowFileTracing) {
      this.nbDocuments = portalFlowFileTracing.getNbDocuments();
      this.nbPages = portalFlowFileTracing.getNbPages();
      this.nbDocumentsKo = portalFlowFileTracing.getNbDocumentsKO();
      this.flowDocuments = portalFlowFileTracing.getFlowDocuments();
    }
  }

  @Setter
  @Getter
  @Builder
  @AllArgsConstructor
  private static class AcquisitionResponse implements Serializable {

    private String customer;
    private String depositType;
    private String connector;
    private String fileName;
    private String extension;
    private Long size;
    private String depositDate;
    private String fileId;
    private String flowType;
    private String uuid;
    private String serverName;
    private String idCreator;
    private String serviceId;
    private String serviceName;
    private String userName;
  }
}
