package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handling process of uploading file to cxm-file-manager.
 *
 * @author Vichet CHANN
 * @version 1.11.0
 * @since 11 October 2022
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UploadPortalDocumentHandler extends AbstractExecutionHandler {

  private static final String TEMP_PATH = "tmp";

  private final FileManagerResource fileManagerResource;

  private final FileService fileService;

  /**
   * Handling the execution process after call.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return object of {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    this.uploadDocument(context);
    return ExecutionState.NEXT;
  }

  private Path getFile(ExecutionContext context) {
    var uuid = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
    String token = BearerAuthentication.PREFIX_TOKEN.concat(context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class));
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    var fileMetadata =
        this.fileManagerResource.getFile(
            context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class), funcKey, privKey, token);

    var pathFile = this.fileService.getPath(TEMP_PATH, uuid);
    // decode from base64 to zip file
    this.fileService.decodeBase64ToFile(
        fileMetadata.getContent(),
        pathFile.resolve(uuid.concat(".").concat(FlowTreatmentConstants.ZIP_EXTENSION)).toString());
    var destination = this.fileService.getPath(uuid);
    // unzip file after decode
    this.fileService.unZipCommand(
        pathFile.resolve(uuid.concat(".").concat(FlowTreatmentConstants.ZIP_EXTENSION)),
        destination);
    // delete a zip file
    this.fileService.deleteDirectoryQuietly(pathFile);
    return destination;
  }

  private void uploadDocument(ExecutionContext context) {

    var filePath = this.getFile(context);
    var portalJsonFile =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    final long idCreator = Long.parseLong(portalJsonFile.getUserId());
    final String bearerToken = ProcessControlExecutionContextUtils.getBearerToken(context);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    portalJsonFile.getFlow().getFlowDocuments().parallelStream()
        .forEach(
            doc ->
            {
              try {
                this.fileManagerResource.uploadFile(
                    this.fileService.fileToMultipartFile(
                        filePath.resolve(doc.getProcessing().getDocName()).toFile()),
                    idCreator,
                    doc.getUuid(),
                    portalJsonFile.getUuid(),
                    "",
                    0,
                    false,
                    funcKey,
                    privKey,
                    bearerToken);
                // delete file after upload
                this.fileService.delete(
                    doc.getProcessing().getDocName(), filePath.toString());
              } catch (IOException e) {
                log.error("Unable to find a file", e);
                throw new FileNotFoundException(
                    "Unable to find a file with name: "
                        + doc.getProcessing().getDocName());
              }
            });
  }
}
