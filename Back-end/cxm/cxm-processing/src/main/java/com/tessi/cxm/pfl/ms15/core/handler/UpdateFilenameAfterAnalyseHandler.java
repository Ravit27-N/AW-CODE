package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.ms15.model.AnalyseResponse;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateFilenameAfterAnalyseHandler extends AbstractExecutionHandler {
  private static final String XML_EXTENSION = ".xml";
  private final FileService fileService;
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var analyseResponse = getAnalyseResponse(context);
    if (CollectionUtils.isNotEmpty(analyseResponse)) {
      var fileId = context.get(ProcessingConstant.FILE_ID, String.class);
      var zipExtension = ".".concat(FlowTreatmentConstants.ZIP_EXTENSION);
      var tmpFilePath = getTmpFilePath();
      log.info("Temporary pdf path :{}", tmpFilePath.toString());
      var fileControl =
          context.get(ProcessingConstant.PROCESSING_FILE_CONTROL, PortalFlowFileControl.class);
      renameFiles(analyseResponse, fileControl, tmpFilePath.resolve(fileId));
      zipRenameFiles(tmpFilePath.resolve(fileId));
      context.put(ProcessingConstant.TMP_FILE, tmpFilePath);
      context.put(ProcessingConstant.TMP_FILE_NAME, fileId.concat(zipExtension));
      context.put(FileManagerHandler.OPTION_KEY, FileManagerHandler.Option.POST);
    }

    return ExecutionState.NEXT;
  }

  @SuppressWarnings("unchecked")
  private List<AnalyseResponse> getAnalyseResponse(ExecutionContext context) {
    return context.get(ProcessingConstant.ANALYSED_RESPONSE, List.class);
  }

  private Path getTmpFilePath() {
    return this.fileService.getPath(PreProcessingConstant.COMPOSED_PDF_PATH);
  }

  private void renameFiles(
      List<AnalyseResponse> analyseResponses,
      PortalFlowFileControl portalFlowFileControl,
      Path outputFile) {
    var fileDocumentProcessings =
        portalFlowFileControl.getFlow().getFlowDocuments().stream()
            .map(PortalFileFlowDocument::getProcessing)
            .collect(Collectors.toList());
    IntStream.range(0, analyseResponses.size())
        .forEach(
            index -> {
              var analyseResponse = analyseResponses.get(index);
              var portalFileDocumentProcessing = fileDocumentProcessings.get(index);
              var source = outputFile.resolve(analyseResponse.getDocName());

              this.fileService.moveFile(
                  source.toString(),
                  source.resolveSibling(portalFileDocumentProcessing.getDocName()).toString());
              var existXmlFile =
                  FilenameUtils.getBaseName(portalFileDocumentProcessing.getDocName())
                      .concat(XML_EXTENSION);
              var newXmlFile =
                  FilenameUtils.getBaseName(analyseResponse.getDocName()).concat(XML_EXTENSION);
              this.fileService.moveFile(
                  source.resolveSibling(newXmlFile).toString(),
                  source.resolveSibling(existXmlFile).toString());
            });
  }

  private void zipRenameFiles(Path path) {
    this.fileService.compressFile(path);
  }
}
