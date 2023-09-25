package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.shared.core.chains.Base64FileSupporter;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.HtmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConvertBase64ToFileHandler extends Base64FileSupporter {
  private static final String[] IMAGE_FILE_EXTENSION = {
    "png", "jpg", "gif", "jpeg", "jpe", "jif", "jfif", "jfi", "webp", "tiff", "tif", "psd", "pdf"
  };

  protected ConvertBase64ToFileHandler(FileManagerResource fileManagerResource) {
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
    log.info("--- Start convert file to base64 ---");
    var depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    log.info("DepositType = '" + depositedFlowLaunchRequest.getDepositType() + "', " +
            "Extension = '" + depositedFlowLaunchRequest.getExtension() + "'");
    if (depositedFlowLaunchRequest.getDepositType().equals(FlowTreatmentConstants.BATCH_DEPOSIT) &&
            !"pdf".equals(depositedFlowLaunchRequest.getExtension())) {
      log.info("--- End convert file to base64 (batch) ---");
      return getExecutionStateForBatch(context);
    }
    log.info("--- End convert file to base64 (portal/pdf) ---");
    return ExecutionState.NEXT;
  }

  private ExecutionState getExecutionStateForBatch(ExecutionContext context) {
    final String fileId = context.get(FlowTreatmentConstants.FILE_ID, String.class);
    String token = BearerAuthentication.PREFIX_TOKEN.concat(context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class));
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    final String base64File = base64File(fileId, funcKey, privKey, token);
    AtomicReference<String> htmlContent = new AtomicReference<>("");
    List<String> fileExtensions = Arrays.stream(IMAGE_FILE_EXTENSION).collect(Collectors.toList());
    fileExtensions.add(FlowTreatmentConstants.HTML_EXTENSION);
    try {
      log.info("base64File = '" + base64File + "', fileExtensions = '" + fileExtensions + "'");
      final List<MultipartFile> files = convertBase64ToMultiPart(base64File, fileExtensions);
      files.stream()
          .filter(
              file ->
                  FlowTreatmentConstants.HTML_EXTENSION.equalsIgnoreCase(
                      FilenameUtils.getExtension(file.getOriginalFilename())))
          .findFirst()
          .ifPresent(
              multipartFile -> {
                try {
                  htmlContent.set(convertInputStreamToString(multipartFile.getInputStream()));
                } catch (IOException e) {
                  log.error("Unable to convert file to plain text.", e);
                }
              });
      htmlContent.set(replaceHtmlContentVariables(htmlContent.get(), files));
    } catch (IOException e) {
      log.error("Unable to convert file base64 to multipart", e);
    }
    context.put(FlowTreatmentConstants.BASE64_NAME.concat("_".concat(fileId)), htmlContent.get());
    return ExecutionState.NEXT;
  }

  private String replaceHtmlContentVariables(String htmlContent, List<MultipartFile> files) {

    List<String> base64 = new ArrayList<>();
    List<String> filenames = new ArrayList<>();
    files.stream()
        .filter(
            multipartFile ->
                Arrays.stream(IMAGE_FILE_EXTENSION)
                    .anyMatch(
                        ext ->
                            ext.equalsIgnoreCase(
                                FilenameUtils.getExtension(multipartFile.getOriginalFilename()))))
        .forEach(
            multipartFile -> {
              String imageFile =
                  "data:image/"
                      + FilenameUtils.getExtension(multipartFile.getOriginalFilename())
                      + ";base64,"
                      + inputStreamToBase64(multipartFile);
              base64.add(imageFile);
              filenames.add(multipartFile.getOriginalFilename());
            });
    return HtmlUtils.replaceHtmlContent(htmlContent, base64, filenames);
  }

  private String inputStreamToBase64(MultipartFile multipartFile) {
    try {
      return convertInputStreamToBase64(multipartFile.getInputStream());
    } catch (IOException e) {
      log.error("Unable to convert file to base64 ", e);
    }
    return "";
  }
}
