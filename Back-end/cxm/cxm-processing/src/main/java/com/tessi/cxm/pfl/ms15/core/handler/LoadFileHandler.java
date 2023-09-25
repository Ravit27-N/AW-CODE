package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handling processing of loading file from cxm-file-manager.
 *
 * @author  Vichet CHANN
 * @since 01 Jun 2022
 * @version 1.5.0
 */
@Component
@RequiredArgsConstructor
public class LoadFileHandler extends AbstractExecutionHandler {

  private final FileManagerResource fileManagerResource;
  private final FileService fileService;

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
    this.storeBase64File(context);
    return ExecutionState.NEXT;
  }

  /**
   * To store in local path after get from cxm-file-manager.
   *
   * @param context object of {@link ExecutionContext}
   */
  private void storeBase64File(ExecutionContext context) {
    var request =
        context.get(
            PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_REQUEST,
            CampaignPreProcessingRequest.class);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    String token = context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class);
    String base64 = this.fileManagerResource.getFile(request.getFileId(),
        funcKey,
        privKey,
        token
    ).getContent();
    String tempPath = "temp";
    Path rootPath = this.fileService.getPath(tempPath, request.getFileId());
    var destinationPath =
        rootPath.resolve(
            request.getFileId().concat(".").concat(FlowTreatmentConstants.ZIP_EXTENSION));
    this.fileService.decodeBase64ToFile(base64, destinationPath.toString());
    this.fileService.unZipCommand(destinationPath, rootPath);
    this.fileService.deleteDirectoryQuietly(destinationPath);
    context.put(PreProcessingConstant.FILE_PATH, rootPath);
  }
}
