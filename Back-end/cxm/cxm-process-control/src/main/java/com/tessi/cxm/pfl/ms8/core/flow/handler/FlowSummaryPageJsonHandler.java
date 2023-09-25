package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.dto.FlowSummaryPage;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FlowSummaryPageJsonHandler extends AbstractExecutionHandler {
  private static final String EXTENSION = ".json";
  private final FileService fileService;

  public FlowSummaryPageJsonHandler(LocalFileConfig localFileConfig) {
    this.fileService = new FileServiceImpl(localFileConfig.getPath(), "");
  }

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    writeJsonFile(context);
    readJsonFile(context);
    return ExecutionState.NEXT;
  }

  private void writeJsonFile(ExecutionContext context) {
    final String flowId =
        context
            .get(
                FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
                DepositedFlowLaunchRequest.class)
            .getUuid();

    File jsonFile = fileService.getPath(flowId).resolve(flowId + EXTENSION).toFile();
    if (jsonFile.exists()) {
      return;
    }

    final PortalFlowFileControl portalFlowFileControl =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    FlowSummaryPage flowSummaryPage = new FlowSummaryPage();
    flowSummaryPage.setNbDocuments(
        Integer.parseInt(portalFlowFileControl.getFlow().getNbDocuments()));
    flowSummaryPage.setNbPages(Integer.parseInt(portalFlowFileControl.getFlow().getNbPages()));
    Map<String, Integer> documents =
        portalFlowFileControl.getFlow().getFlowDocuments().stream()
            .collect(
                Collectors.toMap(
                    PortalFileFlowDocument::getUuid,
                    fileFlowDocument -> Integer.parseInt(fileFlowDocument.getNbPages())));
    flowSummaryPage.setDocuments(documents);

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writeValue(jsonFile, flowSummaryPage);
    } catch (IOException e) {
      log.error("{0}", e);
    }
  }

  private void readJsonFile(ExecutionContext context) {
    final String flowId =
        context
            .get(
                FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
                DepositedFlowLaunchRequest.class)
            .getUuid();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      File jsonFile = fileService.getPath(flowId).resolve(flowId + EXTENSION).toFile();
      FlowSummaryPage flowSummaryPage = objectMapper.readValue(jsonFile, FlowSummaryPage.class);
      context.put(ProcessControlConstants.FLOW_SUM_PAGE, flowSummaryPage);
    } catch (IOException e) {
      log.error("{0}", e);
    }
  }
}
