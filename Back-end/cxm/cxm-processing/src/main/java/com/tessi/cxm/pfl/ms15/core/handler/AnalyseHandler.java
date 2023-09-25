package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ProcessingRequest;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.ms15.exception.FailToAnalyseDocumentExceptionHandler;
import com.tessi.cxm.pfl.ms15.model.AnalyseRequest;
import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import java.io.File;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyseHandler extends AbstractExecutionHandler {
  private final Go2pdfResource go2pdfResource;
  private final FileService fileService;

  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var token = context.get(ProcessingConstant.TOKEN_KEY, String.class);
    var analyseRequest = getAnalyseRequest(context);
    var responses = this.go2pdfResource.analyse(analyseRequest, token);
    // validate documents.
    final var numDocs =
        Integer.parseInt(
            context
                .get(ProcessingConstant.PROCESSING_FILE_CONTROL, PortalFlowFileControl.class)
                .getFlow()
                .getNbDocuments());
    if (numDocs > responses.size()) {
      throw new FailToAnalyseDocumentExceptionHandler();
    }
    log.info("analyse response size {}", responses.size());
    context.put(ProcessingConstant.ANALYSED_RESPONSE, responses);
    return ExecutionState.NEXT;
  }

  private AnalyseRequest getAnalyseRequest(ExecutionContext context) {
    var configurationName = "PORTAIL_ANALYSE";
    final ProcessingRequest processingRequest =
        context.get(ProcessingConstant.PROCESSING_REQUEST, ProcessingRequest.class);
    var fileId = context.get(ProcessingConstant.FILE_ID, String.class);
    var base64 = context.get(ProcessingConstant.BASE64_FILE, String.class);
    var configFilePath = context.get(ProcessingConstant.CONFIG_PATH, String.class);
    var filename = fileId.concat(".".concat(FlowTreatmentConstants.PDF_EXTENSION));
    var tmpPath = getTmpFilePath(context);
    log.info("Output directory of analyse documents {}", tmpPath);
    log.info("Configuration path {}", configFilePath);
    log.info("Configuration name {}", configurationName);
    var analyseRequest =
        AnalyseRequest.builder()
            .simpleFile(base64)
            .configFile(configFilePath)
            .outputDir(tmpPath)
            .configName(configurationName)
            .fileName(filename)
            .background(processingRequest.getBackgroundPage());
    // add attachment if present.
    if (Objects.nonNull(processingRequest.getAttachments())) {
      analyseRequest.attachmentDto(processingRequest.getAttachments());
      log.info("Attachments have already been added to analyze the request.");
    }
    // add signature if present.
    if (StringUtils.isNotBlank(processingRequest.getSignature())) {
      analyseRequest.signature(processingRequest.getSignature());
      log.info("Signature have already been added to analyze the request.");
    }
    // add filigrane if present.
    if (Objects.nonNull(processingRequest.getFiligrane())) {
      analyseRequest.filigrane(processingRequest.getFiligrane());
      log.info("Filigrane have already been added to analyze the request.");
    }
    return analyseRequest.build();
  }

  private String getTmpFilePath(ExecutionContext context) {
    final String fileId = context.get(ProcessingConstant.FILE_ID, String.class);
    var path = fileService.getPath(PreProcessingConstant.COMPOSED_PDF_PATH, fileId);
    return path.toString().concat(File.separator);
  }
}
