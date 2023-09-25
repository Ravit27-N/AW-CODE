package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.Base64FileSupporter;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handling process of draft files.
 *
 * @author Piseth KHON
 * @version 1.11.0
 * @since 12 oct 2022
 */
@Component
@Slf4j
public class DraftFileResourceHandler extends Base64FileSupporter {

  protected DraftFileResourceHandler(FileManagerResource fileManagerResource) {
    super(fileManagerResource);
  }

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
    final String token = ProcessControlExecutionContextUtils.getBearerToken(context);
    final String funckey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    final String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    var portalJson =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (portalJson.getFlow().getType().contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS)) {
      List<PortalFileDocumentProcessing> documentProcessing =
          FlowDocumentValidationProcessingHandler.cast(
              context.get(FlowTreatmentConstants.FLOW_FILE_DOCUMENT_PROCESSING, Object.class));

      final String base64File =
          base64File(context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class), funckey, privKey, token);
      try {
        final List<MultipartFile> files =
            convertBase64ToMultiPart(base64File, FlowTreatmentConstants.HTML_EXTENSION);
        portalJson.getFlow().getFlowDocuments().stream()
            .findFirst()
            .ifPresent(
                document ->
                    context.put(
                        ProcessControlConstants.HTML_CONTENT_PORTAL_DEPOSIT_CAMPAIGN,
                        this.convertHtmlToPlainText(
                            Objects.requireNonNull(
                                files.stream()
                                    .filter(f -> getFileName(f).equals(document.getDocUUID()))
                                    .findFirst()
                                    .orElse(null)))));

        files.forEach(
            file ->
                documentProcessing.stream()
                    .filter(
                        doc ->
                            Objects.equals(
                                FilenameUtils.getBaseName(file.getOriginalFilename()),
                                FilenameUtils.getBaseName(doc.getDocName())))
                    .findFirst()
                    .ifPresent(
                        document ->
                            uploadToFileManager(
                                file,
                                FilenameUtils.getBaseName(document.getDocName()),
                                Long.parseLong(portalJson.getUserId()),
                                funckey,
                                privKey,
                                token)));
      } catch (IOException e) {
        log.error("Unable to convert file base64 to multipart", e);
        throw new ExecutionException("Unable to convert file base64 to multipart.");
      }
    }
    return ExecutionState.NEXT;
  }

  private String convertHtmlToPlainText(MultipartFile multipartFile) {
    try {
      return this.convertInputStreamToString(multipartFile.getInputStream());
    } catch (IOException e) {
      log.error("Unable to convert file html to plain text", e);
      throw new ExecutionException("Unable to convert file html to plain text.");
    }
  }

  /**
   * To get filename from multipart file {@link MultipartFile}.
   *
   * @param multipartFile refer to multipart file content {@link MultipartFile}.
   * @return name of multipart.
   */
  private String getFileName(MultipartFile multipartFile) {
    return FilenameUtils.getBaseName(multipartFile.getOriginalFilename());
  }
}
